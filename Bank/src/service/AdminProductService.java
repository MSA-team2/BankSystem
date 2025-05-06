package service;

import java.math.BigDecimal;
import java.util.List;

import dao.AccountDAO;
import dao.ProductDAO;
import model.ProductVO;

public class AdminProductService {
	private final ProductDAO productDAO = new ProductDAO();
	private final AccountDAO accountDAO = new AccountDAO();
	
	// 관리자 모든 상품 조회
	public List<ProductVO> getAllProducts() {
		return productDAO.getProductInfo();
	}
	
	// 관리자 상품 등록
	public boolean addProduct(ProductVO product) {
		if (product.getProductName() == null || product.getProductName().trim().isEmpty()) {
	        System.out.println("상품명은 비워둘 수 없습니다.");
	        return false;
	    }
	    
	    if (product.getInterestRate() == null || product.getInterestRate().compareTo(BigDecimal.ZERO) < 0) {
	        System.out.println("금리는 0 이상이어야 합니다.");
	        return false;
	    }
	    
	    if (product.getPeriodMonths() < 0) {
	        System.out.println("가입기간은 0 이상이어야 합니다.");
	        return false;
	    }
		
		return productDAO.addPorduct(product);
	}
	
	// 관리자 상품 수정
	public boolean updateProduct(ProductVO product) {
	    // 유효성 검사
	    if (product.getProductName() == null || product.getProductName().trim().isEmpty()) {
	        System.out.println("상품명은 비워둘 수 없습니다.");
	        return false;
	    }
	    
	    if (product.getInterestRate() == null || product.getInterestRate().compareTo(BigDecimal.ZERO) < 0) {
	        System.out.println("금리는 0 이상이어야 합니다.");
	        return false;
	    }
	    
	    if (product.getPeriodMonths() < 0) {
	        System.out.println("가입기간은 0 이상이어야 합니다.");
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

	// 상품 ID로 상품 정보 조회
	public ProductVO getProductById(int productId) {
	    return productDAO.getProductById(productId);
	}
}
