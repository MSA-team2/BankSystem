package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;

import controller.SessionManager;
import dbConn.util.CloseHelper;
import dbConn.util.ConnectionSingletonHelper;
import model.AccountVO;
import model.MemberVO;

public class AccountDAO {

	public boolean createAccount(AccountVO dto) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		boolean flag = false;
		
		String sql = "INSERT INTO ACCOUNT (account_no, member_no, product_id, account_pwd,"
				+ " balance, status, lock_cnt, created_date, deposit_amount, maturity_date) "
				+ "VALUES (?, ?, ?, ?, ?, 'Y', 0, NOW(), ?, ?)";
		try {
			MemberVO currentUser = SessionManager.getCurrentUser();
			conn = ConnectionSingletonHelper.getConnection("mysql");
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
				pstmt.setDate(7, Date.valueOf(dto.getMaturity_date()));
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
}
