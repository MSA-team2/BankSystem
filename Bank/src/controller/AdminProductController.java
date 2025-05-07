package controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import model.ProductVO;
import service.AdminProductService;

public class AdminProductController {
    private final Scanner sc = new Scanner(System.in);
    private final AdminProductService adminProductService = new AdminProductService();

    // 모든 상품 조회
    public void getAllProducts() {    
        System.out.println("\n============ [모든 상품 조회] ============");
        
        List<ProductVO> findProducts = adminProductService.getAllProducts();
        Map<String, Integer> stats = adminProductService.calculateProductStats(findProducts);
        
        displayProductList(findProducts);
        displayStatistics(stats);
        displayCurrentDateTime("조회");
    }

    // 상품 추가
    public void addProduct() {
        System.out.println("\n============ [상품 등록] ============");
        
        // 상품 정보 입력 받기
        String productName = inputProductName(null);
        int productType = inputProductType();
        BigDecimal interestRate = inputInterestRate(null);
        int periodMonths = inputPeriodMonths(null, productType);
        
        // 상품 유형별 추가 정보 입력
        BigDecimal maxDepositAmount = null;
        BigDecimal maxMonthlyDeposit = null;
        
        if (adminProductService.isSavingsAccount(productType)) {
            maxMonthlyDeposit = inputMoneyAmount(
                    "최대 월 납입액 입력(원)",
                    null,
                    new BigDecimal("300000"));
        } else if (adminProductService.isDepositAccount(productType)) {
            maxDepositAmount = inputMoneyAmount(
                    "최대 예치금액 입력(원)",
                    null,
                    new BigDecimal("1000000"));
        }
        
        // 상품 객체 생성
        ProductVO product = adminProductService.createProductVO(0, productName, productType, interestRate, 
                periodMonths, maxDepositAmount, maxMonthlyDeposit);
        
        // 상품 정보 확인
        displayProductDetails(product, "📋 상품 정보 확인 📋");
        
        // 확인 요청
        if (confirmAction("이 정보로 상품을 등록하시겠습니까?")) {
            boolean result = adminProductService.addProduct(product);
            
            if (result) {
                displaySuccess("등록");
            } else {
                displayError("등록");
            }
        } else {
            displayCancel("등록");
        }
    }
    
    // 상품 수정
    public void updateProduct() {
        System.out.println("\n============ [상품 수정] ============");
        
        // 수정할 상품 ID 입력
        Integer productId = inputProductId("수정");
        if (productId == null) {
            return;
        }
        
        // 기존 상품 정보 조회
        ProductVO product = adminProductService.getProductById(productId);
        if (product == null || product.getProductId() == 0) {
            displayWarning("해당 ID의 상품이 존재하지 않습니다. 수정을 취소합니다.");
            return;
        }
        
        // 현재 상품 정보 표시
        displayProductDetails(product, "📋 현재 상품 정보 📋");
        
        // 새 상품 정보 입력
        System.out.println("\n📝 수정할 정보를 입력하세요. (변경하지 않으려면 엔터)");
        
        String productName = inputProductName(product.getProductName());
        BigDecimal interestRate = inputInterestRate(product.getInterestRate());
        int periodMonths = inputPeriodMonths(product.getPeriodMonths(), product.getProduct_type());
        
        // 상품 유형별 추가 정보 입력
        BigDecimal maxDepositAmount = product.getMaxDepositAmount();
        BigDecimal maxMonthlyDeposit = product.getMaxMonthlyDeposit();
        
        if (adminProductService.isSavingsAccount(product.getProduct_type())) {
            maxMonthlyDeposit = inputMoneyAmount(
                    "새 최대 월 납입액",
                    product.getMaxMonthlyDeposit(),
                    new BigDecimal("300000"));
        } else if (adminProductService.isDepositAccount(product.getProduct_type())) {
            maxDepositAmount = inputMoneyAmount(
                    "새 최대 예치금액",
                    product.getMaxDepositAmount(),
                    new BigDecimal("1000000"));
        }
        
        // 상품 객체 업데이트
        ProductVO updateProduct = adminProductService.createProductVO(productId, productName, product.getProduct_type(), 
                interestRate, periodMonths, maxDepositAmount, maxMonthlyDeposit);
        
        // 수정 정보 확인
        displayProductDetails(updateProduct, "📝 수정할 상품 정보 📝");
        
        // 확인 요청
        if (confirmAction("이 정보로 상품을 수정하시겠습니까?")) {
            boolean result = adminProductService.updateProduct(updateProduct);
            
            if (result) {
                displaySuccess("수정");
            } else {
                displayError("수정");
            }
        } else {
            displayCancel("수정");
        }
    }
    
    // 상품 삭제
    public void deleteProduct() {
        System.out.println("\n============ [상품 삭제] ============");
        
        // 삭제할 상품 ID 입력
        Integer productId = inputProductId("삭제");
        if (productId == null) {
            return;
        }
        
        // 기존 상품 정보 조회
        ProductVO product = adminProductService.getProductById(productId);
        if (product == null || product.getProductId() == 0) {
            displayWarning("해당 ID의 상품이 존재하지 않습니다. 삭제를 취소합니다.");
            return;
        }
        
        // 삭제할 상품 정보 표시
        displayProductDetails(product, "⚠️ 삭제할 상품 정보 ⚠️");
        
        // 삭제 확인
        if (confirmDelete(product.getProductName())) {
            boolean result = adminProductService.deleteProduct(productId);
            
            if (result) {
                displaySuccess("삭제");
            } else {
                displayError("삭제");
                System.out.println("💡 이 상품에 가입한 계좌가 있는지 확인하세요.");
            }
        } else {
            displayCancel("삭제");
        }
    }
    
    // ===== 입력 처리 메서드 =====
    
    // 상품명 입력
    private String inputProductName(String currentName) {
        if (currentName != null) {
            System.out.print("📌 새 상품명 [" + currentName + "]: ");
            String input = sc.nextLine();
            if (input.trim().isEmpty()) {
                System.out.println("ℹ️ 상품명을 유지합니다: " + currentName);
                return currentName;
            }
            return input;
        } else {
            System.out.print("\n📝 상품명 입력: ");
            return sc.nextLine();
        }
    }
    
    // 상품 유형 선택
    private int inputProductType() {
        System.out.println("\n📋 상품유형 선택: ");
        System.out.println("┌─────────────────────────────┐");
        System.out.println("│  1. 💳 입출금 계좌 (100)     │");
        System.out.println("│  2. 💰 적금 상품 (200)       │");
        System.out.println("│  3. 💵 예금 상품 (300)       │");
        System.out.println("└─────────────────────────────┘");
        System.out.print("👉 선택: ");
        String typeChoice = sc.nextLine();
        
        int productType = adminProductService.getProductTypeFromChoice(typeChoice);
        
        if (!typeChoice.matches("[1-3]")) {
            System.out.println("⚠️ 잘못된 선택입니다. 입출금(100)으로 자동 설정합니다.");
        }
        
        return productType;
    }
    
    // 금리 입력
    private BigDecimal inputInterestRate(BigDecimal currentRate) {
        BigDecimal interestRate;
        
        if (currentRate != null) {
            System.out.print("📌 새 금리(%) [" + currentRate + "%]: ");
            String input = sc.nextLine();
            
            if (input.trim().isEmpty()) {
                System.out.println("ℹ️ 금리를 유지합니다: " + currentRate + "%");
                return currentRate;
            }
            
            try {
                interestRate = new BigDecimal(input);
            } catch (NumberFormatException e) {
                System.out.println("⚠️ 유효하지 않은 숫자입니다. 기존 금리(" + currentRate + "%)를 유지합니다.");
                interestRate = currentRate;
            }
        } else {
            System.out.print("\n📊 금리(%) 입력: ");
            
            try {
                interestRate = new BigDecimal(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("⚠️ 유효하지 않은 숫자입니다. 기본값 0.1%로 설정합니다.");
                interestRate = new BigDecimal("0.1");
            }
        }
        
        return interestRate;
    }
    
    // 가입 기간 선택
    private int inputPeriodMonths(Integer currentPeriod, int productType) {
        // 입출금 계좌는 가입 기간이 없음
        if (adminProductService.isCheckingAccount(productType)) {
            System.out.println("ℹ️ 입출금 계좌는 가입 기간이 없어 0개월로 자동 설정됩니다.");
            return 0;
        }
        
        String currentPeriodText = currentPeriod != null ? " [현재: " + currentPeriod + "개월]" : "";
        System.out.println("\n⏱️ 가입 기간(개월) 선택" + currentPeriodText + ":");
        System.out.println("┌─────────────────────────────┐");
        System.out.println("│  1. 12개월 (1년)            │");
        System.out.println("│  2. 24개월 (2년)            │");
        System.out.println("│  3. 36개월 (3년)            │");
        if (currentPeriod != null) {
            System.out.println("│  4. 변경하지 않음           │");
        }
        System.out.println("└─────────────────────────────┘");
        System.out.print("👉 선택: ");
        
        String periodChoice = sc.nextLine();
        return adminProductService.getPeriodFromChoice(periodChoice, currentPeriod);
    }
    
    // 금액 입력
    private BigDecimal inputMoneyAmount(String prompt, BigDecimal currentAmount, BigDecimal defaultAmount) {
        String currentAmountStr = currentAmount != null ? 
            adminProductService.formatCurrency(currentAmount) + "원" : "설정되지 않음";
            
        System.out.print("\n💰 " + prompt + " [" + currentAmountStr + "]: ");
        String input = sc.nextLine();
        
        if (currentAmount != null && input.trim().isEmpty()) {
            return currentAmount;
        }
        
        try {
            BigDecimal amount = new BigDecimal(input);
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                System.out.println("⚠️ 금액은 0보다 커야 합니다. " + 
                        (currentAmount != null ? "기존 값을 유지합니다." : "기본값으로 설정합니다."));
                return currentAmount != null ? currentAmount : defaultAmount;
            }
            return amount;
        } catch (NumberFormatException e) {
            System.out.println("⚠️ 유효하지 않은 숫자입니다. " + 
                    (currentAmount != null ? "기존 값을 유지합니다." : "기본값으로 설정합니다."));
            return currentAmount != null ? currentAmount : defaultAmount;
        }
    }
    
    // 확인 요청
    private boolean confirmAction(String message) {
        System.out.print("\n✅ " + message + " (Y/N) ");
        String confirm = sc.nextLine();
        return confirm.equalsIgnoreCase("Y");
    }
    
    // 삭제 확인 (2단계)
    private boolean confirmDelete(String productName) {
        System.out.println("\n⚠️ 주의: 상품을 삭제하면 복구할 수 없습니다!");
        System.out.print("❓ 이 상품을 정말 삭제하시겠습니까? (Y/N): ");
        String confirm = sc.nextLine();
        
        if (confirm.equalsIgnoreCase("Y")) {
            System.out.print("\n⚠️ 최종 확인: 상품 [" + productName + "]을(를) 삭제합니다. 진행하시겠습니까? (DELETE 입력): ");
            String finalConfirm = sc.nextLine();
            return finalConfirm.equalsIgnoreCase("DELETE");
        }
        
        return false;
    }
    
    // ID 입력
    private Integer inputProductId(String action) {
        System.out.print("\n🔍 " + action + "할 상품의 ID를 입력하세요: ");
        try {
            return Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("❌ 유효하지 않은 ID입니다. " + action + "을 취소합니다.");
            return null;
        }
    }
    
    // ===== 화면 출력 메서드 =====
    
    // 상품 목록 표시
    private void displayProductList(List<ProductVO> products) {
        // 컬럼 헤더
        System.out.println("+---------+------------------+--------------+--------+--------------+--------------------+--------------------+");
        System.out.println("| 상품ID  | 상품명           | 상품유형     | 금리(%) | 가입기간(월) | 최대 예치금액      | 최대 월 납입액      |");
        System.out.println("+---------+------------------+--------------+--------+--------------+--------------------+--------------------+");
        
        // 데이터 출력
        for (ProductVO p : products) {
            String productType = adminProductService.getProductTypeName(p.getProduct_type());
            String interestRate = String.format("%.2f%%", p.getInterestRate());
            
            // 상품 유형에 따라 최대 예치금액과 최대 월 납입액 표시
            String maxDepositAmount = "-";
            String maxMonthlyDeposit = "-";
            
            if (adminProductService.isSavingsAccount(p.getProduct_type())) {
                if (p.getMaxMonthlyDeposit() != null) {
                    maxMonthlyDeposit = adminProductService.formatCurrency(p.getMaxMonthlyDeposit()) + "원";
                }
            } else if (adminProductService.isDepositAccount(p.getProduct_type())) {
                if (p.getMaxDepositAmount() != null) {
                    maxDepositAmount = adminProductService.formatCurrency(p.getMaxDepositAmount()) + "원";
                }
            }
            
            System.out.printf("| %-7s | %-16s | %-12s | %-6s | %-12s | %-18s | %-18s |\n", 
                    p.getProductId(), 
                    p.getProductName(), 
                    productType, 
                    interestRate, 
                    p.getPeriodMonths() + "개월",
                    maxDepositAmount,
                    maxMonthlyDeposit);
        }
        
        // 푸터
        System.out.println("+---------+------------------+--------------+--------+--------------+--------------------+--------------------+");
    }
    
    // 통계 정보 표시
    private void displayStatistics(Map<String, Integer> stats) {
        System.out.println("\n📊 총 " + stats.get("total") + "개의 상품이 등록되어 있습니다.");
        System.out.println("📌 상품 유형별 통계:");
        System.out.println("   - 💳 입출금 상품: " + stats.get("checking") + "개");
        System.out.println("   - 💰 적금 상품: " + stats.get("savings") + "개");
        System.out.println("   - 💵 예금 상품: " + stats.get("deposit") + "개");
    }
    
 // 단일 상품 정보 출력 (이어서)
    private void displayProductDetails(ProductVO product, String title) {
        if (product == null) {
            System.out.println("❌ 상품 정보가 없습니다.");
            return;
        }
        
        String productTypeName = adminProductService.getProductTypeName(product.getProduct_type());
        
        System.out.println("\n+--------------------------------------------+");
        System.out.println("|          " + title + "          |");
        System.out.println("+--------------------------------------------+");
        
        if (product.getProductId() > 0) {
            System.out.println("│ 상품ID    : " + product.getProductId());
        }
        
        System.out.println("│ 상품명    : " + product.getProductName());
        System.out.println("│ 상품유형  : " + productTypeName + " (" + product.getProduct_type() + ")");
        System.out.println("│ 금리      : " + product.getInterestRate() + "%");
        System.out.println("│ 가입기간  : " + product.getPeriodMonths() + "개월");
        
        // 상품 유형에 따라 추가 정보 표시
        if (adminProductService.isSavingsAccount(product.getProduct_type())) {
            System.out.println("│ 최대 월 납입액: " + adminProductService.formatCurrency(product.getMaxMonthlyDeposit()) + "원");
        } else if (adminProductService.isDepositAccount(product.getProduct_type())) {
            System.out.println("│ 최대 예치금액: " + adminProductService.formatCurrency(product.getMaxDepositAmount()) + "원");
        }
        
        System.out.println("+--------------------------------------------+");
    }
    
    // 상품 등록/수정/삭제 성공 메시지
    private void displaySuccess(String operation) {
        System.out.println("\n🎉 성공! 상품이 성공적으로 " + operation + "되었습니다.");
        displayCurrentDateTime(operation);
    }
    
    // 상품 등록/수정/삭제 실패 메시지
    private void displayError(String operation) {
        System.out.println("\n❌ 오류! 상품 " + operation + "에 실패했습니다.");
        System.out.println("💡 시스템 관리자에게 문의하세요.");
    }
    
    // 취소 메시지
    private void displayCancel(String operation) {
        System.out.println("\n🔄 상품 " + operation + "이 취소되었습니다.");
    }
    
    // 현재 날짜/시간 출력
    private void displayCurrentDateTime(String operation) {
        System.out.println("📆 " + operation + " 시간: " + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }
    
    // 경고 메시지
    private void displayWarning(String message) {
        System.out.println("⚠️ " + message);
    }
}