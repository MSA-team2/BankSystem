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
            System.out.print(" 👉 메뉴를 선택하세요: ");
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
                	adminProductController.getAllProducts();
                    adminProductController.updateProduct();
                    adminProductController.getAllProducts();
                    break;
                case "4":
                	adminProductController.getAllProducts();
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
    	System.out.println("\n================================");
        System.out.println("\t🛠️ 상품 관리 메뉴");
        System.out.println("================================");
        System.out.println("\t[1] 전체 상품 조회");
        System.out.println("\t[2] 상품 등록");
        System.out.println("\t[3] 상품 수정");
        System.out.println("\t[4] 상품 삭제");
        System.out.println("\t[0] 뒤로 가기");
        System.out.println("================================");
    }
}
