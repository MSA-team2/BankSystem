package controller;

import java.util.Scanner;

import model.MemberVO;
import service.AdminMemberService;
import util.Validator;

public class AdminMemberController {
	
	private final Scanner sc = new Scanner(System.in);
	private final AdminMemberService adminMemberService = new AdminMemberService();
	
	public void editMember() {
        System.out.println("===== 회원 정보 수정 =====");
        System.out.print("회원 이름 : ");
        String inputName = sc.nextLine();

        System.out.print("주민번호 : ");
        String inputJumin = sc.nextLine();

        System.out.print("새 전화번호 : ");
        String newPhone = sc.nextLine();

        System.out.print("새 주소 : ");
        String newAddress = sc.nextLine();

        String result = adminMemberService.updateMemberInfo(inputName, inputJumin, newPhone, newAddress);
        System.out.println(result);
    }

    public void findMember() {
        System.out.println("===== 회원 검색 =====");
        System.out.print("회원 이름 : ");
        String name = sc.nextLine();

        System.out.print("주민번호 : ");
        String jumin = sc.nextLine();

        MemberVO member = adminMemberService.findMemberByNameAndJumin(name, jumin);
        if (member == null) {
            System.out.println("[!] 회원을 찾을 수 없습니다.");
            return;
        }

        System.out.println("===== 회원 정보 =====");
        System.out.println("이름 : " + member.getName());
        System.out.println("주민번호 : " + member.getJumin());
        System.out.println("전화번호 : " + member.getPhone());
        System.out.println("주소 : " + member.getAddress());
    }

    public void manageAccountLock() {
        if (!SessionManager.isAdmin()) {
            System.out.println("[!] 관리자만 접근할 수 있는 기능입니다.");
            return;
        }

        System.out.println("===== 계좌 잠금 관리 =====");
        System.out.print("회원 이름 : ");
        String name = sc.nextLine();

        System.out.print("주민번호 : ");
        String jumin = sc.nextLine();

        System.out.print("잠금/해제할 계좌번호를 입력하세요: ");
        String accountNo = sc.nextLine();

        if (!Validator.isValidHyphenAccountNumber(accountNo)) {
            System.out.println("[!] 잘못된 계좌번호 형식입니다. 예) 100-1234-5678");
            return;
        }

        System.out.print("변경할 상태 (Y/N): ");
        char status = sc.nextLine().toUpperCase().charAt(0);

        String result = adminMemberService.changeAccountStatus(name, jumin, accountNo, status);
        System.out.println(result);
    }
}
