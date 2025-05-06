package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.mindrot.jbcrypt.BCrypt;

import dbConn.util.CloseHelper;
import dbConn.util.ConnectionHelper;
import model.MemberVO;

public class MemberDAO {

	// 회원가입
	public int insertMember(Connection conn, MemberVO member) {
		// insert => 처리된 행 수
		String sql = "INSERT INTO MEMBER (name, jumin, member_id, password, address, phone)"
					+ "VALUES (?, ?, ?, ?, ?, ?)";
	    try (PreparedStatement ps = conn.prepareStatement(sql)) {
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
	    }
    }
	
	// 로그인
	public MemberVO loginMember(Connection conn, String id, String pwd) throws SQLException {
		// select문 => ResultSet객체 => Member객체
		String sql = "SELECT * FROM MEMBER WHERE member_id = ?";
	    try (PreparedStatement ps = conn.prepareStatement(sql)) {
	        ps.setString(1, id);
	        try (ResultSet rs = ps.executeQuery()) {
	        	if (rs.next()) {
	        	    String dbPwd = rs.getString("password");
	        	    int lockCnt = rs.getInt("lock_cnt");
	        	    String status = rs.getString("status");
	        	    
	        	    // 잠금 상태
	                if ("N".equals(status)) {
	                    return new MemberVO(
	                        rs.getInt("member_no"),
	                        rs.getString("name"),
	                        rs.getString("jumin"),
	                        rs.getString("member_id"),
	                        dbPwd,
	                        rs.getString("address"),
	                        rs.getString("phone"),
	                        status.charAt(0),
	                        lockCnt,
	                        rs.getInt("role")
	                    );
	                }

	                //비밀번호 불일치
	                if (!BCrypt.checkpw(pwd, dbPwd)) {
	                    incrementLockCount(conn, id); // lock_cnt 증가
	                    if (getLockCount(conn, id) >= 5) {
	                        lockAccount(conn, id); // 계정 잠금 처리
	                        return new MemberVO(
	                            rs.getInt("member_no"),
	                            rs.getString("name"),
	                            rs.getString("jumin"),
	                            rs.getString("member_id"),
	                            dbPwd,
	                            rs.getString("address"),
	                            rs.getString("phone"),
	                            'N',
	                            5,
	                            rs.getInt("role")
	                        );
	                    }
	                    return null;
	                }

	                // lock_cnt가 5 이상 잠금 처리
	                if (lockCnt >= 5) {
	                    lockAccount(conn, id);
	                    return new MemberVO(
	                        rs.getInt("member_no"),
	                        rs.getString("name"),
	                        rs.getString("jumin"),
	                        rs.getString("member_id"),
	                        dbPwd,
	                        rs.getString("address"),
	                        rs.getString("phone"),
	                        'N',
	                        lockCnt,
	                        rs.getInt("role")
	                    );
	                }

	                // 로그인 성공 -> lock_cnt 초기화
	                resetLockCount(conn, id);
	                return new MemberVO(
	                    rs.getInt("member_no"),
	                    rs.getString("name"),
	                    rs.getString("jumin"),
	                    rs.getString("member_id"),
	                    dbPwd,
	                    rs.getString("address"),
	                    rs.getString("phone"),
	                    status.charAt(0),
	                    0,
	                    rs.getInt("role")
	                );
	        	}
	        }
	    }
	    return null;
    }

	// 아이디 찾기
	public String findMemberId(Connection conn, String name, String jumin) {
	    String sql = "SELECT member_id FROM MEMBER WHERE name = ? AND jumin = ?";
	    try (PreparedStatement ps = conn.prepareStatement(sql)) {
	        ps.setString(1, name);
	        ps.setString(2, jumin);

	        try (ResultSet rs = ps.executeQuery()) {
	            if (rs.next()) {
	                return rs.getString("member_id");
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return null;
	}


	// 비밀번호 찾기 -> 새 비밀번호 변경
    public int updatePwd(Connection conn, String id, String name, String jumin, String newHashedPwd) {
    	// update문 => 처리된 행수(int)
        String sql = "UPDATE MEMBER SET password = ? WHERE member_id = ? AND name = ? AND jumin = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newHashedPwd);
            ps.setString(2, id);
            ps.setString(3, name);
            ps.setString(4, jumin);
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }
    
    // 주민번호 중복 체크
    public boolean confirmJumin(Connection conn, String jumin) {
    	String sql = "SELECT 1 FROM MEMBER WHERE jumin = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, jumin);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
	}

    // 아이디 중복 체크
    public boolean checkId(Connection conn, String id) {
        String sql = "SELECT member_id FROM MEMBER WHERE member_id = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return true;
        }
    }

    // 전화번호 중복 체크
    public boolean confirmPhone(Connection conn, String phone) {
        String sql = "SELECT phone FROM MEMBER WHERE phone = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, phone);
            
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return true;
        }
    }

    // 비밀번호 변경시 입력 정보 확인
	public boolean validateUserInfo(Connection conn, String id, String name, String jumin) {
		String sql = "SELECT 1 FROM MEMBER WHERE member_id = ? AND name = ? AND jumin = ?";
	    try (PreparedStatement ps = conn.prepareStatement(sql)) {
	        ps.setString(1, id);
	        ps.setString(2, name);
	        ps.setString(3, jumin);

	        try (ResultSet rs = ps.executeQuery()) {
	            return rs.next();
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}
	
	// 틀린횟수 조회
	public int getLockCount(Connection conn, String id) {
		String sql = "SELECT lock_cnt FROM MEMBER WHERE member_id = ?";
	    try (PreparedStatement ps = conn.prepareStatement(sql)) {
	        ps.setString(1, id);
	        try (ResultSet rs = ps.executeQuery()) {
	            if (rs.next()) return rs.getInt("lock_cnt");
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return 0;
	}
	
	// 틀린횟수 증가
	public void incrementLockCount(Connection conn, String id) throws SQLException {
		String sql = "UPDATE MEMBER SET lock_cnt = lock_cnt + 1 WHERE member_id = ?";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, id);
			ps.executeUpdate();
		}
	}
	
	// 틀린횟수 초기화
	public void resetLockCount(Connection conn, String id) throws SQLException {
	    String sql = "UPDATE MEMBER SET lock_cnt = 0 WHERE member_id = ?";
	    try (PreparedStatement ps = conn.prepareStatement(sql)) {
	        ps.setString(1, id);
	        ps.executeUpdate();
	    }
	}
	
	// 잠금 상태 변경 'Y' -> 'N'
	public void lockAccount(Connection conn, String id) throws SQLException {
        String sql = "UPDATE MEMBER SET status = 'N' WHERE member_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.executeUpdate();
        }
    }
	
	// 잠금 계정 관리자 문의
	public boolean isAccountLocked(Connection conn, String id) {
		String sql = "SELECT status, lock_cnt FROM MEMBER WHERE member_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String status = rs.getString("status");
                    int lockCnt = rs.getInt("lock_cnt");
                    return "N".equals(status) && lockCnt >= 5;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
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

    
} // MemberDAO