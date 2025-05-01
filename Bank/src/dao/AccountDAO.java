package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import dbConn.util.ConnectionSingletonHelper;
import dto.AccountSummaryDto;

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

	            AccountSummaryDto dto = AccountSummaryDto.builder()
	                    .accountNo(rs.getString("account_no"))
	                    .name(rs.getString("name"))
	                    .accountPwd(rs.getString("account_pwd"))
	                    .balance(rs.getBigDecimal("balance"))
	                    .status(rs.getString("status").charAt(0))
	                    .createdDate(ts != null ? ts.toLocalDateTime() : null)
	                    .build();

	            list.add(dto);
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
}
