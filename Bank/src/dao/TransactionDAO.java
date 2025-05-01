package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;

import dbConn.util.ConnectionHelper;
import dto.DailyTransferSummaryDto;

public class TransactionDAO {
	
	public DailyTransferSummaryDto getDailySummary(LocalDate date) {
        String sql = "SELECT TRANSACTION_TYPE, " +
                     "       COUNT(*) AS COUNT, " +
                     "       SUM(AMOUNT) AS AMOUNT, " +
                     "       SUM(CASE " +
                     "            WHEN TRANSACTION_TYPE = 'DEPOSIT' THEN AMOUNT " +
                     "            WHEN TRANSACTION_TYPE = 'WITHDRAW' THEN -AMOUNT " +
                     "            ELSE 0 " +
                     "       END) AS NET_AMOUNT " +
                     "FROM TRANSACTION_HISTORY " +
                     "WHERE TRANSACTION_DATE >= ? AND TRANSACTION_DATE < ? " +
                     "GROUP BY TRANSACTION_TYPE";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        int depositCount = 0;
        int depositAmount = 0;
        int withdrawCount = 0;
        int withdrawAmount = 0;
        int netTotalAmount = 0;

        try {
            conn = ConnectionHelper.getConnection("mysql");
            conn.setAutoCommit(false);

            pstmt = conn.prepareStatement(sql);
            pstmt.setTimestamp(1, Timestamp.valueOf(date.atStartOfDay())); // 2025-04-24 00:00:00
            pstmt.setTimestamp(2, Timestamp.valueOf(date.plusDays(1).atStartOfDay())); // 2025-04-25 00:00:00
            rs = pstmt.executeQuery();

            while (rs.next()) {
                String type = rs.getString("transaction_type");
                int count = rs.getInt("count");
                int amount = rs.getInt("amount");
                int net = rs.getInt("net_amount");

                if ("DEPOSIT".equals(type)) {
                    depositCount = count;
                    depositAmount = amount;
                } else if ("WITHDRAW".equals(type)) {
                    withdrawCount = count;
                    withdrawAmount = amount;
                }

                netTotalAmount += net;
            }

            conn.commit();

        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                    System.out.println("[!] 트랜잭션 롤백되었습니다.");
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        DailyTransferSummaryDto dailyTransferSummaryDto = new DailyTransferSummaryDto();
        dailyTransferSummaryDto.setDate(date);
        dailyTransferSummaryDto.setDepositCount(depositCount);
        dailyTransferSummaryDto.setDepositAmount(depositAmount);
        dailyTransferSummaryDto.setWithdrawCount(withdrawCount);
        dailyTransferSummaryDto.setWithdrawAmount(withdrawAmount);
        dailyTransferSummaryDto.setNetTotalAmount(netTotalAmount);
        
        return dailyTransferSummaryDto;
    }
}
