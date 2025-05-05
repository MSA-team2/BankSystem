package view;

import java.math.BigDecimal;
import java.util.Scanner;

import controller.AdminProductController;

public class AdminProductMenu {
	private final Scanner sc = new Scanner(System.in);
    private final AdminProductController adminProductController = new AdminProductController();
    
    public void start() {
        while (true) {
            printMenu();
            System.out.print("메뉴 선택 >> ");
            String input = sc.nextLine();
            
            switch (input) {
                case "1":
                    adminProductController.getAllProducts();
                    break;
                case "2":
                    adminProductController.addProduct();
                    adminProductController.getAllProducts();
                    break;
                case "3":
                    adminProductController.updateProduct();
                    adminProductController.getAllProducts();
                    break;
                case "4":
                    adminProductController.deleteProduct();
                    adminProductController.getAllProducts();
                    break;
                case "0":
                    System.out.println("상품 관리 메뉴를 종료합니다.");
                    return;
                default:
                    System.out.println("잘못된 입력입니다.");
            }
        }
    }
    
    private void printMenu() {
        System.out.println("\n===== 상품 관리 메뉴 =====");
        System.out.println("1. 전체 상품 조회");
        System.out.println("2. 상품 추가");
        System.out.println("3. 상품 수정");
        System.out.println("4. 상품 삭제");
        System.out.println("0. 뒤로 가기");
    }
}
