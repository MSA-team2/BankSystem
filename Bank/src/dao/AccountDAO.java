package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import dbConn.util.ConnectionHelper;
import dbConn.util.ConnectionSingletonHelper;
import dto.AccountSummaryDto;
import model.AccountVO;

public class AccountDAO {

	public List<AccountSummaryDto> findAllAccount() {
	    List<AccountSummaryDto> list = new ArrayList<>();
	    String sql = "SELECT A.ACCOUNT_NO, M.NAME, A.ACCOUNT_PWD, A.BALANCE, A.STATUS, A.CREATED_DATE " +
	                 "FROM ACCOUNT A JOIN MEMBER M ON A.MEMBER_NO = M.MEMBER_NO";

	    Connection conn = null;
	    PreparedStatement pstmt = null;
	    ResultSet rs = null;

	    try {
	        conn = ConnectionSingletonHelper.getConnection("mysql");
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
	            if (rs != null) rs.close();
	            if (pstmt != null) pstmt.close();
	            if (conn != null) conn.close();
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }

	    return list;
	}

	public AccountVO findByAccountNo(String accountNo) {
		String sql = "SELECT * FROM account WHERE account_no = ?";
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
  
}
