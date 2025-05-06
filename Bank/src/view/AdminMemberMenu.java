package view;

import java.util.Scanner;

import controller.AdminMemberController;

public class AdminMemberMenu {
	private final Scanner sc = new Scanner(System.in);
    private final AdminMemberController adminMemberController = new AdminMemberController();

    public void start() {
        while (true) {
            printMenu();
            System.out.print("메뉴 선택 >> ");
            String input = sc.nextLine();

            switch (input) {
                case "1":
                	adminMemberController.findAllMembers();
                    break;
                case "2":
                	adminMemberController.findAllMembers();
                	adminMemberController.findMember();
                    break;
                case "3":
                	adminMemberController.findAllMembers();
                	adminMemberController.editMember();
                    break;
                case "4":
                	adminMemberController.findAllMembers();
                	adminMemberController.manageLockedAccounts();
                    break;
                case "0":
                    System.out.println("회원 관리 메뉴를 종료합니다.");
                    return;
                default:
                    System.out.println("잘못된 입력입니다.");
            }
        }
    }

    private void printMenu() {
        System.out.println("\n===== 회원 관리 메뉴 =====");
        System.out.println("1. 전체 회원 조회");
        System.out.println("2. 회원 검색");
        System.out.println("3. 회원 정보 수정");
        System.out.println("4. 잠금 계정 관리");
        System.out.println("0. 뒤로 가기");
    }
}
