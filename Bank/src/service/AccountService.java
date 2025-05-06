package service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import controller.SessionManager;
import dao.AccountDAO;
import dao.ProductDAO;
import dto.AccountShowDTO;
import model.AccountVO;
import model.ProductVO;

public class AccountService {
	// 재할당 방지를 위해 final 키워드 사용
	private final ProductDAO productDAO = new ProductDAO();
    private final AccountDAO accountDAO = new AccountDAO();
    private final Scanner sc = new Scanner(System.in);
    // 상품 관련하여 service가 필요하지 않을거 같아 한 service에서 몰아서 작성
    public List<ProductVO> getAllProducts() {
        return productDAO.getProductInfo();
    }
    // 상품 타입별로 정보 가져오기
    public List<ProductVO> getProductbyType(int product_type) {
    	return productDAO.getProductByType(product_type);
    }
    // 계정에 가입한 상품 유무 체크
    public List<AccountShowDTO> checkRegistProduct(){
    	return accountDAO.showMyAccounts();
    }
    // 예금 상품 가입 시 출력할 보유 입출금 계좌
    public List<AccountShowDTO> myDepositWithdrawAccount(){
    	return accountDAO.showMyAccountDepositWithdraw();
    }
    // 예금 상품 가입 시 출력할 보유 입출금 계좌
    public AccountShowDTO myDepositWithdrawAccountbalance(String account_no){
    	return accountDAO.selectMyAccountDepositWithdraw(account_no);
    }
    // 사용자가 입력한 프로덕트 넘버에 따른 상품 정보 가져오기
    public ProductVO getProduct_id(int product_no) {
    	return productDAO.getProductById(product_no);
    }
    
    public AccountVO createAccountNumber(int productId, BigDecimal deposit, BigDecimal balance, String password) {
    	ProductVO product = productDAO.getProductById(productId);
    	if(product == null) return null;
    	
    	String phoneTail = SessionManager.getCurrentUser().getPhone().substring(9);
        String randomNumber = String.format("%04d", new Random().nextInt(10000));
        String prefix = Integer.toString(product.getProduct_type());  
        String accountNo = prefix + "-" + phoneTail + "-" + randomNumber;

        AccountVO dto = new AccountVO();
        dto.setAccountNo(accountNo);
        dto.setAccountPwd(password);
        dto.setProduct_id(productId);
        dto.setBalance(balance);
        dto.setMemberNo(SessionManager.getCurrentUser().getMemberNo());
        dto.setCreateDate(LocalDateTime.now());
        if(prefix.equals("300"))  dto.setDeposit_amount(deposit);
        if(prefix.equals("200") || prefix.equals("300")) {
        	dto.setMaturityDate(LocalDateTime.now().plusMonths(product.getPeriodMonths()));
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
    	
    	if(product_type.equals("200")) {	// 적금 계산
    		BigDecimal first = deposit.multiply(month).multiply(rate).divide(percent);
    		BigDecimal second = deposit.multiply(month);
    		return first.add(second);
    	}else if(product_type.equals("300")) {	// 예금 계산
    		BigDecimal first = balance.multiply(year).multiply(rate).divide(percent);
    		return first.add(balance);
    	}else {	// 입출금계좌는 불필요
    		return new BigDecimal("0");
    	}
    }

	public boolean verifyPassword(String accountNo, String pwd) {
		AccountVO account = accountDAO.getPwdAndStatus(accountNo);
		if (account == null) {
			System.out.println("해당 계좌가 존재하지 않습니다.");
			return false;
		}

		if (account.getStatus() == 'N') {
			System.out.println("해당 계좌는 잠겨 있습니다.");
			return false;
		}

		int lockCnt = account.getLock_cnt();
			if (!pwd.equals(account.getAccountPwd())) {
				lockCnt++;
				accountDAO.updateLockCnt(accountNo, lockCnt);

				if (lockCnt == 5) {
					accountDAO.lockAccount(accountNo);
					System.out.println("비밀번호 5회 틀렸습니다. 계좌가 잠겼습니다.");
					return false;
				} else {
					System.out.println("비밀번호가 틀렸습니다. 현재 " + lockCnt + "회 오류.");
				}
			} else {
				accountDAO.resetLockCnt(accountNo);
				return true;
			}

		return false;
	}

}
