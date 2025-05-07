package controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

import model.ProductVO;
import service.AdminProductService;

public class AdminProductController {
	private final Scanner sc = new Scanner(System.in);
	private final AdminProductService adminProductService = new AdminProductService();

	// 모든 상품 조회
	public void getAllProducts() {	
		List<ProductVO> findProducts = adminProductService.getAllProducts();
	    
		// 헤더
	    System.out.println("\n+----------------------------------------------------------------------------+");
	    System.out.println("|                        📋 전체 상품 조회 📋                              |");
	    System.out.println("+----------------------------------------------------------------------------+");
	    
	    // 컬럼 헤더
	    System.out.println("+---------+------------------+--------------+--------+--------------+--------------------+--------------------+");
	    System.out.println("| 상품ID  | 상품명           | 상품유형     | 금리(%) | 가입기간(월) | 최대 예치금액      | 최대 월 납입액      |");
	    System.out.println("+---------+------------------+--------------+--------+--------------+--------------------+--------------------+");
	    
	    // 상품 유형 설명을 위한 맵
	    java.util.Map<Integer, String> productTypeMap = new java.util.HashMap<>();
	    productTypeMap.put(100, "💳 입출금");
	    productTypeMap.put(200, "💰 적금");
	    productTypeMap.put(300, "💵 예금");
	    
	    // 데이터 출력
	    for (ProductVO p : findProducts) {
	        String productType = productTypeMap.getOrDefault(p.getProduct_type(), String.valueOf(p.getProduct_type()));
	        
	        String interestRate = String.format("%.2f%%", p.getInterestRate());
	        
	        // 상품 유형에 따라 최대 예치금액과 최대 월 납입액 표시
	        String maxDepositAmount = "-";
	        String maxMonthlyDeposit = "-";
	        
	        switch(p.getProduct_type()) {
		        case 200:  // 적금 상품
	                if (p.getMaxMonthlyDeposit() != null) {
	                    maxMonthlyDeposit = formatCurrency(p.getMaxMonthlyDeposit()) + "원";
	                }
	                break;
	        
	            case 300:  // 예금 상품
	                if (p.getMaxDepositAmount() != null) {
	                    maxDepositAmount = formatCurrency(p.getMaxDepositAmount()) + "원";
	                }
	                break;
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
	    System.out.println("+---------+------------------+--------------+--------+--------------+");
	    
	    // 통계 정보
	    int totalProducts = findProducts.size();
	    int checkingCount = 0;
	    int depositCount = 0;
	    int savingsCount = 0;
	    
	    for (ProductVO p : findProducts) {
	        switch (p.getProduct_type()) {
	            case 100: 
	            	checkingCount++; // 입출금
	            	break;
	            case 200: 
	            	savingsCount++; // 적금
	            	break;
	            case 300: 
	            	depositCount++; // 예금
	            	break;
	        }
	    }
	    
	    // 상품 통계 출력
	    System.out.println("\n📊 총 " + totalProducts + "개의 상품이 등록되어 있습니다.");
	    System.out.println("📌 상품 유형별 통계:");
	    System.out.println("   - 💳 입출금 상품: " + checkingCount + "개");
	    System.out.println("   - 💰 적금 상품: " + savingsCount + "개");
	    System.out.println("   - 💵 예금 상품: " + depositCount + "개");
	    
	    // 조회 시간
	    System.out.println("\n🕒 조회 일시: " + java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
	}

	// 상품 추가
	public void addProduct() {
		// 헤더
	    System.out.println("\n+--------------------------------------------+");
	    System.out.println("|          ✨ 새 금융 상품 등록 ✨           |");
	    System.out.println("+--------------------------------------------+");
	    
	    // 상품명 입력
	    System.out.print("\n📝 상품명 입력: ");
	    String productName = sc.nextLine();
	    
	    // 상품유형 선택
	    System.out.println("\n📋 상품유형 선택: ");
	    System.out.println("┌─────────────────────────────┐");
	    System.out.println("│  1. 💳 입출금 계좌 (100)     │");
	    System.out.println("│  2. 💰 적금 상품 (200)       │");
	    System.out.println("│  3. 💵 예금 상품 (300)       │");
	    System.out.println("└─────────────────────────────┘");
	    System.out.print("👉 선택: ");
	    String typeChoice = sc.nextLine();
	    
	    int productType;
	    String productTypeName;
	    switch(typeChoice) {
	        case "1": 
	            productType = 100; 
	            productTypeName = "💳 입출금 계좌";
	            break;
	        case "2": 
	            productType = 200; 
	            productTypeName = "💰 적금 상품";
	            break;
	        case "3": 
	            productType = 300; 
	            productTypeName = "💵 예금 상품";
	            break;
	        default:
	            System.out.println("⚠️ 잘못된 선택입니다. 입출금(100)으로 자동 설정합니다.");
	            productType = 100;
	            productTypeName = "💳 입출금 계좌";
	    }
	    
	    // 금리 입력
	    System.out.print("\n📊 금리(%) 입력: ");
	    BigDecimal interestRate;
	    try {
	        interestRate = new BigDecimal(sc.nextLine());
	    } catch (NumberFormatException e) {
	        System.out.println("⚠️ 유효하지 않은 숫자입니다. 기본값 0.1%로 설정합니다.");
	        interestRate = new BigDecimal("0.1");
	    }
	    
	    // 가입 기간 선택 (입출금 계좌인 경우 스킵)
	    int periodMonths;
	    if (productType == 100) { // 입출금 계좌
	        periodMonths = 0; // 입출금 계좌는 가입 기간이 없음
	        System.out.println("ℹ️ 입출금 계좌는 가입 기간이 없어 0개월로 자동 설정됩니다.");
	    } else {
	        System.out.println("\n⏱️ 가입 기간(개월) 선택:");
	        System.out.println("┌─────────────────────────────┐");
	        System.out.println("│  1. 12개월 (1년)            │");
	        System.out.println("│  2. 24개월 (2년)            │");
	        System.out.println("│  3. 36개월 (3년)            │");
	        System.out.println("└─────────────────────────────┘");
	        System.out.print("👉 선택: ");
	        String periodChoice = sc.nextLine();
	        
	        switch(periodChoice) {
	            case "1": periodMonths = 12; break;
	            case "2": periodMonths = 24; break;
	            case "3": periodMonths = 36; break;
	            default:
	                System.out.println("⚠️ 잘못된 선택입니다. 12개월로 자동 설정합니다.");
	                periodMonths = 12;
	        }
	    }
	    
	    // 상품 유형별 추가 정보 입력
	    BigDecimal maxDepositAmount = null;  // 예금 상품의 최대 예치금
	    BigDecimal maxMonthlyDeposit = null; // 적금 상품의 최대 월 납입액
	    
	    switch(productType) {
		    case 200:  // 적금 상품
	            System.out.print("\n💰 최대 월 납입액 입력(원): ");
	            try {
	                maxMonthlyDeposit = new BigDecimal(sc.nextLine());
	                if (maxMonthlyDeposit.compareTo(BigDecimal.ZERO) <= 0) {
	                    System.out.println("⚠️ 월 납입액은 0보다 커야 합니다. 300,000원으로 설정합니다.");
	                    maxMonthlyDeposit = new BigDecimal("300000");
	                }
	            } catch (NumberFormatException e) {
	                System.out.println("⚠️ 유효하지 않은 숫자입니다. 기본값 300,000원으로 설정합니다.");
	                maxMonthlyDeposit = new BigDecimal("300000");
	            }
	            break;
	    
	        case 300:  // 예금 상품
	            System.out.print("\n💵 최대 예치금액 입력(원): ");
	            try {
	                maxDepositAmount = new BigDecimal(sc.nextLine());
	                if (maxDepositAmount.compareTo(BigDecimal.ZERO) <= 0) {
	                    System.out.println("⚠️ 예치금액은 0보다 커야 합니다. 1,000,000원으로 설정합니다.");
	                    maxDepositAmount = new BigDecimal("1000000");
	                }
	            } catch (NumberFormatException e) {
	                System.out.println("⚠️ 유효하지 않은 숫자입니다. 기본값 1,000,000원으로 설정합니다.");
	                maxDepositAmount = new BigDecimal("1000000");
	            }
	            break;           
            
	        case 100:  // 입출금 계좌
	        default:
	            // 추가 정보 필요 없음
	            break;
	    }
	    
	    // 상품 정보 확인
	    System.out.println("\n+--------------------------------------------+");
	    System.out.println("|          📋 상품 정보 확인 📋             |");
	    System.out.println("+--------------------------------------------+");
	    System.out.println("│ 상품명    : " + productName);
	    System.out.println("│ 상품유형  : " + productTypeName + " (" + productType + ")");
	    System.out.println("│ 금리      : " + interestRate + "%");
	    System.out.println("│ 가입기간  : " + periodMonths + "개월");
	    
	    
	    // 상품 유형별 추가 정보 표시
	    switch(productType) {
		    case 200:  // 적금 상품
	            System.out.println("│ 최대 월 납입액: " + formatCurrency(maxMonthlyDeposit) + "원");
	            break;
	        case 300:  // 예금 상품
	            System.out.println("│ 최대 예치금액: " + formatCurrency(maxDepositAmount) + "원");
	            break;
	    }
	    System.out.println("+--------------------------------------------+");
	    
	    // 확인 요청
	    System.out.print("\n✅ 이 정보로 상품을 등록하시겠습니까? (Y/N) ");
	    String confirm = sc.nextLine();
	    
	    if (confirm.equalsIgnoreCase("Y")) {
	        // 상품 객체 생성 및 등록
	        ProductVO addProductVO = new ProductVO();
	        addProductVO.setProductName(productName);
	        addProductVO.setProduct_type(productType);
	        addProductVO.setInterestRate(interestRate);
	        addProductVO.setPeriodMonths(periodMonths);
	        
	        // 상품 유형별 추가 정보 설정 - 수정된 부분
	        if (productType == 200) {  // 적금 상품
	            addProductVO.setMaxMonthlyDeposit(maxMonthlyDeposit); // 적금 상품에는 월 납입액 설정
	        } else if (productType == 300) {  // 예금 상품
	            addProductVO.setMaxDepositAmount(maxDepositAmount); // 예금 상품에는 최대 예치금액 설정
	        }
	        
	        boolean result = adminProductService.addProduct(addProductVO);
	        
	        if (result) {
	            System.out.println("\n🎉 성공! 상품이 성공적으로 등록되었습니다.");
	            System.out.println("📆 등록 시간: " + java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
	        } else {
	            System.out.println("\n❌ 오류! 상품 등록에 실패했습니다.");
	            System.out.println("💡 시스템 관리자에게 문의하세요.");
	        }
	    } else {
	        System.out.println("\n🔄 상품 등록이 취소되었습니다.");
	    }
	}
	
	// 상품 수정
	public void updateProduct() {
		 // 헤더
	    System.out.println("\n+--------------------------------------------+");
	    System.out.println("|          🔄 금융 상품 정보 수정 🔄         |");
	    System.out.println("+--------------------------------------------+");
	    
	    // 수정할 상품 ID 입력
	    System.out.print("\n🔍 수정할 상품의 ID를 입력하세요: ");
	    int productId;
	    try {
	        productId = Integer.parseInt(sc.nextLine());
	    } catch (NumberFormatException e) {
	        System.out.println("❌ 유효하지 않은 ID입니다. 수정을 취소합니다.");
	        return;
	    }
	    
	    // 기존 상품 정보 조회
	    ProductVO product = adminProductService.getProductById(productId);
	    if (product == null || product.getProductId() == 0) {
	        System.out.println("❌ 해당 ID의 상품이 존재하지 않습니다. 수정을 취소합니다.");
	        return;
	    }
	    
	    // 상품 유형에 대한 이름 매핑
	    String productTypeName;
	    switch(product.getProduct_type()) {
	        case 100: productTypeName = "💳 입출금 계좌"; break;
	        case 200: productTypeName = "💰 적금 상품"; break;
	        case 300: productTypeName = "💵 예금 상품"; break;
	        default: productTypeName = String.valueOf(product.getProduct_type());
	    }
	    
	    // 현재 상품 정보 표시
	    System.out.println("\n+--------------------------------------------+");
	    System.out.println("|          📋 현재 상품 정보 📋             |");
	    System.out.println("+--------------------------------------------+");
	    System.out.println("│ 상품ID    : " + product.getProductId());
	    System.out.println("│ 상품명    : " + product.getProductName());
	    System.out.println("│ 상품유형  : " + productTypeName + " (" + product.getProduct_type() + ")");
	    System.out.println("│ 금리      : " + product.getInterestRate() + "%");
	    System.out.println("│ 가입기간  : " + product.getPeriodMonths() + "개월");
	    
	    // 상품 유형에 따라 추가 정보 표시
	    switch(product.getProduct_type()) {
	        case 200: // 적금 상품
	            System.out.println("│ 최대 월 납입액: " + formatCurrency(product.getMaxMonthlyDeposit()) + "원");
	            break;
	        case 300: // 예금 상품
	            System.out.println("│ 최대 예치금액: " + formatCurrency(product.getMaxDepositAmount()) + "원");
	            break;
	    }
	    
	    System.out.println("+--------------------------------------------+");
	    
	    // 새 상품 정보 입력
	    System.out.println("\n📝 수정할 정보를 입력하세요. (변경하지 않으려면 엔터)");
	    
	    // 새 상품명 입력
	    System.out.print("📌 새 상품명 [" + product.getProductName() + "]: ");
	    String productName = sc.nextLine();
	    if (productName.trim().isEmpty()) {
	        productName = product.getProductName();
	        System.out.println("ℹ️ 상품명을 유지합니다: " + productName);
	    }
	    
	    // 새 금리 입력
	    System.out.print("📌 새 금리(%) [" + product.getInterestRate() + "%]: ");
	    String interestRateStr = sc.nextLine();
	    BigDecimal interestRate;
	    if (interestRateStr.trim().isEmpty()) {
	        interestRate = product.getInterestRate();
	        System.out.println("ℹ️ 금리를 유지합니다: " + interestRate + "%");
	    } else {
	        try {
	            interestRate = new BigDecimal(interestRateStr);
	        } catch (NumberFormatException e) {
	            System.out.println("⚠️ 유효하지 않은 숫자입니다. 기존 금리(" + product.getInterestRate() + "%)를 유지합니다.");
	            interestRate = product.getInterestRate();
	        }
	    }
	    
	    // 새 가입 기간 선택 (입출금 계좌인 경우 스킵)
	    int periodMonths;
	    if (product.getProduct_type() == 100) { // 입출금 계좌
	        periodMonths = 0;
	        System.out.println("ℹ️ 입출금 계좌는 가입 기간이 없어 0개월로 자동 설정됩니다.");
	    } else {
	        System.out.println("\n📌 새 가입 기간(개월) 선택 [현재: " + product.getPeriodMonths() + "개월]:");
	        System.out.println("┌─────────────────────────────┐");
	        System.out.println("│  1. 12개월 (1년)            │");
	        System.out.println("│  2. 24개월 (2년)            │");
	        System.out.println("│  3. 36개월 (3년)            │");
	        System.out.println("│  4. 변경하지 않음           │");
	        System.out.println("└─────────────────────────────┘");
	        System.out.print("👉 선택: ");
	        String periodChoice = sc.nextLine();
	        
	        switch(periodChoice) {
	            case "1": periodMonths = 12; break;
	            case "2": periodMonths = 24; break;
	            case "3": periodMonths = 36; break;
	            case "4":
	            default:
	                System.out.println("ℹ️ 가입기간을 유지합니다: " + product.getPeriodMonths() + "개월");
	                periodMonths = product.getPeriodMonths();
	        }
	    }
	    
	    // 상품 유형별 추가 정보 입력
	    BigDecimal maxDepositAmount = product.getMaxDepositAmount();
	    BigDecimal maxMonthlyDeposit = product.getMaxMonthlyDeposit();
	    
	    switch(product.getProduct_type()) {
	        case 200:  // 적금 상품
	            String currentMonthly = (product.getMaxMonthlyDeposit() != null) ? 
	                formatCurrency(product.getMaxMonthlyDeposit()) + "원" : "설정되지 않음";
	            System.out.print("📌 새 최대 월 납입액 [" + currentMonthly + "]: ");
	            
	            String maxMonthlyStr = sc.nextLine();
	            
	            if (!maxMonthlyStr.trim().isEmpty()) {
	                try {
	                    maxMonthlyDeposit = new BigDecimal(maxMonthlyStr);
	                    if (maxMonthlyDeposit.compareTo(BigDecimal.ZERO) <= 0) {
	                        System.out.println("⚠️ 월 납입액은 0보다 커야 합니다. 기존 값을 유지합니다.");
	                        maxMonthlyDeposit = product.getMaxMonthlyDeposit();
	                    }
	                } catch (NumberFormatException e) {
	                    System.out.println("⚠️ 유효하지 않은 숫자입니다. 기존 값을 유지합니다.");
	                    maxMonthlyDeposit = product.getMaxMonthlyDeposit();
	                }
	            }
	            break;
            
	        case 300:  // 예금 상품
	            String currentMax = (product.getMaxDepositAmount() != null) ? 
	                formatCurrency(product.getMaxDepositAmount()) + "원" : "설정되지 않음";
	            System.out.print("📌 새 최대 예치금액 [" + currentMax + "]: ");
	            
	            String maxDepositStr = sc.nextLine();
	            
	            if (!maxDepositStr.trim().isEmpty()) {
	                try {
	                    maxDepositAmount = new BigDecimal(maxDepositStr);
	                    if (maxDepositAmount.compareTo(BigDecimal.ZERO) <= 0) {
	                        System.out.println("⚠️ 예치금액은 0보다 커야 합니다. 기존 값을 유지합니다.");
	                        maxDepositAmount = product.getMaxDepositAmount();
	                    }
	                } catch (NumberFormatException e) {
	                    System.out.println("⚠️ 유효하지 않은 숫자입니다. 기존 값을 유지합니다.");
	                    maxDepositAmount = product.getMaxDepositAmount();
	                }
	            }
	            break;
	    }
	    
	    // 수정할 상품 정보 확인
	    System.out.println("\n+--------------------------------------------+");
	    System.out.println("|          📝 수정할 상품 정보 📝           |");
	    System.out.println("+--------------------------------------------+");
	    System.out.println("│ 상품ID    : " + product.getProductId() + " (변경 불가)");
	    System.out.println("│ 상품명    : " + productName);
	    System.out.println("│ 상품유형  : " + productTypeName + " (변경 불가)");
	    System.out.println("│ 금리      : " + interestRate + "%");
	    System.out.println("│ 가입기간  : " + periodMonths + "개월");
	    
	    // 상품 유형에 따라 추가 정보 표시
	    switch(product.getProduct_type()) {
	        case 200: // 적금 상품
	            System.out.println("│ 최대 월 납입액: " + formatCurrency(maxMonthlyDeposit) + "원");
	            break;
	        case 300: // 예금 상품
	            System.out.println("│ 최대 예치금액: " + formatCurrency(maxDepositAmount) + "원");
	            break;
	    }
	    
	    System.out.println("+--------------------------------------------+");
	    
	    // 확인 요청
	    System.out.print("\n✅ 이 정보로 상품을 수정하시겠습니까? (Y/N) ");
	    String confirm = sc.nextLine();
	    
	    if (confirm.equalsIgnoreCase("Y")) {
	        // 상품 객체 업데이트
	        ProductVO updateProductVO = new ProductVO();
	        updateProductVO.setProductId(productId);
	        updateProductVO.setProductName(productName);
	        updateProductVO.setProduct_type(product.getProduct_type()); // 상품 유형은 변경 불가
	        updateProductVO.setInterestRate(interestRate);
	        updateProductVO.setPeriodMonths(periodMonths);
	        
	        // 상품 유형별 추가 정보 설정
	        switch(product.getProduct_type()) {
	            case 200: // 적금 상품
	            	updateProductVO.setMaxMonthlyDeposit(maxMonthlyDeposit);
	                break;
	            case 300: // 예금 상품
	            	updateProductVO.setMaxDepositAmount(maxDepositAmount);
	                break;
	        }
	        
	        boolean result = adminProductService.updateProduct(updateProductVO);
	        
	        if (result) {
	            System.out.println("\n🎉 성공! 상품이 성공적으로 수정되었습니다.");
	            System.out.println("📆 수정 시간: " + java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
	        } else {
	            System.out.println("\n❌ 오류! 상품 수정에 실패했습니다.");
	            System.out.println("💡 시스템 관리자에게 문의하세요.");
	        }
	    } else {
	        System.out.println("\n🔄 상품 수정이 취소되었습니다.");
	    }
	}

	// 상품 삭제
	public void deleteProduct() {
		// 헤더
	    System.out.println("\n+--------------------------------------------+");
	    System.out.println("|          🗑️ 금융 상품 삭제 🗑️             |");
	    System.out.println("+--------------------------------------------+");
	    
	    // 삭제할 상품 ID 입력
	    System.out.print("\n🔍 삭제할 상품의 ID를 입력하세요: ");
	    int productId;
	    try {
	        productId = Integer.parseInt(sc.nextLine());
	    } catch (NumberFormatException e) {
	        System.out.println("❌ 유효하지 않은 ID입니다. 삭제를 취소합니다.");
	        return;
	    }
	    
	    // 기존 상품 정보 조회
	    ProductVO product = adminProductService.getProductById(productId);
	    if (product == null || product.getProductId() == 0) {
	        System.out.println("❌ 해당 ID의 상품이 존재하지 않습니다. 삭제를 취소합니다.");
	        return;
	    }
	    
	    // 상품 유형에 대한 이름 매핑
	    String productTypeName;
	    switch(product.getProduct_type()) {
	        case 100: productTypeName = "💳 입출금 계좌"; break;
	        case 200: productTypeName = "💰 적금 상품"; break;
	        case 300: productTypeName = "💵 예금 상품"; break;
	        default: productTypeName = String.valueOf(product.getProduct_type());
	    }
	    
	    // 삭제할 상품 정보 표시
	    System.out.println("\n+--------------------------------------------+");
	    System.out.println("|          ⚠️ 삭제할 상품 정보 ⚠️           |");
	    System.out.println("+--------------------------------------------+");
	    System.out.println("│ 상품ID    : " + product.getProductId());
	    System.out.println("│ 상품명    : " + product.getProductName());
	    System.out.println("│ 상품유형  : " + productTypeName + " (" + product.getProduct_type() + ")");
	    System.out.println("│ 금리      : " + product.getInterestRate() + "%");
	    System.out.println("│ 가입기간  : " + product.getPeriodMonths() + "개월");
	    
	    // 상품 유형에 따라 추가 정보 표시
	    switch(product.getProduct_type()) {
	        case 200: // 적금 상품
	            System.out.println("│ 최대 월 납입액: " + formatCurrency(product.getMaxMonthlyDeposit()) + "원");
	            break;
	        case 300: // 예금 상품
	            System.out.println("│ 최대 예치금액: " + formatCurrency(product.getMaxDepositAmount()) + "원");
	            break;
	    }
	    
	    System.out.println("+--------------------------------------------+");
	    
	    // 삭제 확인 (2단계 확인 과정)
	    System.out.println("\n⚠️ 주의: 상품을 삭제하면 복구할 수 없습니다!");
	    System.out.print("❓ 이 상품을 정말 삭제하시겠습니까? (Y/N): ");
	    String confirm = sc.nextLine();
	    
	    if (confirm.equalsIgnoreCase("Y")) {
	        // 추가 확인
	        System.out.print("\n⚠️ 최종 확인: 상품 [" + product.getProductName() + "]을(를) 삭제합니다. 진행하시겠습니까? (DELETE 입력): ");
	        String finalConfirm = sc.nextLine();
	        
	        if (finalConfirm.equalsIgnoreCase("DELETE")) {
	            boolean result = adminProductService.deleteProduct(productId);
	            
	            if (result) {
	                System.out.println("\n✅ 성공! 상품이 성공적으로 삭제되었습니다.");
	                System.out.println("📆 삭제 시간: " + java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
	            } else {
	                System.out.println("\n❌ 오류! 상품 삭제에 실패했습니다.");
	                System.out.println("💡 이 상품에 가입한 계좌가 있는지 확인하세요.");
	                System.out.println("💡 시스템 관리자에게 문의하세요.");
	            }
	        } else {
	            System.out.println("\n🔄 'DELETE'를 입력하지 않아 상품 삭제가 취소되었습니다.");
	        }
	    } else {
	        System.out.println("\n🔄 상품 삭제가 취소되었습니다.");
	    }
	}
	
	// 금액 포맷팅 메서드
	private String formatCurrency(BigDecimal amount) {
		if (amount == null) {
			return "-";
		}
	    return String.format("%,d", amount.longValue());
	}
}
