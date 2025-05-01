package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dbConn.util.CloseHelper;
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
	
	public int insertMember(Connection conn, MemberVO mv) {
		int result = 0;
		PreparedStatement ps = null;
		
		String sql = "INSERT INTO MEMBER (name, jumin, member_id, password, address, phone)"
				+ "VALUES(?, ?, ?, ?, ?, ?)";
		try {
			conn = ConnectionHelper.getConnection("mysql");
			ps = conn.prepareStatement(sql);
			
			ps = conn.prepareStatement(sql);
			ps.setString(1, mv.getName());
			ps.setString(2, mv.getJumin());
			ps.setString(3, mv.getMemberId());
			ps.setString(4, mv.getPassword());
			ps.setString(5, mv.getAddress());
			ps.setString(6, mv.getPhone());
			
			result = ps.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			CloseHelper.close(ps);
			CloseHelper.close(conn);
		}
	    return result;
	}
	
	
	public int selectByMemberIdPwd(Connection conn, String id, String pwd) {
		int result = 0;
		MemberVO mv = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		String sql = "SELECT * FROM MEMBER WHERE member_id = ? AND password = ?";
		try {
			conn = ConnectionHelper.getConnection("mysql");
			ps.setString(1, id);
            ps.setString(2, pwd);
            
            rs = ps.executeQuery();
			
			if (rs.next()) {
//				mv = new MemberVO(rs.getString("member_id"), rs.getString("password"));
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			CloseHelper.close(rs);
			CloseHelper.close(ps);
			CloseHelper.close(conn);
		}
	    return result;
		
	}

	public int updatePwd(Connection conn, MemberVO mv) {
		int result = 0;
		PreparedStatement ps = null;
		String sql = "UPDATE MEMBER SET password = ? WHERE member_id = ?";
		
		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, mv.getPassword());
        	ps.setString(2, mv.getMemberId());
        	
			result = ps.executeUpdate();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			CloseHelper.close(ps);
			CloseHelper.close(conn);
		}
		return result;
	}

	
	// 아이디 중복 체크
	
	
	
	// 전화번호 중복 체크
	
	
	
	
}
