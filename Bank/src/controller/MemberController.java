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

public class MemberController {
	static Scanner sc = new Scanner(System.in);
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
	// 첫화면
	public static void MainMenu() {
		MemberVO vo = new MemberVO();
		
		while(true) {
			System.out.println("\n======== 투게더 은행 ========");
			System.out.println("\t1. 회원가입");
			System.out.println("\t2. 로그인");
			System.out.println("\t3. 아이디 찾기");			
			System.out.println("\t4. 비밀번호 찾기");			
			System.out.println("\t0. 종료");
			System.out.println("===========================");
			System.out.print("메뉴 선택: ");
			int menu = sc.nextInt();
			sc.nextLine();
			
			switch (menu) {
			case 1: joinMember(); break;
			case 2: loginMember(); break;
			case 3: findId(); break;
			case 4: findPwd(); break;
			case 0: System.out.println("감사합니다. 안녕히가세요"); System.exit(0); break;
			default: System.out.println("잘못 입력하셨습니다. 다시 입력해주세요");
			}
		}
	}
	
	// 1. 회원가입
	public static void joinMember() {
		System.out.println("\n======= 회원가입 =======");
		System.out.println("※ 입력 중 '0'을 입력하면 메인메뉴로 돌아갑니다.");
		String sql = "INSERT INTO MEMBER (name, jumin, member_id, password, address, phone)"
						+ "VALUES(?, ?, ?, ?, ?, ?)";
		try {
			ps = conn.prepareStatement(sql);
			
			String name = getInput("이름: ");
	        if (name == null) return;

	        // 주민번호 자리수 체크
	        String jumin = null;
	        while (true) {
	        	jumin = getInput("주민번호(ex 120304-1234567): ");
	        	if (jumin == null) return;
	        	if (checkJumin(jumin)) {
	        		break;
	        	}
	        }
	        // 아이디 중복 체크
	        String id = null;
	        while (true) {
	        	id = getInput("아이디: ");
	            if (id == null) return;

	            if (checkId(id)) {
	                System.out.println("이미 사용 중인 아이디입니다. 다른 아이디를 입력해주세요.");
	            } else {
	                System.out.println("사용 가능한 아이디입니다.");
	                break; // 중복 아니면 while 탈출
	            }
	        }
	        // 비밀번호 유효성 체크
	        String pwd = null;
	        String pwdConfirm = null;
	        while (true) {
	        	pwd = getInput("비밀번호(영문+숫자 8자리이상): ");
	        	if (pwd == null) return;
	        	if (!checkPwd(pwd)) continue;

	        	pwdConfirm = getInput("비밀번호 확인: ");
	        	if (pwdConfirm == null) return;
	        	
	        	if (!confirmPwd(pwd, pwdConfirm)) continue;
	        	break;
	        }
	        String address = getInput("주소: ");
	        if (address == null) return;
	        
	        // 전화번호 자리수 체크
	        String phone = null;
	        while (true) {      	
	        	phone = getInput("전화번호 입력(ex 010-1234-5678): ");
	        	if (phone == null) return;
	        	
	        	if (!checkPhone(phone)) continue;
	        	if (confirmPhone(phone)) {
	        		System.out.println("이미 가입된 핸드폰 번호 입니다. 다른 번호를 입력해주세요");
	        	} else {
	        		System.out.println("사용 가능한 번호 입니다.");
	        		break;
	        	}
	        }
			
			ps.setString(1, name);
			ps.setString(2, jumin);
			ps.setString(3, id);
			ps.setString(4, pwd);
			ps.setString(5, address);
			ps.setString(6, phone);
			
			int result = ps.executeUpdate();

			if (result == 1) {
			    System.out.println("회원가입이 완료되었습니다. 로그인 페이지로 이동합니다.");
			    loginMember();
			} else {
			    System.out.println("회원가입에 실패했습니다. 다시 시도해주세요.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	// 2. 로그인
	public static void loginMember() {
		
	    while (true) {
	        System.out.println("\n========== 로그인 ==========");
	        System.out.print("아이디: ");
	        String id = sc.nextLine();

	        System.out.print("비밀번호: ");
	        String pwd = sc.nextLine();

	        String sql = "SELECT * FROM MEMBER WHERE member_id = ? AND password = ?";

	        try {
	            ps = conn.prepareStatement(sql);
	            ps.setString(1, id);
	            ps.setString(2, pwd);
	            rs = ps.executeQuery();

	            if (rs.next()) {

	            	MemberVO member = new MemberVO();
	                
	            	member.setMemberNo(rs.getInt("member_no"));
	            	member.setName(rs.getString("name"));
	            	member.setJumin(rs.getString("jumin"));
	            	member.setMemberId(rs.getString("member_id"));
	            	member.setPassword(rs.getString("password"));
	            	member.setAddress(rs.getString("address"));
	            	member.setPhone(rs.getString("phone"));
	            	member.setStatus(rs.getString("status").charAt(0));
	            	member.setRole(rs.getInt("lock_cnt"));
	            	member.setRole(rs.getInt("role"));
	            	
	            	SessionManager.login(member);
	            	
	                System.out.println("[" + member.getName() + "] 님, 환영합니다!");	                
	             
	                if (SessionManager.isAdmin()) { // 관리자
//	                	 AdminController.menu();	          
	                } else { // 일반 회원 
	                	UserController.MemberMenu();	                    
	                }
	                return; // 로그인 성공 -> 메서드 종료	                
	            } else {
	            	// 추가사항: 아이디/비밀번호 찾기
	                System.out.println("아이디 또는 비밀번호가 올바르지 않습니다.");
	                System.out.print("다시 시도하시겠습니까? (Y/N): ");
	                
	                String retry = sc.nextLine().trim().toUpperCase();
	                if (!retry.equals("Y")) {
	                    break;
	                }
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        } catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
	    } // while
	}
	
	// 3. 아이디 찾기
	public static void findId() {
		System.out.println("\n======== 아이디 찾기 ========");
		System.out.print("이름: ");
		String name = sc.nextLine();
		
		String jumin = null;
        while (true) {
        	jumin = getInput("주민번호: ");
        	if (jumin == null) return;
        	if (checkJumin(jumin)) {
        		break;
        	}
        }
        
        String sql = "SELECT name, member_id FROM MEMBER WHERE name = ? AND jumin = ?";
        
        try {
        	ps = conn.prepareStatement(sql);
        	ps.setString(1, name);
        	ps.setString(2, jumin);
        	rs = ps.executeQuery();
        	
        	if (rs.next()) {
        		System.out.println(rs.getString("name") + "님의 아이디는 " + rs.getString("member_id") +  " 입니다.");
        		System.out.println("메인메뉴로 이동합니다.");
        	} else {
        		System.out.println("일치하는 정보가 없습니다.");
        	}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	// 4. 비밀번호 찾기 -> 새 비밀번호로 변경
	public static void findPwd() {
		System.out.println("\n======= 비밀번호 찾기 =======");
		System.out.print("아이디: ");
		String id = sc.nextLine();
		
		System.out.print("이름: ");
		String name = sc.nextLine();
		
		String jumin = null;
        while (true) {
        	jumin = getInput("주민번호: ");
        	if (jumin == null) return;
        	if (checkJumin(jumin)) {
        		break;
        	}
        }
        
        String checkId = "SELECT * FROM MEMBER WHERE member_id = ? AND name = ? AND jumin = ?";
        
        try {
			ps = conn.prepareStatement(checkId);
        	ps.setString(1, id);
        	ps.setString(2, name);
        	ps.setString(3, jumin);
			rs = ps.executeQuery();
			
			// 회원 정보 있을시 비밀번호 재설정
			if (rs.next()) {
				System.out.println("회원 정보가 확인되었습니다.");
				System.out.print("새 비밀번호 설정: ");
				String newPwd = sc.nextLine();

				String updatePwd = "UPDATE MEMBER SET password = ? WHERE member_id = ?";
				ps = conn.prepareStatement(updatePwd);
				
	        	ps.setString(1, newPwd);
	        	ps.setString(2, id);
	        	
				int result = ps.executeUpdate();

				if (result == 1) {
					System.out.println("비밀번호가 재설정 되었습니다.");
					System.out.println("메인메뉴로 이동합니다.");
				} else {
					System.out.println("비밀번호 변경에 실패했습니다.");
				}
			} else {
				System.out.println("입력한 정보가 일치하지 않습니다.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	} // end MainMenu()
	// ============================================================
	// ----- 비밀번호 5회이상 틀릴시 ----- //
	public static void lockPwd() {
		
	}

	// -----  입력중 돌아가기 ----- //
	public static String getInput(String info) {
	    System.out.print(info);
	    String input = sc.nextLine().trim();
	    if (input.equals("0")) return null;
	    return input;
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
	    	if (rs != null) rs.close();
	    	if (ps != null) ps.close();
	    	
	        ps = conn.prepareStatement(sql);
	        ps.setString(1, phone);
	        rs = ps.executeQuery();
	        return rs.next();  // 중복
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return true;
	    }
	}
	
}