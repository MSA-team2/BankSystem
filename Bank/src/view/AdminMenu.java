package view;

import java.util.Scanner;

import controller.AdminController;

public class AdminMenu {
	private final Scanner sc = new Scanner(System.in);
    private final AdminController adminController = new AdminController();

    public void start() {
        while (true) {
            printMenu();
            System.out.print("원하는 메뉴를 선택하세요: ");
            String input = sc.nextLine();

            switch (input) {
                case "1":
                    adminController.selectAllAccounts();
                    break;
                case "2":
                    adminController.selectAllMembers();
                    break;
                case "3":
                    adminController.showDailyTransferHistory();
                    break;
                case "0":
                    System.out.println("관리자 모드를 종료합니다.");
                    return;
                default:
                    System.out.println("잘못된 입력입니다. 다시 시도하세요.");
            }

            System.out.println();
        }
    }

    private void printMenu() {
        System.out.println("\n===== 관리자 메뉴 =====");
        System.out.println("1. 전체 계좌 조회");
        System.out.println("2. 전체 회원 조회");
        System.out.println("3. 일일 거래량 조회");
        System.out.println("0. 종료");
    }
}
