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

	// 회원가입
	public int insertMember(Connection conn, MemberVO member) {
		// insert => 처리된 행 수
		PreparedStatement ps = null;
        String sql = "INSERT INTO MEMBER (name, jumin, member_id, password, address, phone)"
        			+ "VALUES (?, ?, ?, ?, ?, ?)";
        
        try {
        	ps = conn.prepareStatement(sql);
            ps.setString(1, member.getName());
            ps.setString(2, member.getJumin());
            ps.setString(3, member.getMemberId());
            ps.setString(4, member.getPassword());
            ps.setString(5, member.getAddress());
            ps.setString(6, member.getPhone());
            
            return ps.executeUpdate();
        } catch (SQLException e) {
			e.printStackTrace();
			return 0;
		} finally {
			CloseHelper.close(ps);
		}
    }
	
	// 로그인
	public MemberVO loginMember(Connection conn, String id, String pwd) {
		// select문 => ResultSet객체 => Member객체
		PreparedStatement ps = null;
		ResultSet rs = null;
        String sql = "SELECT * FROM MEMBER WHERE member_id = ? AND password = ?";
        try {
        	ps = conn.prepareStatement(sql);
            ps.setString(1, id);
            ps.setString(2, pwd);
            
            rs = ps.executeQuery();
            if (rs.next()) {
                return new MemberVO(
                    rs.getInt("member_no"),
                    rs.getString("name"),
                    rs.getString("jumin"),
                    rs.getString("member_id"),
                    rs.getString("password"),
                    rs.getString("address"),
                    rs.getString("phone"),
                    rs.getString("status").charAt(0),
                    rs.getInt("lock_cnt"),
                    rs.getInt("role")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
        	CloseHelper.close(rs);
        	CloseHelper.close(ps);
        }
        return null;
    }

	// 아이디 찾기
	public String findMemberId(Connection conn, String name, String jumin) {
		PreparedStatement ps = null;
		ResultSet rs = null;
        String sql = "SELECT member_id FROM MEMBER WHERE name = ? AND jumin = ?";
        try {
        	ps = conn.prepareStatement(sql);
            ps.setString(1, name);
            ps.setString(2, jumin);
            
            rs = ps.executeQuery();
            if (rs.next()) return rs.getString("member_id");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
			CloseHelper.close(rs);
			CloseHelper.close(ps);
		}
        return null;
    }

	// 비밀번호 찾기 -> 새 비밀번호 변경
    public int updatePwd(Connection conn, String id, String name, String jumin, String newPwd) {
    	// update문 => 처리된 행수(int)
    	PreparedStatement ps = null;
        String sql = "UPDATE MEMBER SET password = ? WHERE member_id = ? AND name = ? AND jumin = ?";
        try {
        	ps = conn.prepareStatement(sql);
            ps.setString(1, newPwd);
            ps.setString(2, id);
            ps.setString(3, name);
            ps.setString(4, jumin);
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
			CloseHelper.close(ps);
		}
    }

    // 아이디 중복 체크
    public boolean checkId(Connection conn, String id) {
    	PreparedStatement ps = null;
		ResultSet rs = null;
        String sql = "SELECT member_id FROM MEMBER WHERE member_id = ?";
        try {
        	ps = conn.prepareStatement(sql);
            ps.setString(1, id);
            
            rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return true;
        } finally {
			CloseHelper.close(rs);
			CloseHelper.close(ps);
		}
    }

    // 전화번호 중복 체크
    public boolean confirmPhone(Connection conn, String phone) {
    	PreparedStatement ps = null;
		ResultSet rs = null;
        String sql = "SELECT phone FROM MEMBER WHERE phone = ?";
        try {
        	ps = conn.prepareStatement(sql);
            ps.setString(1, phone);
            
            rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return true;
        } finally {
			CloseHelper.close(rs);
			CloseHelper.close(ps);
		}
    }

    // 비밀번호 변경시 입력 정보 확인
	public boolean validateUserInfo(Connection conn, String id, String name, String jumin) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "SELECT 1 FROM MEMBER WHERE member_id = ? AND name = ? AND jumin = ?";
		try {
			ps = conn.prepareStatement(sql);
            ps.setString(1, id);
            ps.setString(2, name);
            ps.setString(3, jumin);
            
            rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
			CloseHelper.close(rs);
			CloseHelper.close(ps);
		}
	}
	
	// 틀린횟수 증가 (int?)
	public void incrementLockCount(Connection conn, String id) {
		// TODO Auto-generated method stub
	}
	
	// 틀린횟수 조회
	public int getLockCount(Connection conn, String id) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	// 잠금 상태 변경 'Y' -> 'N'
	public void lockAccount(MemberDAO md, String id) {
		// TODO Auto-generated method stub
	}
	
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
	
	/**
	 * 
	 * @param 이름
	 * @param 주민번호
	 * @return
	 */
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
            	
            	member.setMemberNo(rs.getInt("member_no"));
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
	
	/**
	 * 어드민 컨트롤러에서 회원 정보 수정 시 사용하는 메서드
	 * 
	 * @param 회원번호
	 * @param 전화번호
	 * @param 주소
	 * @return 업데이트
	 */
	public int updateMemberInfo(int memberNo, String phone, String address) {
        String sql = "UPDATE MEMBER SET PHONE = ?, ADDRESS = ? WHERE MEMBER_NO = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        int result = 0;
        
        try {
        	conn = ConnectionHelper.getConnection("mysql");
        	conn.setAutoCommit(false);
        	pstmt = conn.prepareStatement(sql);
        	
            pstmt.setString(1, phone);
            pstmt.setString(2, address);
            pstmt.setInt(3, memberNo);
            result = pstmt.executeUpdate();
            
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
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
	
	/**
	 * 어드민 잠금 계정 조회 메서드
	 * @return List<MemberVO>
	 */
	public List<MemberVO> getLockedAccounts() {
		List<MemberVO> list = new ArrayList<>();
        String sql = "SELECT * FROM MEMBER WHERE STATUS = 'N'";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
        	conn = ConnectionHelper.getConnection("mysql");
        	pstmt = conn.prepareStatement(sql);
        	rs = pstmt.executeQuery();

            while (rs.next()) {
                MemberVO member = new MemberVO();
                member.setMemberNo(rs.getInt("member_no"));
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
        		if (rs != null) rs.close();
        		if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
        }
        return list;
	}
	
	/**
	 * 어드민 계정 상태 Y 변경 메서드
	 * 
	 * @param 회원번호
	 * @return 업데이트
	 */
	public int updateAccountStatus(int memberNo, char status) {
        String sql = "UPDATE MEMBER SET STATUS = ?, LOCK_CNT = 0 WHERE MEMBER_NO = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        int result = 0;
        
        try {
        	conn = ConnectionHelper.getConnection("mysql");
        	conn.setAutoCommit(false);
        	pstmt = conn.prepareStatement(sql);
        	
        	pstmt.setString(1, String.valueOf(status));
            pstmt.setInt(2, memberNo);
            result = pstmt.executeUpdate();
            
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
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
    
} // MemberDAO