package service;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.mindrot.jbcrypt.BCrypt;

import dao.MemberDAO;
import dbConn.util.ConnectionHelper;
import model.MemberVO;

public class MemberService {
	private final MemberDAO md = new MemberDAO();
	
	// 회원가입
	public int insertMember(MemberVO member) {
		try (Connection conn = ConnectionHelper.getConnection("mysql")) {
			// 비밀번호 암호화
			String hashedPwd = BCrypt.hashpw(member.getPassword(), BCrypt.gensalt());
	        member.setPassword(hashedPwd);
            return md.insertMember(conn, member);
        } catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}
	}

	// 로그인
	public MemberVO loginMember(String id, String pwd) {
		try (Connection conn = ConnectionHelper.getConnection("mysql")) {
            return md.loginMember(conn, id, pwd);
        } catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	// 아이디 찾기
	public String findId(String name, String jumin) {
		try (Connection conn = ConnectionHelper.getConnection("mysql")) {
			return md.findMemberId(conn, name, jumin);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	// 비밀번호 찾기 -> 새 비밀번호 변경
	public int updatePwd(String id, String name, String jumin, String newPwd) {
		try (Connection conn = ConnectionHelper.getConnection("mysql")) {
			String hashedPwd = BCrypt.hashpw(newPwd, BCrypt.gensalt());
            return md.updatePwd(conn, id, name, jumin, hashedPwd);
        } catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}
	}

	// 주민번호 유효성 체크
	public boolean checkJumin(String jumin) {
		if (!jumin.matches("\\d{6}-\\d{7}")) {
            System.out.println("⚠️ 주민번호 형식이 올바르지 않습니다.");
            return false;
        }
        jumin = jumin.replace("-", "");
        if (!jumin.matches("\\d{13}")) {
            System.out.println("⚠️ 주민번호는 숫자 13자리여야 합니다.");
            return false;
        }
        char genderCode = jumin.charAt(6);
//        if (genderCode < '1' || genderCode > '4') {
//            System.out.println("성별코드가 잘못되었습니다.");
//            return false;
//        }
        try {
            String birth = jumin.substring(0, 6);
            String century = (genderCode == '1' || genderCode == '2') ? "19" : "20";
            LocalDate.parse(century + birth, DateTimeFormatter.ofPattern("yyyyMMdd"));
        } catch (Exception e) {
            System.out.println("⚠️ 생년월일이 올바르지 않습니다.");
            return false;
        }
        return true;
    }
	
	// 주민번호 중복 체크
	public boolean confirmJumin(String jumin) {
	    try (Connection conn = ConnectionHelper.getConnection("mysql")) {
	        return md.confirmJumin(conn, jumin);
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return true;
	    }
	}
	
	// 아이디 중복 체크
	public boolean checkId(String id) {
		try (Connection conn = ConnectionHelper.getConnection("mysql")) {
            return md.checkId(conn, id);
        } catch (SQLException e) {
			e.printStackTrace();
			return true; // 중복
		}
    }
	
	// 비밀번호 유효성 체크
	public boolean checkPwd(String pwd) {
        return pwd.length() >= 8 && pwd.matches(".*[a-zA-Z].*") && pwd.matches(".*[0-9].*");
    }
	
	// 비밀번호 일치 확인
	public boolean confirmPwd(String pwd, String pwdConfirm) {
        return pwd.equals(pwdConfirm);
    }
	
	// 전화번호 유효성 체크
	public boolean checkPhone(String phone) {
		if (!phone.matches("\\d{3}-\\d{4}-\\d{4}")) {
			System.out.println("⚠️ 전화번호 형식이 올바르지 않습니다.");
			return false;
		}
		return true;
    }

	// 전화번호 중복 체크
	public boolean confirmPhone(String phone) {
		try (Connection conn = ConnectionHelper.getConnection("mysql")) {
            return md.confirmPhone(conn, phone);
        } catch (SQLException e) {
            e.printStackTrace();
            return true;
        }
    }
	
	// 비밀번호 변경시 입력 정보 확인
	public boolean validateUserInfo(String id, String name, String jumin) {
		try (Connection conn = ConnectionHelper.getConnection("mysql")) {
            return md.validateUserInfo(conn, id, name, jumin);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
	}
	
//	public void lockPwd(String id) {
//	    Connection conn = null;
//	    try {
//	        conn = ConnectionHelper.getConnection("mysql");
//	        conn.setAutoCommit(false);
//
//	        md.incrementLockCount(conn, id);
//	        int count = md.getLockCount(conn, id);
//	        if (count >= 5) {
//	            md.lockAccount(conn, id);
//	            System.out.println("❌ 비밀번호 5회 이상 틀려 계정이 잠금되었습니다.");
//	        } else {
//	            System.out.println("⚠️ 비밀번호 오류 횟수: " + count + "/5");
//	        }
//
//	        conn.commit();
//	    } catch (SQLException e) {
//	        e.printStackTrace();
//	        if (conn != null) {
//	            try {
//	                conn.rollback(); 
//	            } catch (SQLException rollbackEx) {
//	                rollbackEx.printStackTrace();
//	            }
//	        }
//	    } finally {
//	        CloseHelper.close(conn);
//	    }
//	}
	
	// 틀린횟수 조회
	public int getLockCount(String id) {
	    try (Connection conn = ConnectionHelper.getConnection("mysql")) {
	        return md.getLockCount(conn, id);
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return -1;
	    }
	}
	
	// 잠금 계정 관리자 문의
	public boolean isAccountLocked(String id) {
		try (Connection conn = ConnectionHelper.getConnection("mysql")) {
			return md.isAccountLocked(conn, id);
		} catch (SQLException e) {
			e.printStackTrace();
			return true;
		}
	}

	
} // MemberService