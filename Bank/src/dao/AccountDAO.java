package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import controller.SessionManager;
import dbConn.util.CloseHelper;
import dbConn.util.ConnectionHelper;
import dto.AccountProductDto;
import dto.AccountShowDTO;
import dto.AccountSummaryDto;
import model.AccountDTO;
import model.AccountVO;
import model.MemberVO;

public class AccountDAO {

	public List<AccountSummaryDto> findAllAccount() {
		List<AccountSummaryDto> list = new ArrayList<>();
		String sql = "SELECT A.ACCOUNT_NO, M.NAME, A.ACCOUNT_PWD, A.BALANCE, A.STATUS, A.CREATED_DATE "
				+ "FROM ACCOUNT A JOIN MEMBER M ON A.MEMBER_NO = M.MEMBER_NO";

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			conn = ConnectionHelper.getConnection("mysql");
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				Timestamp ts = rs.getTimestamp("created_date");

				AccountSummaryDto accountSummartDto = new AccountSummaryDto();
				accountSummartDto.setAccountNo(rs.getString("account_no"));
				accountSummartDto.setName(rs.getString("name"));
				accountSummartDto.setAccountPwd(rs.getString("account_pwd"));
				accountSummartDto.setBalance(rs.getBigDecimal("balance"));
				accountSummartDto.setStatus(rs.getString("status").charAt(0));
				accountSummartDto.setCreatedDate(ts != null ? ts.toLocalDateTime() : null);

				list.add(accountSummartDto);
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

		return list;
	}

	public AccountVO findByAccountNo(String accountNo) {
		String sql = "SELECT * FROM ACCOUNT WHERE ACCOUNT_NO = ?";
		try (Connection conn = ConnectionHelper.getConnection("mysql");
				PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setString(1, accountNo);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				AccountVO account = new AccountVO();
				account.setAccountNo(rs.getString("account_no"));
				account.setMemberNo(rs.getInt("member_no"));
				account.setAccountPwd(rs.getString("account_pwd"));
				account.setBalance(rs.getBigDecimal("balance"));
				account.setStatus(rs.getString("status").charAt(0));
				account.setCreateDate(rs.getObject("created_date", LocalDateTime.class));
				return account;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public int updateStatus(String accountNo, char status) {
		String sql = "UPDATE account SET status = ? WHERE account_no = ?";
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = ConnectionHelper.getConnection("mysql");
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, String.valueOf(status));
			pstmt.setString(2, accountNo);
			int result = pstmt.executeUpdate();
			conn.commit();
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				if (conn != null)
					conn.rollback();
			} catch (SQLException rollbackEx) {
				rollbackEx.printStackTrace();
			}
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
		return 0;
	}

	public List<AccountVO> findAccountsByMemberNo(int memberNo) {
		List<AccountVO> list = new ArrayList<>();
		String sql = "SELECT * FROM account WHERE member_no = ?";
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = ConnectionHelper.getConnection("mysql");
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, memberNo);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				AccountVO account = new AccountVO();
				account.setAccountNo(rs.getString("account_no"));
				account.setMemberNo(rs.getInt("member_no"));
				account.setAccountPwd(rs.getString("account_pwd"));
				account.setBalance(rs.getBigDecimal("balance"));
				account.setStatus(rs.getString("status").charAt(0));
				account.setCreateDate(rs.getObject("created_date", LocalDateTime.class));
				list.add(account);
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
		return list;
	}

	public boolean createAccount(AccountVO dto) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		boolean flag = false;

		String sql = "INSERT INTO ACCOUNT (account_no, member_no, product_id, account_pwd,"
				+ " balance, status, lock_cnt, created_date, deposit_amount, maturity_date) "
				+ "VALUES (?, ?, ?, ?, ?, 'Y', 0, NOW(), ?, ?)";
		try {
			MemberVO currentUser = SessionManager.getCurrentUser();
			conn = ConnectionHelper.getConnection("mysql");
			conn.setAutoCommit(false);

			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, dto.getAccountNo());
			pstmt.setInt(2, currentUser.getMemberNo());
			pstmt.setInt(3, dto.getProduct_id());
			pstmt.setString(4, dto.getAccountPwd());
			pstmt.setBigDecimal(5, dto.getBalance());

			if (dto.getDeposit_amount() != null) {
				pstmt.setBigDecimal(6, dto.getDeposit_amount()); // 납입금액
			} else {
				pstmt.setNull(6, java.sql.Types.BIGINT);
			}

			if (dto.getAccountNo().substring(0, 3).equals("200") || dto.getAccountNo().substring(0, 3).equals("300")) {
				pstmt.setDate(7, Date.valueOf(dto.getMaturityDate().toLocalDate()));
			} else {
				pstmt.setNull(7, java.sql.Types.DATE);
			}
			int result = pstmt.executeUpdate();

			if (result > 0) {
				conn.commit();
				flag = true;
			} else {
				conn.rollback();
				flag = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			try {
				if (conn != null)
					conn.rollback();
			} catch (Exception rollbackEx) {
				rollbackEx.printStackTrace();
			}
		} finally {
			CloseHelper.close(pstmt);
			CloseHelper.close(conn);
		}
		return flag;
	}

	public List<AccountProductDto> findAccountProductByMemberNo(int memberNo) {

		List<AccountProductDto> list = new ArrayList<>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String sql = "SELECT A.account_no , P.product_name ,P.interest_rate ,A.maturity_date , A.balance  "
				+ "FROM ACCOUNT A " + "JOIN  PRODUCT P ON A.product_id = P.product_id " + "where A.member_no=?";

		try {
			conn = ConnectionHelper.getConnection("mysql");
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, memberNo);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				// 계좌번호, 상품이름, 이자율, 만기일, 잔액
				String accountNo = rs.getString("account_no");
				String productName = rs.getString("product_name");
				Number interestRate = rs.getBigDecimal("interest_rate");
				Date maturityDate = rs.getDate("maturity_date");
				Number balance = rs.getBigDecimal("balance");

				AccountProductDto dto = new AccountProductDto();
				dto.setAccountNo(accountNo);
				dto.setProductName(productName);
				dto.setInterestRate(interestRate);
				dto.setMaturityDate(maturityDate);
				dto.setBalance(balance);

				list.add(dto);

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			CloseHelper.close(pstmt);
			CloseHelper.close(conn);
		}
		return list;

	}

	public List<AccountProductDto> findYegeumJeoggeumByMemberNO(int memberNo) {
		List<AccountProductDto> list = new ArrayList<>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		// 200 적금 | 300 예금
		String sql = "SELECT A.account_no, P.product_name, P.interest_rate, A.maturity_date, A.balance "
				+ "FROM ACCOUNT A " + "JOIN PRODUCT P ON A.product_id = P.product_id " + "WHERE A.member_no = ? "
				+ "AND (P.product_type = 200 OR P.product_type = 300)";

		try {

			conn = ConnectionHelper.getConnection("mysql");
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, memberNo);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				// 계좌번호, 상품이름, 이자율, 만기일, 잔액
				String accountNo = rs.getString("account_no");
				String productName = rs.getString("product_name");
				Number interestRate = rs.getBigDecimal("interest_rate");
				Date maturityDate = rs.getDate("maturity_date");
				Number balance = rs.getBigDecimal("balance");

				AccountProductDto dto = new AccountProductDto();
				dto.setAccountNo(accountNo);
				dto.setProductName(productName);
				dto.setInterestRate(interestRate);
				dto.setMaturityDate(maturityDate);
				dto.setBalance(balance);

				list.add(dto);

			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			CloseHelper.close(pstmt);
			CloseHelper.close(conn);
		}
		return list;

	}

	/**
	 * 예금 적금 중도해지 중도해지 계좌의 금액을 입출금 계좌로 이체
	 * 
	 * @param accountNo
	 * @param ibchulAccountNo 
	 */
	public void cancelAccount(String accountNo, String ibchulAccountNo) {
		System.out.println("accountNo의 잔액을 ibChulAccountNo로 몽땅 이체하기");
	}

	/**
	 *입/출금 계좌 찾기 
	 * @param memberNo
	 */
	public List<AccountProductDto> finIbchulgeum(int memberNo) {
		
		List<AccountProductDto> list = new ArrayList<>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		// 100 = 입출
		String sql = "SELECT A.account_no, P.product_name, P.interest_rate, A.maturity_date, A.balance "
				+ "FROM ACCOUNT A " + "JOIN PRODUCT P ON A.product_id = P.product_id " + "WHERE A.member_no = ? "
				+ "AND P.product_type = 100";

		try {

			conn = ConnectionHelper.getConnection("mysql");
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, memberNo);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				// 계좌번호, 상품이름, 이자율, 만기일, 잔액
				String accountNo = rs.getString("account_no");
				String productName = rs.getString("product_name");
				Number interestRate = rs.getBigDecimal("interest_rate");
				Date maturityDate = rs.getDate("maturity_date");
				Number balance = rs.getBigDecimal("balance");

				AccountProductDto dto = new AccountProductDto();
				dto.setAccountNo(accountNo);
				dto.setProductName(productName);
				dto.setInterestRate(interestRate);
				dto.setMaturityDate(maturityDate);
				dto.setBalance(balance);

				list.add(dto);

			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			CloseHelper.close(pstmt);
			CloseHelper.close(conn);
		}
		return list;
	}
}
	
	public List<AccountShowDTO> showMyAccountDepositWithdraw() {
		String sql = "select ROW_NUMBER() OVER (ORDER BY account_no) AS 번호, account_no, balance, p.product_name from account a\n"
				+ "join member b on a.member_no = b.member_no\n"
				+ "join product p on a.product_id = p.product_id\n"
				+ "where p.product_type = 100 and a.member_no = ?;";
		List<AccountShowDTO> list = new ArrayList<>();
		MemberVO currentUser = SessionManager.getCurrentUser();
		
		try (Connection conn = ConnectionHelper.getConnection("mysql");
				PreparedStatement pstmt = conn.prepareStatement(sql)) {
			
			pstmt.setInt(1, currentUser.getMemberNo());
			ResultSet rs = pstmt.executeQuery();
			
			while (rs.next()) {
				int accountNum = rs.getInt("번호");
				String accountNo = rs.getString("account_no");
				String productName = rs.getString("product_name");
				long balance = rs.getLong("balance");
				
				list.add(new AccountShowDTO(accountNum, accountNo, productName, balance));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}
	public AccountShowDTO selectMyAccountDepositWithdraw(String account_no) {
		String sql = "select ROW_NUMBER() OVER (ORDER BY account_no) AS 번호, account_no, balance, p.product_name from account a\n"
				+ "join member b on a.member_no = b.member_no\n"
				+ "join product p on a.product_id = p.product_id\n"
				+ "where p.product_type = 100 and a.account_no = ?;";
		AccountShowDTO dto = new AccountShowDTO();
		
		try (Connection conn = ConnectionHelper.getConnection("mysql");
				PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, account_no);
			ResultSet rs = pstmt.executeQuery();
			
			if(rs.next()) {
				dto.setAccountNum(rs.getInt("번호"));
				dto.setAccountNo(rs.getString("account_no"));
				dto.setBalance(rs.getLong("balance"));
				dto.setProductName(rs.getString("product_name"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return dto;
	}
	public List<AccountShowDTO> showMyAccounts() {
		String sql = "select ROW_NUMBER() OVER (ORDER BY account_no) AS 번호, account_no, balance, p.product_name from account a\n"
				+ "join member b on a.member_no = b.member_no\n"
				+ "join product p on a.product_id = p.product_id\n"
				+ "where a.member_no = ?;";
		List<AccountShowDTO> list = new ArrayList<>();
		MemberVO currentUser = SessionManager.getCurrentUser();
		
		try (Connection conn = ConnectionHelper.getConnection("mysql");
				PreparedStatement pstmt = conn.prepareStatement(sql)) {
			
			pstmt.setInt(1, currentUser.getMemberNo());
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				
				int accountNum = rs.getInt("번호");
				String accountNo = rs.getString("account_no");
	            String productName = rs.getString("product_name");
	            long balance = rs.getLong("balance");
				
				list.add(new AccountShowDTO(accountNum, accountNo, productName, balance));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	
	public AccountVO getPwdAndStatus(String accountNo) {
        String sql = "SELECT account_pwd, status, lock_cnt FROM account WHERE account_no = ?";
        try (Connection conn = ConnectionHelper.getConnection("mysql");
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
        	pstmt.setString(1, accountNo);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                AccountVO account = new AccountVO();
                account.setAccountNo(accountNo);
                account.setAccountPwd(rs.getString("account_pwd"));
                account.setStatus(rs.getString("status").charAt(0));
                account.setLock_cnt(rs.getInt("lock_cnt"));
                return account;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void updateLockCnt(String accountNo, int lockCnt) {
        String sql = "UPDATE account SET lock_cnt = ? WHERE account_no = ?";
        try (Connection conn = ConnectionHelper.getConnection("mysql");
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, lockCnt);
            pstmt.setString(2, accountNo);
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void lockAccount(String accountNo) {
        String sql = "UPDATE account SET status = 'N' WHERE account_no = ?";
        try (Connection conn = ConnectionHelper.getConnection("mysql");
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, accountNo);
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void resetLockCnt(String accountNo) {
        updateLockCnt(accountNo, 0);
    }
    
    /**
     * 어드민에서 상품이 계좌에서 사용중인지 확인
     * 
     * @param productId
     * @return
     */
    public boolean isProductInUse(int productId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean inUse = false;
        
        String sql = "SELECT COUNT(*) FROM ACCOUNT WHERE product_id = ?";
        
        try {
            conn = ConnectionHelper.getConnection("mysql");
            pstmt = conn.prepareStatement(sql);
            
            pstmt.setInt(1, productId);
            
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                int count = rs.getInt(1);
                inUse = (count > 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return inUse;
    }
}
