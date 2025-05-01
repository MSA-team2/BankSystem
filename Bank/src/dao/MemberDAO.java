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
	
	public MemberVO findMemberByNameAndJumin(String name, String jumin) {
        String sql = "SELECT * FROM MEMBER WHERE NAME = ? AND JUMIN = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        MemberVO member = null;
        
        try {
        	conn = ConnectionHelper.getConnection("mysql");
        	
        	pstmt = conn.prepareStatement(sql);
        	pstmt.setString(1, name);
        	pstmt.setString(2, jumin);
        	
        	rs = pstmt.executeQuery();
        	
        	
            if (rs.next()) {
            	member = new MemberVO();
            	
                member.setName(rs.getString("name"));
                member.setJumin(rs.getString("jumin"));
                member.setMemberId(rs.getString("member_id"));
                member.setPassword(rs.getString("password"));
                member.setAddress(rs.getString("address"));
                member.setPhone(rs.getString("phone"));
                member.setStatus(rs.getString("status").charAt(0));
                member.setLockCnt(rs.getInt("lock_cnt"));
                member.setRole(rs.getInt("role"));
            }
            
        } catch(SQLException e) {
        	e.printStackTrace();
        } finally {
        	try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
	        	if(conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
        }
        return member;
    }
	
	public int updateMemberInfo(int memberNo, String phone, String address) {
        String sql = "UPDATE member SET phone = ?, address = ? WHERE member_no = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
        	conn = ConnectionHelper.getConnection("mysql");
        	conn.setAutoCommit(false);
        	pstmt = conn.prepareStatement(sql);
        	
            pstmt.setString(1, phone);
            pstmt.setString(2, address);
            pstmt.setInt(3, memberNo);
            int result = pstmt.executeUpdate();
            
            conn.commit();
            
            return result;
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
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }
	
}
