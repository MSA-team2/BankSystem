package dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import controller.SessionManager;
import dbConn.util.CloseHelper;
import dbConn.util.ConnectionHelper;
import dto.DailyTransferSummaryDto;
import dto.TransactionDTO;
import model.domain.Member;
import model.domain.TransactionHistory;

public class TransactionDAO {

	public DailyTransferSummaryDto getDailySummary(LocalDate date) {
		String sql = "SELECT TRANSACTION_TYPE, " + "       COUNT(*) AS COUNT, " + "       SUM(AMOUNT) AS AMOUNT, "
				+ "       SUM(CASE " + "            WHEN TRANSACTION_TYPE = 'DEPOSIT' THEN AMOUNT "
				+ "            WHEN TRANSACTION_TYPE = 'WITHDRAW' THEN -AMOUNT " + "            ELSE 0 "
				+ "       END) AS NET_AMOUNT " + "FROM TRANSACTION_HISTORY "
				+ "WHERE TRANSACTION_DATE >= ? AND TRANSACTION_DATE < ? " + "GROUP BY TRANSACTION_TYPE";

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
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
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

	// 입, 출, 이체
	public boolean transfer(TransactionDTO dto) {
		String sql = "UPDATE account SET balance = balance + ? WHERE account_no = ?";
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = ConnectionHelper.getConnection("mysql");
			pstmt = conn.prepareStatement(sql);

			pstmt.setBigDecimal(1, dto.getAmount());
			pstmt.setString(2, dto.getAccountNo());
			return pstmt.executeUpdate() > 0;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	// 거래내역 저장
	public void saveTransaction(TransactionDTO transaction) {
		String sql = "INSERT INTO TRANSACTION_HISTORY (account_no, transaction_type, amount, transaction_date, target_account) "
				+ "VALUES (?, ?, ?, NOW(), ?)";

		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = ConnectionHelper.getConnection("mysql");
			pstmt = conn.prepareStatement(sql);

			pstmt.setString(1, transaction.getAccountNo());
			pstmt.setString(2, transaction.getTransactionType());
			pstmt.setBigDecimal(3, transaction.getAmount());
//	            pstmt.setObject(4, transaction.getTransactionDate());
			pstmt.setString(4, transaction.getTargetAccount());
			pstmt.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

	// 거래내역 가져오기
	public List<TransactionHistory> TransactionHistory(String accountNo) {
		List<TransactionHistory> transactions = new ArrayList<>();
		String sql = "SELECT \n" + "t.transaction_date AS 날짜, \n" + "a.account_no AS 계좌번호,\n"
				+ "p.product_name AS 상품명,\n" + "CASE \n" + "WHEN t.transaction_type = 'DEPOSIT' THEN '입금'\n"
				+ "WHEN t.transaction_type = 'WITHDRAW' THEN '출금'\n"
				+ "        WHEN t.transaction_type = 'TRANSFER' THEN '이체'\n" + "        ELSE t.transaction_type\n"
				+ "    END AS 구분, \n" + "    t.amount AS 금액, \n"
//					+ "		a.balance AS 잔액,\n"
				+ "    CASE \n" + "        WHEN t.transaction_type = 'TRANSFER' THEN \n"
				+ "            (SELECT m2.name \n" + "             FROM MEMBER m2 \n"
				+ "             JOIN ACCOUNT a2 ON m2.member_no = a2.member_no \n"
				+ "             WHERE a2.account_no = t.target_account)\n" +  "ELSE m.name\n" + "    END AS 상대방\n"
				+ "FROM \n" + "    TRANSACTION_HISTORY t\n" + "JOIN \n"
				+ "    ACCOUNT a ON t.account_no = a.account_no\n" + "JOIN \n"
				+ "    PRODUCT p ON a.product_id = p.product_id\n" + "JOIN \n"
				+ "    MEMBER m ON a.member_no = m.member_no\n" + "WHERE \n"
				+ "    m.member_no = ? and a.account_no = ?\n" + "ORDER BY \n" + "    t.transaction_date DESC";

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = ConnectionHelper.getConnection("mysql");
			pstmt = conn.prepareStatement(sql);
			Member currentUser = SessionManager.getCurrentUser();

			pstmt.setInt(1, currentUser.getMemberNo());
			pstmt.setString(2, accountNo);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				TransactionHistory transaction = new TransactionHistory(rs.getObject("날짜", LocalDateTime.class),
						rs.getString("구분"), rs.getBigDecimal("금액"), rs.getString("상대방"));

				transactions.add(transaction);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return transactions;
	}

	// 한 달 최대 적금납부금액
	public BigDecimal maxMonthlyDeposit(String accountNo) throws SQLException {
		String sql = "SELECT p.max_monthly_deposit\n" + "FROM account a\n"
				+ "JOIN product p ON a.product_id = p.product_id\n" + "WHERE a.account_no = ?";

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = ConnectionHelper.getConnection("mysql");
			pstmt = conn.prepareStatement(sql);

			pstmt.setString(1, accountNo);
			rs = pstmt.executeQuery();

			if (rs.next())
				return rs.getBigDecimal(1);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return BigDecimal.ZERO;
	}

	// 한 달 적금 납부한 총액
	public BigDecimal monthlyDepositAmount(String accountNo) throws SQLException {
		String sql = "select sum(t.amount) \n" + "from transaction_history t\n" + "where t.account_no = ? \n"
				+ "and t.transaction_type = 'TRANSFER'\n" + "and year(t.transaction_date) = year(curdate())\n"
				+ "and month(t.transaction_date) = month(curdate());";

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = ConnectionHelper.getConnection("mysql");
			pstmt = conn.prepareStatement(sql);

			pstmt.setString(1, accountNo);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				BigDecimal result = rs.getBigDecimal(1);
				return result != null ? result : BigDecimal.ZERO;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return BigDecimal.ZERO;
	}

	// 이체 시, 상품타입을 가져와 검사함. 100 or 200
	public int productType(String accountNo) {
		String sql = "select p.product_type from product p \n" + "join account a on p.product_id = a.product_id\n"
				+ "where a.account_no = ?";

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = ConnectionHelper.getConnection("mysql");
			pstmt = conn.prepareStatement(sql);

			pstmt.setString(1, accountNo);
			rs = pstmt.executeQuery();

			if (rs.next())
				return rs.getInt("product_type");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return 0;
	}

	// 잔액 가져오기
	public BigDecimal balance(String accountNo) throws SQLException {
		String sql = "select balance from account where account_no = ?";

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = ConnectionHelper.getConnection("mysql");
			pstmt = conn.prepareStatement(sql);

			pstmt.setString(1, accountNo);
			rs = pstmt.executeQuery();

			if (rs.next())
				return rs.getBigDecimal(1);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return BigDecimal.ZERO;
	}

	// 입, 출 계좌 확인
	public boolean checkAccountNo(String accountNo) {
		String sql = "select member_no, account_no from account a\n" + "join product p on a.product_id = p.product_id\n"
				+ "where member_no = ? and account_no = ? and p.product_type = '100'";

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = ConnectionHelper.getConnection("mysql");
			pstmt = conn.prepareStatement(sql);

			Member currentUser = SessionManager.getCurrentUser();
			pstmt.setInt(1, currentUser.getMemberNo());
			pstmt.setString(2, accountNo);
			rs = pstmt.executeQuery();

			return rs.next();
		} catch (SQLException e) {
//			e.printStackTrace();
			return false;
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

	// 이체 할 계좌 확인
	public boolean targetCheckAccountNo(String accountNo) {
		String sql = "select account_no from account a\n" + "join product p on a.product_id = p.product_id\n"
				+ "where account_no = ? and (product_type = '100' or product_type = '200')";

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = ConnectionHelper.getConnection("mysql");
			pstmt = conn.prepareStatement(sql);

			pstmt.setString(1, accountNo);
			rs = pstmt.executeQuery();

			return rs.next();
		} catch (SQLException e) {
//			e.printStackTrace();
			return false;
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

	// 거래내역 계좌 확인
	public boolean transactionCheckAccountNo(String accountNo) {
		String sql = "select member_no, account_no from account\n" + "where member_no = ? and account_no = ?";

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = ConnectionHelper.getConnection("mysql");
			pstmt = conn.prepareStatement(sql);

			Member currentUser = SessionManager.getCurrentUser();
			pstmt.setInt(1, currentUser.getMemberNo());
			pstmt.setString(2, accountNo);
			rs = pstmt.executeQuery();

			return rs.next();
		} catch (SQLException e) {
//			e.printStackTrace();
			return false;
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	// 계좌 해지(예/적금 전액 -> 입출금)
	public void transferAllBalanceAndCloseAndDelete(String targetAccountNo, String depositAccountNo,
			Number targetBalance) {
		Connection conn = null;
		PreparedStatement pstmt1 = null; // 잔액 0 처리
		PreparedStatement pstmt2 = null; // 입출금 계좌 금액 추가
		PreparedStatement pstmt3 = null; // 계좌 삭제

		String setZeroSql = "UPDATE ACCOUNT SET balance = 0 WHERE account_no = ?";
		String depositSql = "UPDATE ACCOUNT SET balance = balance + ? WHERE account_no = ?";
		String deleteAccountSql = "DELETE FROM ACCOUNT WHERE account_no = ?";

		try {
			conn = ConnectionHelper.getConnection("mysql");
			conn.setAutoCommit(false); // 트랜잭션 시작

			// 1. 해지 계좌 balance = 0
			pstmt1 = conn.prepareStatement(setZeroSql);
			pstmt1.setString(1, targetAccountNo);
			pstmt1.executeUpdate();

			// 2. 입출금 계좌에 금액 추가
			pstmt2 = conn.prepareStatement(depositSql);
			pstmt2.setBigDecimal(1, new BigDecimal(targetBalance.toString()));
			pstmt2.setString(2, depositAccountNo);
			pstmt2.executeUpdate();

			// 3. 해지 계좌 DB 삭제
			pstmt3 = conn.prepareStatement(deleteAccountSql);
			pstmt3.setString(1, targetAccountNo);
			pstmt3.executeUpdate();

			conn.commit(); // 전체 커밋
			System.out.println("전액 이체 및 해지 계좌 삭제가 완료되었습니다.");

		} catch (Exception e) {
			e.printStackTrace();
			try {
				if (conn != null)
					conn.rollback();
			} catch (SQLException rollbackEx) {
				rollbackEx.printStackTrace();
			}
		} finally {
			CloseHelper.close(pstmt1);
			CloseHelper.close(pstmt2);
			CloseHelper.close(pstmt3);
			CloseHelper.close(conn);
		}
	}


}
