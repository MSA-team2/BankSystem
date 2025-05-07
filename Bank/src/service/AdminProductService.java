package service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import dao.AccountDAO;
import dao.ProductDAO;
import model.domain.Product;

public class AdminProductService {
    private final ProductDAO productDAO = new ProductDAO();
    private final AccountDAO accountDAO = new AccountDAO();
    
    private static final Map<Integer, String> PRODUCT_TYPE_MAP = new HashMap<>();
    
    static {
        PRODUCT_TYPE_MAP.put(100, "💳 입출금");
        PRODUCT_TYPE_MAP.put(200, "💰 적금");
        PRODUCT_TYPE_MAP.put(300, "💵 예금");
    }
    
    // 관리자 모든 상품 조회
    public List<Product> getAllProducts() {
        return productDAO.getProductInfo();
    }
    
    // 상품 ID로 상품 정보 조회
    public Product getProductById(int productId) {
        return productDAO.getProductById(productId);
    }
    
    // 관리자 상품 등록
    public boolean addProduct(Product product) {
        // 데이터 유효성 검증
        if (!validateProduct(product)) {
            return false;
        }
        
        return productDAO.addPorduct(product);
    }
    
    // 관리자 상품 수정
    public boolean updateProduct(Product product) {
        // 데이터 유효성 검증
        if (!validateProduct(product)) {
            return false;
        }
        
        return productDAO.updateProduct(product);
    }
    
    // 관리자 상품 삭제
    public boolean deleteProduct(int productId) {
        // 상품이 사용중인지 확인
        if (accountDAO.isProductInUse(productId)) {
            System.out.println("이 상품은 현재 사용중이라 삭제할 수 없습니다.");
            return false;
        }
        
        return productDAO.deleteProduct(productId);
    }
    
    // 상품 객체 유효성 검증
    public boolean validateProduct(Product product) {
        // 상품명 검증
        if (product.getProductName() == null || product.getProductName().trim().isEmpty()) {
            System.out.println("상품명은 비워둘 수 없습니다.");
            return false;
        }
        
        // 금리 검증
        if (product.getInterestRate() == null || product.getInterestRate().compareTo(BigDecimal.ZERO) < 0) {
            System.out.println("금리는 0 이상이어야 합니다.");
            return false;
        }
        
        // 가입 기간 검증
        if (product.getPeriodMonths() < 0) {
            System.out.println("가입기간은 0 이상이어야 합니다.");
            return false;
        }
        
        // 상품 유형별 추가 검증
        switch (product.getProduct_type()) {
            case 200: // 적금 상품
                if (product.getMaxMonthlyDeposit() != null && 
                        product.getMaxMonthlyDeposit().compareTo(BigDecimal.ZERO) <= 0) {
                    System.out.println("월 납입액은 0보다 커야 합니다.");
                    return false;
                }
                break;
            case 300: // 예금 상품
                if (product.getMaxDepositAmount() != null && 
                        product.getMaxDepositAmount().compareTo(BigDecimal.ZERO) <= 0) {
                    System.out.println("예치금액은 0보다 커야 합니다.");
                    return false;
                }
                break;
        }
        
        return true;
    }
    
    // 상품 유형별 기본값 설정
    public void setDefaultValues(Product product) {
        // 상품 유형에 따른 기본값 설정
        switch (product.getProduct_type()) {
            case 100: // 입출금 계좌
                if (product.getPeriodMonths() != 0) {
                    product.setPeriodMonths(0); // 입출금 계좌는 가입 기간이 없음
                }
                break;
                
            case 200: // 적금 상품
                if (product.getMaxMonthlyDeposit() == null) {
                    product.setMaxMonthlyDeposit(new BigDecimal("300000")); // 기본 월 납입액
                }
                
                if (product.getPeriodMonths() == 0) {
                    product.setPeriodMonths(12); // 기본 가입 기간
                }
                break;
                
            case 300: // 예금 상품
                if (product.getMaxDepositAmount() == null) {
                    product.setMaxDepositAmount(new BigDecimal("1000000")); // 기본 예치금액
                }
                
                if (product.getPeriodMonths() == 0) {
                    product.setPeriodMonths(12); // 기본 가입 기간
                }
                break;
        }
        
        // 기본 금리 설정
        if (product.getInterestRate() == null) {
            product.setInterestRate(new BigDecimal("0.1")); // 기본 금리
        }
    }
    
    // 상품 유형 이름 반환
    public String getProductTypeName(int productType) {
        return PRODUCT_TYPE_MAP.getOrDefault(productType, String.valueOf(productType));
    }
    
    // 모든 상품 유형 맵 반환
    public Map<Integer, String> getAllProductTypes() {
        return PRODUCT_TYPE_MAP;
    }
    
    // 상품 유형별 특성 체크
    public boolean isCheckingAccount(int productType) {
        return productType == 100;
    }
    
    public boolean isSavingsAccount(int productType) {
        return productType == 200;
    }
    
    public boolean isDepositAccount(int productType) {
        return productType == 300;
    }
    
    // 상품 유형별 가능한 기간 옵션 반환
    public List<Integer> getAvailablePeriods(int productType) {
        List<Integer> periods = new java.util.ArrayList<>();
        
        if (productType == 100) { // 입출금 계좌
            periods.add(0); // 입출금 계좌는 기간 없음
        } else {
            // 적금/예금 상품은 12, 24, 36개월 선택 가능
            periods.add(12);
            periods.add(24);
            periods.add(36);
        }
        
        return periods;
    }
    
    // 상품 유형코드로 상품 유형 가져오기
    public int getProductTypeFromChoice(String choice) {
        switch(choice) {
            case "1": return 100; // 입출금
            case "2": return 200; // 적금
            case "3": return 300; // 예금
            default: return 100;  // 기본값은 입출금
        }
    }
    
    // 기간 선택값으로 기간(개월) 가져오기
    public int getPeriodFromChoice(String choice, Integer currentPeriod) {
        // 입출금 계좌인 경우 항상 0 반환
        if (currentPeriod != null && (choice.equals("4") || choice.trim().isEmpty())) {
            return currentPeriod; // 변경하지 않음 선택
        }
        
        switch(choice) {
            case "1": return 12;
            case "2": return 24;
            case "3": return 36;
            default:
                return (currentPeriod != null) ? currentPeriod : 12;
        }
    }
    
    // 금액 포맷팅 메서드
    public String formatCurrency(BigDecimal amount) {
        if (amount == null) {
            return "-";
        }
        return String.format("%,d", amount.longValue());
    }
    
    // 상품 통계 정보 계산
    public Map<String, Integer> calculateProductStats(List<Product> products) {
        Map<String, Integer> stats = new HashMap<>();
        
        int totalProducts = products.size();
        int checkingCount = 0;
        int depositCount = 0;
        int savingsCount = 0;
        
        for (Product p : products) {
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
        
        stats.put("total", totalProducts);
        stats.put("checking", checkingCount);
        stats.put("savings", savingsCount);
        stats.put("deposit", depositCount);
        
        return stats;
    }
    
    // ProductVO 객체 생성 헬퍼 메서드
    public Product createProductVO(int productId, String productName, int productType, 
            BigDecimal interestRate, int periodMonths, BigDecimal maxDepositAmount, BigDecimal maxMonthlyDeposit) {
        
        Product product = new Product();
        product.setProductId(productId);
        product.setProductName(productName);
        product.setProduct_type(productType);
        product.setInterestRate(interestRate);
        product.setPeriodMonths(periodMonths);
        
        // 상품 유형별 추가 정보 설정
        if (productType == 200) { // 적금 상품
            product.setMaxMonthlyDeposit(maxMonthlyDeposit);
        } else if (productType == 300) { // 예금 상품
            product.setMaxDepositAmount(maxDepositAmount);
        }
        
        return product;
    }
}