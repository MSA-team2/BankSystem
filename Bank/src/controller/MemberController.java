package controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

import dbConn.util.CloseHelper;
import dbConn.util.ConnectionSingletonHelper;
import model.MemberVO;
import service.MemberService;

public class MemberController {
	static Scanner sc = new Scanner(System.in);
	static MemberService ms = new MemberService();
	
	static Connection conn = null;
	static PreparedStatement ps = null;
	static ResultSet rs = null;
	
	// connect
	public static void connect() {
		try {
			conn = ConnectionSingletonHelper.getConnection("mysql");
			conn.setAutoCommit(true); // 자동커밋 켬
			
		} catch (Exception e) {
			e.printStackTrace();
		} 
	} 
	// close
	public static void close() {
		try {
			CloseHelper.close(rs);
			CloseHelper.close(ps);
			CloseHelper.close(conn);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void insertMember(String name, String jumin, String id, String pwd, String address, String phone) {
		
		MemberVO mv = new MemberVO(name, jumin, id, pwd, address, phone);
		int result = new MemberService().insertMember();
		
		if (result > 0) {
			// 성공 메세지
		} else {
			// 실패 메세지
		}
		
		
	}
	
	public void loginMember() {
		int result = new MemberService().loginMember();
		
		if (result > 0) {
			// 성공 메세지
		} else {
			// 실패 메세지
		}
	}

	// 3. 아이디 찾기
	public void findId() {
		
		MemberVO mv = new MemberService().selectByMemberIdPwd();
		
		if (mv == null) {
			// 조회된 아이디 없음
		} else {
			// 있으면 반환
		}
		
		
        
//        String sql = "SELECT name, member_id FROM MEMBER WHERE name = ? AND jumin = ?";
//        
//        try {
//        	ps = conn.prepareStatement(sql);
//        	ps.setString(1, name);
//        	ps.setString(2, jumin);
//        	rs = ps.executeQuery();
//        	
//        	if (rs.next()) {
//        		System.out.println(rs.getString("name") + "님의 아이디는 " + rs.getString("member_id") +  " 입니다.");
//        		System.out.println("메인메뉴로 이동합니다.");
//        	} else {
//        		System.out.println("일치하는 정보가 없습니다.");
//        	}
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
	}
	
	// 4. 비밀번호 찾기 -> 새 비밀번호로 변경
	public static void findPwd() {
		
		MemberVO mv = new MemberVO();
		
		// pwd 값 받아와야함
		mv.setPassword(null);
		
        
//        String checkId = "SELECT * FROM MEMBER WHERE member_id = ? AND name = ? AND jumin = ?";
//        
//        try {
//			ps = conn.prepareStatement(checkId);
//        	ps.setString(1, id);
//        	ps.setString(2, name);
//        	ps.setString(3, jumin);
//			rs = ps.executeQuery();
//			
//			// 회원 정보 있을시 비밀번호 재설정
//			if (rs.next()) {
//				System.out.println("회원 정보가 확인되었습니다.");
//				System.out.print("새 비밀번호 설정: ");
//				String newPwd = sc.nextLine();
//
//				String updatePwd = "UPDATE MEMBER SET password = ? WHERE member_id = ?";
//				ps = conn.prepareStatement(updatePwd);
//				
//	        	ps.setString(1, newPwd);
//	        	ps.setString(2, id);
//	        	
//				int result = ps.executeUpdate();
//
//				if (result == 1) {
//					System.out.println("비밀번호가 재설정 되었습니다.");
//					System.out.println("메인메뉴로 이동합니다.");
//				} else {
//					System.out.println("비밀번호 변경에 실패했습니다.");
//				}
//			} else {
//				System.out.println("입력한 정보가 일치하지 않습니다.");
//			}
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
	}
	
	
	// ============================================================
	// ----- 비밀번호 5회이상 틀릴시 ----- //
	public static void lockPwd() {
		
	}

	

	// ----- 아이디 중복체크----- //
	public static boolean checkId(String id) {
		String sql = "SELECT member_id FROM MEMBER WHERE member_id = ?";
		try {
			ps = conn.prepareStatement(sql);
            ps.setString(1, id);
            rs = ps.executeQuery();
			
            if (rs.next()) {
            	return true; // 중복된 아이디
            }
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	// ----- 주민번호 자리수 체크 ----- //
	public static boolean checkJumin(String jumin) {
		// 	 6   -   7
		// 920123-1204543
		if (!jumin.matches("\\d{6}-\\d{7}")) {
	        System.out.println("형식은 000000-0000000 이어야 합니다.");
	        return false;
	    }

	    // 하이픈 제거
	    jumin = jumin.replace("-", ""); // → "9201231234567"
		
	    // 자리수 체크
		if (!jumin.matches("\\d{13}")) {
	        System.out.println("주민번호는 숫자 13자리여야 합니다.");
	        return false;
	    }
		
		// 성별 체크
	    char genderCode = jumin.charAt(6);
	    if (genderCode < '1' || genderCode > '4') {
	        System.out.println("성별코드가 잘못되었습니다.");
	        return false;
	    }

	    // 생년월일 유효성 체크
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
	
	// ----- 비밀번호 유효성 체크 ----- //
	public static boolean checkPwd(String pwd) {
		if (pwd.length() < 8) {
	        System.out.println("비밀번호는 최소 8자 이상이어야 합니다.");
	        return false;
	    }
	    if (!pwd.matches(".*[a-zA-Z].*")) {
	        System.out.println("비밀번호에는 영문자가 포함되어야 합니다.");
	        return false;
	    }
	    if (!pwd.matches(".*[0-9].*")) {
	        System.out.println("비밀번호에는 숫자가 포함되어야 합니다.");
	        return false;
	    }
	    return true;
	}
	
	// ----- 비밀번호 일치 확인 ----- //
	public static boolean confirmPwd(String pwd, String pwdConfirm) {
		if (!pwd.equals(pwdConfirm)) {
	        System.out.println("비밀번호가 일치하지 않습니다.");
	        return false;
	    }
	    return true;
	}
	
	// ----- 전화번호 유효성 체크 ----- //
	public static boolean checkPhone(String phone) {
		if (!phone.matches("\\d{3}-\\d{4}-\\d{4}")) {
	        System.out.println("형식은 010-0000-0000 이어야 합니다.");
	        return false;
	    }
		return true;
	}
	
	// ----- 전화번호 중복 체크 ----- //
	public static boolean confirmPhone(String phone) {
		String sql = "SELECT phone FROM MEMBER WHERE phone = ?";
		try {
	        PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, phone);
			
	        ResultSet rs = ps.executeQuery();
	        return rs.next();  // 중복
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return true;
	    }
	}
	
	
	
	
}