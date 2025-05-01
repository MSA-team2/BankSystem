package service;

import java.sql.Connection;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import dao.MemberDAO;
import dbConn.util.ConnectionHelper;
import model.MemberVO;

public class MemberService {
	private final MemberDAO md = new MemberDAO();
	
	// 회원가입
	public int insertMember(MemberVO member) {
		Connection conn = ConnectionHelper.getConnection("mysql");
		return md.insertMember(conn, member);
	}

	// 로그인
	public MemberVO loginMember(String id, String pwd) {
		Connection conn = ConnectionHelper.getConnection("mysql");
		return md.loginMember(conn, id, pwd);
	}

	// 아이디 찾기
	public String findId(String name, String jumin) {
		Connection conn = ConnectionHelper.getConnection("mysql");
		return md.findMemberId(conn, name, jumin);
	}
	
	// 비밀번호 찾기 -> 새 비밀번호 변경
	public int updatePwd(String id, String name, String jumin, String newPwd) {
		Connection conn = ConnectionHelper.getConnection("mysql");
		return md.updatePwd(conn, id, name, jumin, newPwd);
	}

	// 주민번호 유효성 체크
	public boolean checkJumin(String jumin) {
		if (!jumin.matches("\\d{6}-\\d{7}")) {
            System.out.println("형식은 000000-0000000 이어야 합니다.");
            return false;
        }
        jumin = jumin.replace("-", "");
        if (!jumin.matches("\\d{13}")) {
            System.out.println("주민번호는 숫자 13자리여야 합니다.");
            return false;
        }
        char genderCode = jumin.charAt(6);
        if (genderCode < '1' || genderCode > '4') {
            System.out.println("성별코드가 잘못되었습니다.");
            return false;
        }
        try {
            String birth = jumin.substring(0, 6);
            String century = (genderCode == '1' || genderCode == '2') ? "19" : "20";
            LocalDate.parse(century + birth, DateTimeFormatter.ofPattern("yyyyMMdd"));
        } catch (Exception e) {
            System.out.println("생년월일이 잘못되었습니다.");
            return false;
        }
        return true;
    }
	
	// 아이디 중복 체크
	public boolean checkId(String id) {
        Connection conn = ConnectionHelper.getConnection("mysql");
		return md.checkId(conn, id);
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
        return phone.matches("\\d{3}-\\d{4}-\\d{4}");
    }

	// 전화번호 중복 체크
	public boolean confirmPhone(String phone) {
        Connection conn = ConnectionHelper.getConnection("mysql");
		return md.confirmPhone(conn, phone);
    }
	
	// 비밀번호 변경시 입력 정보 확인
	public boolean validateUserInfo(String id, String name, String jumin) {
		Connection conn = ConnectionHelper.getConnection("mysql");
		return md.validateUserInfo(conn, id, name, jumin);
	}
	
//	public void lockPwd(String id) {
//		Connection conn = ConnectionHelper.getConnection("mysql");
//		return md.lockPwd(conn, id);
//	}
	
	 public void lockPwd(String id) {
		 Connection conn = ConnectionHelper.getConnection("mysql");
	     md.incrementLockCount(conn, id);
	     int count = md.getLockCount(conn, id);
	     if (count >= 5) {
	    	 md.lockAccount(md, id);
	         System.out.println("비밀번호 5회 이상 틀려 계정이 잠금되었습니다.");
		}
	 }
	
	
} // MemberService