package service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import controller.SessionManager;
import dao.AccountDAO;
import dao.ProductDAO;
import dao.TransactionDAO;
import dto.AccountShowDTO;
import dto.TransactionDTO;
import model.domain.Account;
import model.domain.Product;
import util.Validator;

public class AccountService {
	// 재할당 방지를 위해 final 키워드 사용
	private final ProductDAO productDAO = new ProductDAO();
	private final AccountDAO accountDAO = new AccountDAO();
	private final TransactionDAO transDAO = new TransactionDAO();
	private final Scanner sc = new Scanner(System.in);

	// 상품 관련하여 service가 필요하지 않을거 같아 한 service에서 몰아서 작성
	public List<Product> getAllProducts() {
		return productDAO.getProductInfo();
	}

	// 상품 타입별로 정보 가져오기
	public List<Product> getProductbyType(int product_type) {
		return productDAO.getProductByType(product_type);
	}

	// 계정에 가입한 상품 유무 체크
	public List<AccountShowDTO> checkRegistProduct(List<String> productTypes) {
		return accountDAO.showMyAccounts(productTypes);
	}

	// 예금 상품 가입 시 출력할 보유 입출금 계좌
	public List<AccountShowDTO> myDepositWithdrawAccount() {
		return accountDAO.showMyAccountDepositWithdraw();
	}

	// 예금 상품 가입 시 출력할 보유 입출금 계좌
	public AccountShowDTO myDepositWithdrawAccountbalance(String account_no) {
		return accountDAO.selectMyAccountDepositWithdraw(account_no);
	}

	// 사용자가 입력한 프로덕트 넘버에 따른 상품 정보 가져오기
	public Product getProduct_id(int product_no) {
		return productDAO.getProductById(product_no);
	}

	// 예금 상품 가입 시 입출금 계좌에서 예금 계좌로 돈 옮기는 작업
	public boolean transDeposit(TransactionDTO depodto, TransactionDTO withdto) {
		boolean deposit = transDAO.transfer(depodto);
		boolean withdraw = transDAO.transfer(withdto);
		// 거래내역 저장하기
		if (deposit && withdraw) {
			transDAO.saveTransaction(depodto);
			transDAO.saveTransaction(withdto);
			return true;
		} else
			return false;
	}

	public Account createAccountNumber(int productId, BigDecimal deposit, BigDecimal balance, String password) {
		Product product = productDAO.getProductById(productId);
		if (product == null)
			return null;

		String phoneTail, randomNumber, type, accountNo;
		while (true) {		// 계좌번호 고유성 확인 후 생성
			phoneTail = SessionManager.getCurrentUser().getPhone().substring(9);
			randomNumber = String.format("%04d", new Random().nextInt(10000));
			type = Integer.toString(product.getProduct_type());
			accountNo = type + "-" + phoneTail + "-" + randomNumber;

			AccountVO allAccountNo = accountDAO.findByAccountNo(accountNo);
			if (allAccountNo == null || allAccountNo.getAccountNo() == null) break;

			else continue;
		}
		BigDecimal initialBalance = new BigDecimal("0");

		Account dto = new Account();
		dto.setAccountNo(accountNo);
		dto.setAccountPwd(password);
		dto.setProduct_id(productId);
		dto.setMemberNo(SessionManager.getCurrentUser().getMemberNo());
		dto.setCreateDate(LocalDateTime.now());
		if (type.equals("100")) {
			dto.setBalance(balance);
		} else if (type.equals("200") || type.equals("300")) {
			dto.setBalance(initialBalance);
			dto.setMaturityDate(LocalDateTime.now().plusMonths(product.getPeriodMonths()));
		}
		if (type.equals("200"))
			dto.setDeposit_amount(deposit);
		// 이자 계산
		BigDecimal rate = product.getInterestRate();
		dto.setMaturity_amount(calcMaturityAmount(balance, rate, deposit, type, product.getPeriodMonths()));
		return accountDAO.createAccount(dto) ? dto : null;
	}

	// 만기 금액 예상 금액. 단리 기준이고 적금은 독자적인 계산법...
	public BigDecimal calcMaturityAmount(BigDecimal balance, BigDecimal rate, BigDecimal deposit, String product_type,
			int maturity_date) {
		BigDecimal month = new BigDecimal(maturity_date);
		int divideMonth = maturity_date / 12;
		BigDecimal year = new BigDecimal(divideMonth);

		BigDecimal percent = new BigDecimal("100"); // 연이율 계산용

		if (product_type.equals("200")) { // 적금 계산
			BigDecimal first = deposit.multiply(month).multiply(rate).divide(percent);
			BigDecimal second = deposit.multiply(month);
			return first.add(second);
		} else if (product_type.equals("300")) { // 예금 계산
			BigDecimal first = balance.multiply(year).multiply(rate).divide(percent);
			return first.add(balance);
		} else { // 입출금계좌는 불필요
			return new BigDecimal("0");
		}
	}

	public boolean verifyPassword(String accountNo, String pwd) {
		Account account = accountDAO.getPwdAndStatus(accountNo);
		if (account == null) {
			System.out.println("⚠️ 계좌를 찾을 수 없습니다.");
			return false;
		}

		if (account.getStatus() == 'N') {
			System.out.println("🔒 해당 계좌는 잠겨 있습니다.");
			return false;
		}

		int lockCnt = account.getLock_cnt();
		if (!pwd.equals(account.getAccountPwd())) {
			lockCnt++;
			accountDAO.updateLockCnt(accountNo, lockCnt);
			
			if (lockCnt == 5) {
				accountDAO.lockAccount(accountNo);
				System.out.println("❌ 비밀번호 5회 오류로 계좌가 잠겼습니다.");
				return false;
			} else {	
				if(!Validator.isValidNumber(pwd)) {
					System.out.println("⚠️ 잘못된 비밀번호 형식입니다. 숫자로만 입력해주세요."); 
				}
				System.out.println("❗ 비밀번호가 틀렸습니다. 현재 " + lockCnt + "/5회 오류.");
			}
		} else {
			accountDAO.resetLockCnt(accountNo);
			return true;
		}

	return false;
	}

}
