package view;

import java.util.Scanner;

import controller.AdminController;
import controller.AdminMemberController;
import controller.MemberController;

public class AdminMainMenu {
	
	private final AdminMenu adminMenu = new AdminMenu();
	private final AdminMemberMenu adminMemberMenu = new AdminMemberMenu();
	private final AdminProductMenu adminProductMenu = new AdminProductMenu();
	private final Scanner sc = new Scanner(System.in);
	
	public void start() {
        while (true) {
        	printMenu();
        	System.out.print(" 👉 메뉴를 선택하세요: ");
            String input = sc.nextLine();

            switch (input) {
                case "1":
                	adminMenu.start();
                    break;
                case "2":
                	adminMemberMenu.start();
                    break;
                case "3":
                	adminProductMenu.start();
                    break;
                case "0":
                    return;
                default:
                    System.out.println("잘못된 입력입니다.");
            }
        }
    }
	
	private void printMenu() {
		System.out.println("\n================================");
		System.out.println("\t👑 관리자 메인 메뉴");
		System.out.println("================================");
        System.out.println("\t[1] 계좌 관리");
        System.out.println("\t[2] 회원 관리");
        System.out.println("\t[3] 상품 관리");
        System.out.println("\t[0] 뒤로 가기");
        System.out.println("================================");
    }
}
