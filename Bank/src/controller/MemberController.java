package controller;

import java.util.Scanner;

import model.domain.Member;
import service.MemberService;
import view.AdminMainMenu;
import view.MemberMenu;

public class MemberController {
	private final Scanner sc = new Scanner(System.in);
	private final MemberService ms = new MemberService();

	// 1. 회원가입
	public void insertMember() {
		System.out.println("\n=========== [회원가입] ===========");
        System.out.println("※ 입력 중 '0'을 입력하면 메인메뉴로 돌아갑니다.");

        String name = getInput("이름");
        if (name == null) return;

        String jumin;
        while (true) {
            jumin = getInput("주민번호 (예:000123-1234567)");
            if (jumin == null) return;
            if (!ms.checkJumin(jumin)) continue;
            
            if (ms.confirmJumin(jumin)) {
            	System.out.println("⚠️ 이미 가입된 주민 번호 입니다. 다시 입력해주세요");
            	continue;
            }
            break;
        }

        String id;
        while (true) {
            id = getInput("아이디");
            if (id == null) return;
            if (ms.checkId(id)) {
                System.out.println("⚠️ 이미 사용 중인 아이디입니다. 다른 아이디를 입력해주세요.");
            } else {
                System.out.println("✔️ 사용 가능한 아이디입니다.");
                break;
            }
        }

        String pwd;
        String pwdConfirm;
        while (true) {
            pwd = getInput("비밀번호 (8자 이상, 영문+숫자 조합)");
            if (pwd == null) return;
            if (!ms.checkPwd(pwd)) {
            	System.out.println("⚠️ 영문+숫자, 8자 이상이어야 합니다. 다시 입력해주세요.");
            	continue;
            }

            pwdConfirm = getInput("비밀번호 확인");
            if (pwdConfirm == null) return;

            if (!ms.confirmPwd(pwd, pwdConfirm)) {
                System.out.println("❌ 비밀번호가 일치하지 않습니다. 다시 입력해주세요.");
                continue;
            }
            break; // 확인 완료
        }

        String address = getInput("주소");
        if (address == null) return;

        String phone;
        while (true) {
            phone = getInput("전화번호 (예:010-1234-5678)");
            if (phone == null) return;
            if (!ms.checkPhone(phone)) continue;
            if (ms.confirmPhone(phone)) {
                System.out.println("⚠️ 이미 가입된 핸드폰 번호 입니다. 다시 입력해주세요");
            } else {
                System.out.println("✔️ 사용 가능한 번호 입니다.");
                break;
            }
        }
        
        Member member = new Member();
        member.setName(name);
        member.setJumin(jumin);
        member.setMemberId(id);
        member.setPassword(pwd);
        member.setAddress(address);
        member.setPhone(phone);

        int result = ms.insertMember(member);
        System.out.println(result > 0 ? "✔️ 회원가입이 완료되었습니다. 메인메뉴로 이동합니다." 
        							  : "⚠️ 회원가입에 실패했습니다. 다시 시도해주세요");
	}
	
	// 로그인
	public void loginMember() {
		System.out.println("\n============ [로그인] ============");
		while (true) {
	        String id = getInput("아이디");
	        String pwd = getInput("비밀번호");
	        if (id == null || pwd == null) return;
	        Member member = ms.loginMember(id, pwd);
	        // 로그인 성공
	        if (member != null) {
	            if (member.getStatus() == 'N') {
	                System.out.println("❌ 해당 계정은 잠겨 있어 로그인할 수 없습니다. 관리자에게 문의바랍니다.");
	                break;
	            }
	            SessionManager.login(member);
	            System.out.println(member.getName() + "님 환영합니다 🙌");

	            if (SessionManager.isAdmin()) {
	                new AdminMainMenu().start();
	            } else {
	                new MemberMenu().MemberMainMenu();
	            }
	            break;
	        }

	        // 로그인 실패
	        System.out.println("⚠️ 아이디 또는 비밀번호가 올바르지 않습니다.");
	        int count = ms.getLockCount(id);
	        if (count == 5) {
	            System.out.println("❌ 비밀번호 5회 이상 틀려 계정이 잠금되었습니다.");
	        } else {
	            System.out.println("ℹ️ 비밀번호 오류 횟수: " + count + "/5");
	        }

	        System.out.print("\n다시 시도하시겠습니까? (Y/N): ");
	        String retry = sc.nextLine().trim().toUpperCase();
	        if (!retry.equals("Y")) {
	            break;
	        }
	    }
	}

	// 아이디 찾기
	public void findMemberId() {
		System.out.println("\n========== [아이디 찾기] ==========");
		String name = getInput("이름");
        if (name == null) return;

        String jumin;
        while (true) {
            jumin = getInput("주민번호");
            if (jumin == null) return;
            if (ms.checkJumin(jumin)) break;
        }

        String findId = ms.findId(name, jumin);
        if (findId != null) {
            System.out.println(name + "님의 아이디는 " + findId + " 입니다.");
        } else {
            System.out.println("⚠️ 일치하는 정보가 없습니다.");
        }
	}
	
	// 비밀번호 찾기 -> 새 비밀번호 변경
	public void findMemberPwd() {
		System.out.println("\n========= [비밀번호 재설정] =========");
		String id = getInput("아이디");
        String name = getInput("이름");

        String jumin;
        while (true) {
            jumin = getInput("주민번호");
            if (jumin == null) return;
            if (ms.checkJumin(jumin)) break;
        }
        
        // 입력받은 정보가 유효한지 먼저 검증
        if (!ms.validateUserInfo(id, name, jumin)) {
            System.out.println("⚠️ 일치하는 정보가 없습니다.");
            return;
        }
        System.out.println("✅ 입력하신 정보가 확인되었습니다.");
        System.out.println("✏️ 새로운 비밀번호를 입력해주세요.");
        String newPwd;
        String newPwdConfirm;
        while (true) {
            newPwd = getInput("새 비밀번호");
            if (newPwd == null) return;
            if (!ms.checkPwd(newPwd)) continue;

            newPwdConfirm = getInput("새 비밀번호 확인");
            if (newPwdConfirm == null) return;
            if (!ms.confirmPwd(newPwd, newPwdConfirm)) {
                System.out.println("❌ 비밀번호가 일치하지 않습니다. 다시 입력해주세요.");
                continue;
            }
            break;
        }

        int result = ms.updatePwd(id, name, jumin, newPwd);
        System.out.println(result > 0 ? "✔️ 비밀번호가 재설정되었습니다." : "⚠️ 비밀번호 변경에 실패했습니다.");
	}
	
	// 잠금 계정 관리자 문의
	public void isAccountLocked() {
		System.out.println("\n========== [관리자 문의] ==========");
		String id;
		
		id = getInput("잠금 계정 아이디");
		if (id == null) return;
		
		int result = ms.isAccountLocked(id);
		switch (result) {
		    case 1:
		        System.out.println("🔒 해당 계정은 현재 잠금 상태입니다.");
		        System.out.println("✅ 관리자에게 문의가 접수되었습니다.");
		        break;
		    case 0:
		        System.out.println("ℹ️ 해당 계정은 잠금 상태가 아닙니다.");
		        break;
		    case -1:
		        System.out.println("❌ 존재하지 않는 아이디입니다.");
		        break;
		    default:
		        System.out.println("⚠️ 오류가 발생했습니다.");
		}
		
	}
	
	// 입력 정렬, 입력중 되돌아가기
	public String getInput(String label) {
	    System.out.printf("%-1s: ", label);  // 왼쪽 정렬 + 폭 고정
	    String input = sc.nextLine().trim();
	    return input.equals("0") ? null : input;
	}


} // MemberController
