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
        
        if(prefix.equals("300"))  dto.setDeposit_amount(deposit);
        if(prefix.equals("200") || prefix.equals("300")) {
        	dto.setMaturity_date(LocalDate.now().plusMonths(product.getPeriodMonths()));
        }
        return accountDAO.createAccount(dto)?dto:null;
    	
    }
}
