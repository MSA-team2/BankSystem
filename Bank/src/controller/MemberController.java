package controller;

<<<<<<< HEAD
import java.util.Scanner;

=======
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

import dbConn.util.CloseHelper;
import dbConn.util.ConnectionHelper;
>>>>>>> 2b23d7d3f10ffb9c79610e73a294c14c3edc0509
import model.MemberVO;
import service.MemberService;

public class MemberController {
<<<<<<< HEAD
	private final Scanner sc = new Scanner(System.in);
	private final MemberService ms = new MemberService();

	// 회원가입
	public void insertMember() {
=======
	static Scanner sc = new Scanner(System.in);
	static Connection conn = null;
	static PreparedStatement ps = null;
	static ResultSet rs = null;
	
	// connect
	public static void connect() {
		try {
			conn = ConnectionHelper.getConnection("mysql");
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
>>>>>>> 2b23d7d3f10ffb9c79610e73a294c14c3edc0509
		System.out.println("\n======= 회원가입 =======");
        System.out.println("※ 입력 중 '0'을 입력하면 메인메뉴로 돌아갑니다.");

        String name = getInput("이름: ");
        if (name == null) return;

        String jumin;
        while (true) {
            jumin = getInput("주민번호(ex 000123-1234567): ");
            if (jumin == null) return;
            if (ms.checkJumin(jumin)) break;
        }

        String id;
        while (true) {
            id = getInput("아이디: ");
            if (id == null) return;
            if (ms.checkId(id)) {
                System.out.println("이미 사용 중인 아이디입니다. 다른 아이디를 입력해주세요.");
            } else {
                System.out.println("사용 가능한 아이디입니다.");
                break;
            }
        }

        String pwd;
        String pwdConfirm;
        while (true) {
            pwd = getInput("비밀번호(영문+숫자 8자리이상): ");
            if (pwd == null) return;
            if (!ms.checkPwd(pwd)) continue;

            pwdConfirm = getInput("비밀번호 확인: ");
            if (pwdConfirm == null) return;

            if (!ms.confirmPwd(pwd, pwdConfirm)) continue;
            break;
        }

        String address = getInput("주소: ");
        if (address == null) return;

        String phone;
        while (true) {
            phone = getInput("전화번호 입력(ex 010-1234-5678): ");
            if (phone == null) return;
            if (!ms.checkPhone(phone)) continue;
            if (ms.confirmPhone(phone)) {
                System.out.println("이미 가입된 핸드폰 번호 입니다. 다른 번호를 입력해주세요");
            } else {
                System.out.println("사용 가능한 번호 입니다.");
                break;
            }
        }
        
        MemberVO member = new MemberVO();
        member.setName(name);
        member.setJumin(jumin);
        member.setMemberId(id);
        member.setPassword(pwd);
        member.setAddress(address);
        member.setPhone(phone);

        int result = ms.insertMember(member);
        System.out.println(result > 0 ? "회원가입이 완료되었습니다. 메인메뉴로 이동합니다." 
        							  : "회원가입에 실패했습니다. 다시 시도해주세요");
	}
	
	// 로그인
	public void loginMember() {
		while (true) {
            String id = getInput("아이디: ");
            String pwd = getInput("비밀번호: ");
            if (id == null || pwd == null) return;

            MemberVO member = ms.loginMember(id, pwd);
            if (member != null) {
                SessionManager.login(member);
                System.out.println(member.getName() + "님 환영합니다");
                break;
            } else {
                System.out.println("아이디 또는 비밀번호가 올바르지 않습니다.");
//                ms.lockPwd(id); // 로그인 실패 시 틀린 횟수 증가
                System.out.print("다시 시도하시겠습니까? (Y/N): ");
                String retry = sc.nextLine().trim().toUpperCase();
                if (!retry.equals("Y")) {
                    break;
                }
            }
        }
	}

	// 아이디 찾기
	public void findMemberId() {
		String name = getInput("이름: ");
        if (name == null) return;

        String jumin;
        while (true) {
            jumin = getInput("주민번호: ");
            if (jumin == null) return;
            if (ms.checkJumin(jumin)) break;
        }

        String findId = ms.findId(name, jumin);
        if (findId != null) {
            System.out.println(name + "님의 아이디는 " + findId + " 입니다.");
        } else {
            System.out.println("일치하는 정보가 없습니다.");
        }
	}
	
	// 비밀번호 찾기 -> 새 비밀번호 변경
	public void findPwd() {
		String id = getInput("아이디: ");
        String name = getInput("이름: ");

        String jumin;
        while (true) {
            jumin = getInput("주민번호: ");
            if (jumin == null) return;
            if (ms.checkJumin(jumin)) break;
        }
        
        // 입력받은 정보가 유효한지 먼저 검증
        if (!ms.validateUserInfo(id, name, jumin)) {
            System.out.println("정보가 일치하지 않습니다.");
            return;
        }

        String newPwd;
        String newPwdConfirm;
        while (true) {
            newPwd = getInput("새 비밀번호: ");
            if (newPwd == null) return;
            if (!ms.checkPwd(newPwd)) continue;

            newPwdConfirm = getInput("새 비밀번호 확인: ");
            if (newPwdConfirm == null) return;
            if (!ms.confirmPwd(newPwd, newPwdConfirm)) continue;
            break;
        }

        int result = ms.updatePwd(id, name, jumin, newPwd);
        System.out.println(result > 0 ? "비밀번호가 재설정되었습니다." : "비밀번호 변경에 실패했습니다.");
	}
	
	// 비밀번호 5회이상 틀릴시 잠금
//	public static void lockPwd() {
//        System.out.println("비밀번호를 5회 이상 틀려 계정이 잠금되었습니다. 관리자에게 문의해주세요.");
//	}
	
	// 입력받기, 입력중 돌아가기
	public String getInput(String info) {
	    System.out.print(info);
	    String input = sc.nextLine().trim();
	    return input.equals("0") ? null : input;
	}
	
	
} // MemberController