package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import controller.SessionManager;
import dbConn.util.ConnectionHelper;
import dto.DailyTransferSummaryDto;
import dto.TransactionDTO;
import model.MemberVO;
import model.TransactionHistoryVO;

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
	
	public boolean deposit(TransactionDTO dto) {
		 String sql = "UPDATE account SET balance = balance + ? WHERE account_no = ?";
	        try (Connection conn = ConnectionHelper.getConnection("mysql");
	        	PreparedStatement pstmt = conn.prepareStatement(sql)) {
	            pstmt.setBigDecimal(1, dto.getAmount());
	            pstmt.setString(2, dto.getAccountNo());
	            return pstmt.executeUpdate() > 0;
	        }catch(Exception e) {
	        	e.printStackTrace();
	        }
		return false;
	}
	public boolean withdraw(TransactionDTO dto) {
		String sql = "UPDATE account SET balance = balance - ? WHERE account_no = ?";
		try (Connection conn = ConnectionHelper.getConnection("mysql");
				PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setBigDecimal(1, dto.getAmount());
			pstmt.setString(2, dto.getAccountNo());
			return pstmt.executeUpdate() > 0;
		}catch(Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	// 거래내역 저장 
	public void saveTransaction(TransactionDTO transaction) {
		String sql = "INSERT INTO TRANSACTION_HISTORY (account_no, transaction_type, amount, transaction_date, target_account) " +
                "VALUES (?, ?, ?, NOW(), ?)";	
		
		 try (	Connection conn = ConnectionHelper.getConnection("mysql");
	            PreparedStatement pstmt = conn.prepareStatement(sql)) {
	            
	            pstmt.setString(1, transaction.getAccountNo());
	            pstmt.setString(2, transaction.getTransactionType());
	            pstmt.setBigDecimal(3, transaction.getAmount());
//	            pstmt.setObject(4, transaction.getTransactionDate());
	            pstmt.setString(4, transaction.getTargetAccount());
	            
	            pstmt.executeUpdate();
	            
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
		
	}
	
	//거래내역 가져오기
	public List<TransactionHistoryVO> getTransactionHistory(String accountNo) {
	        List<TransactionHistoryVO> transactions = new ArrayList<>();
	        String sql = "SELECT \n"
					+ "t.transaction_date AS 날짜, \n"
					+ "a.account_no AS 계좌번호,\n"
					+ "p.product_name AS 상품명,\n"
					+ "CASE \n"
					+ "WHEN t.transaction_type = 'DEPOSIT' THEN '입금'\n"
					+ "WHEN t.transaction_type = 'WITHDRAW' THEN '출금'\n"
					+ "        WHEN t.transaction_type = 'TRANSFER' THEN '이체'\n"
					+ "        ELSE t.transaction_type\n"
					+ "    END AS 구분, \n"
					+ "    t.amount AS 금액, \n"
//					+ "		a.balance AS 잔액,\n"
					+ "    CASE \n"
					+ "        WHEN t.transaction_type = 'TRANSFER' THEN \n"
					+ "            (SELECT m2.name \n"
					+ "             FROM MEMBER m2 \n"
					+ "             JOIN ACCOUNT a2 ON m2.member_no = a2.member_no \n"
					+ "             WHERE a2.account_no = t.target_account)\n"
					+ "        ELSE '본인'\n"
					+ "    END AS 상대방\n"
					+ "FROM \n"
					+ "    TRANSACTION_HISTORY t\n"
					+ "JOIN \n"
					+ "    ACCOUNT a ON t.account_no = a.account_no\n"
					+ "JOIN \n"
					+ "    PRODUCT p ON a.product_id = p.product_id\n"
					+ "JOIN \n"
					+ "    MEMBER m ON a.member_no = m.member_no\n"
					+ "WHERE \n"
					+ "    m.member_no = ? and a.account_no = ?\n"
					+ "ORDER BY \n"
					+ "    t.transaction_date DESC";
	        
	        try (Connection conn = ConnectionHelper.getConnection("mysql");
	             PreparedStatement pstmt = conn.prepareStatement(sql)) {
	    		MemberVO currentUser = SessionManager.getCurrentUser();
	            pstmt.setInt(1, currentUser.getMemberNo());
	            pstmt.setString(2, accountNo);
	            ResultSet rs = pstmt.executeQuery();
	            
	            while (rs.next()) {
	            	TransactionHistoryVO transaction = new TransactionHistoryVO(
	            			rs.getObject("날짜", LocalDateTime.class),
	            	        rs.getString("구분"),
	            	        rs.getBigDecimal("금액"),
	            	        rs.getString("상대방")
	                );
	                
	                transactions.add(transaction);
	            }
	            
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	        
	        return transactions;
	    }
	
	
	
}
