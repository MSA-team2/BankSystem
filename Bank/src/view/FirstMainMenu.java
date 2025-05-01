package view;

import java.util.Scanner;
import controller.MemberController;

public class FirstMainMenu {
	
	private Scanner sc = new Scanner(System.in);
	private MemberController mc = new MemberController();
	
	// 첫화면
	public void MainMenu() {
		
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
			case 1: inputMember(); break;
			case 2: inputMemberIdPwd(); break;
			case 3: findMemberId(); break;
			case 4: findMemberPwd(); break;
			case 0: System.out.println("감사합니다. 안녕히가세요"); System.exit(0); break;
			default: System.out.println("잘못 입력하셨습니다. 다시 입력해주세요");
			}
		}
	}
	
	public void inputMember() {
		System.out.println("\n======= 회원가입 =======");
		System.out.println("※ 입력 중 '0'을 입력하면 메인메뉴로 돌아갑니다.");
		
		String name = getInput("이름: ");
        if (name == null) return;

        // 주민번호 자리수 체크
        String jumin = null;
        while (true) {
        	jumin = getInput("주민번호(ex 120304-1234567): ");
        	if (jumin == null) return;
        	if (md.checkJumin(jumin)) break;
        }
        // 아이디 중복 체크
        String id = null;
        while (true) {
        	id = getInput("아이디: ");
            if (id == null) return;

            if (md.checkId(id)) {
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
        	if (!md.checkPwd(pwd)) continue;

        	pwdConfirm = getInput("비밀번호 확인: ");
        	if (pwdConfirm == null) return;
        	
        	if (!md.confirmPwd(pwd, pwdConfirm)) continue;
        	break;
        }
        String address = getInput("주소: ");
        if (address == null) return;
        // 전화번호 자리수 체크
        String phone = null;
        while (true) { 	
        	phone = getInput("전화번호 입력(ex 010-1234-5678): ");
        	if (phone == null) return;
        	
        	if (!md.checkPhone(phone)) continue;
        	if (md.confirmPhone(phone)) {
        		System.out.println("이미 가입된 핸드폰 번호 입니다. 다른 번호를 입력해주세요");
        	} else {
        		System.out.println("사용 가능한 번호 입니다.");
        		break;
        	}
        }
        
        mc.insertMember(name, jumin, id, pwd, address, phone);
	}
	
	public void inputMemberIdPwd() {
        System.out.println("\n========== 로그인 ==========");
        String id = getInput("아이디: ");
        String pwd = getInput("비밀번호: ");
        mc.loginMember();
	}
	
	public void findMemberId() {
		System.out.println("\n======== 아이디 찾기 ========");
		String name = getInput("이름: ");
		
		String jumin = null;
        while (true) {
        	jumin = getInput("주민번호: ");
        	if (jumin == null) return;
        	if (checkJumin(jumin)) {
        		break;
        	}
        }
        mc.findId();
	}
	
	public void findMemberPwd() {
		System.out.println("\n======= 비밀번호 찾기 =======");
		String id = getInput("아이디: ");
		String name = getInput("이름: ");
		
		String jumin = null;
        while (true) {
        	jumin = getInput("주민번호: ");
        	if (jumin == null) return;
        	if (checkJumin(jumin)) {
        		break;
        	}
        }
		mc.findPwd();
	}
	
	
	
	// -----  입력받기, 입력중 돌아가기 ----- //
	public String getInput(String info) {
	    System.out.print(info);
	    String input = sc.nextLine().trim();
	    return input.equals("0") ? null : input;
	}
	
	
}
