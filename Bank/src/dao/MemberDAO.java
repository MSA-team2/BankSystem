package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dbConn.util.ConnectionHelper;
import model.MemberVO;

public class MemberDAO {
	
	public List<MemberVO> findAllMembers() {
        List<MemberVO> list = new ArrayList<>();
        String sql = "SELECT * FROM MEMBER ORDER BY ROLE DESC";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
        	conn = ConnectionHelper.getConnection("mysql");
        	pstmt = conn.prepareStatement(sql);
        	rs = pstmt.executeQuery();

            while (rs.next()) {
                MemberVO member = new MemberVO();
                member.setName(rs.getString("name"));
                member.setJumin(rs.getString("jumin"));
                member.setMemberId(rs.getString("member_id"));
                member.setPassword(rs.getString("password"));
                member.setAddress(rs.getString("address"));
                member.setPhone(rs.getString("phone"));
                member.setStatus(rs.getString("status").charAt(0));
                member.setLockCnt(rs.getInt("lock_cnt"));
                member.setRole(rs.getInt("role"));
                
                list.add(member);
            }
        } catch(SQLException e) {
        	e.printStackTrace();
        } finally {
        	try {
				rs.close();
				pstmt.close();
	        	conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
        }
        return list;
    }
	
	
}
