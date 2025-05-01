package service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;

import controller.SessionManager;
import dao.AccountDAO;
import dao.ProductDAO;
import model.AccountVO;
import model.ProductVO;

public class AccountService {
	// 재할당 방지를 위해 final 키워드 사용
	private final ProductDAO productDAO = new ProductDAO();
    private final AccountDAO accountDAO = new AccountDAO();
    
    // 상품 관련하여 service가 필요하지 않을거 같아 한 service에서 몰아서 작성
    public List<ProductVO> getAllProducts() {
        return productDAO.getProductInfo();
    }
    
    public AccountVO createAccountNumber(int productId, BigDecimal deposit, BigDecimal balance, String password) {
    	ProductVO product = productDAO.getProductById(productId);
    	if(product == null) return null;
    	
    	String phoneTail = SessionManager.getCurrentUser().getPhone().substring(9);
        String randomNumber = String.format("%04d", new Random().nextInt(10000));
        String prefix = productId == 1 ? "100" : (productId <= 3 || productId == 7) ? "200" : "300";
        String accountNo = prefix + "-" + phoneTail + "-" + randomNumber;

        AccountVO dto = new AccountVO();
        dto.setAccountNo(accountNo);
        dto.setAccountPwd(password);
        dto.setProduct_id(productId);
        dto.setBalance(balance);
        dto.setMemberNo(SessionManager.getCurrentUser().getMemberNo());
        dto.setCreateDate(LocalDate.now());
        if(prefix.equals("300"))  dto.setDeposit_amount(deposit);
        if(prefix.equals("200") || prefix.equals("300")) {
        	dto.setMaturity_date(LocalDate.now().plusMonths(product.getPeriodMonths()));
        }
        // 이자 계산
        BigDecimal rate = product.getInterestRate();
        dto.setMaturity_amount(calcMaturityAmount(balance, rate, deposit, prefix, product.getPeriodMonths()));
        return accountDAO.createAccount(dto)?dto:null;
    }
    
    // 만기 금액 예상 금액. 단리 기준이고 적금은 독자적인 계산법...
    public BigDecimal calcMaturityAmount(BigDecimal balance, BigDecimal rate, BigDecimal deposit
    		, String product_type, int maturity_date ) {
    	BigDecimal month = new BigDecimal(maturity_date);
    	int divideMonth = maturity_date / 12;
    	BigDecimal year = new BigDecimal(divideMonth);
    	
    	BigDecimal percent = new BigDecimal("100");	// 연이율 계산용
    	
    	if(product_type.equals("300")) {	// 적금 계산
    		BigDecimal first = deposit.multiply(month).multiply(rate).divide(percent);
    		BigDecimal second = deposit.multiply(month);
    		return first.add(second);
    	}else if(product_type.equals("200")) {	// 예금 계산
    		BigDecimal first = balance.multiply(year).multiply(rate).divide(percent);
    		return first.add(balance);
    	}else {	// 입출금계좌는 불필요
    		return new BigDecimal("0");
    	}
    }
}
