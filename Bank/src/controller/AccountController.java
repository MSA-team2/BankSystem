package controller;

import java.math.BigDecimal;
import java.util.List;

import dto.AccountShowDTO;
import model.AccountVO;
import model.ProductVO;
import service.AccountService;
import view.AccountMenu;
import view.MemberMenu;

public class AccountController {
	private final AccountService service = new AccountService();
	private final AccountMenu view = new AccountMenu();
	private final MemberMenu member = new MemberMenu();
	// 계좌 개설 컨트롤러
	public void insertAccount() {
		if (!SessionManager.isLoggedIn()) {
			System.out.println("로그인이 필요합니다.");
			return;
		}
		// if문으로 유저가 
		// 입출금 통장이 있다면 일반 상품 선택으로 넘어가고, 없다면 입출금 계좌 개설
		List<AccountShowDTO> list = service.checkRegistProduct();
		List<ProductVO> product;
		if(list.size() < 1) {
			view.printMessage("가입 상품이 없어 입출금 상품만 출력됩니다.");
			product = service.getProductbyType(100);	// 입출금상품 타입 100번
		}else {
			// 개설 메뉴 들어오자마자 화면 출력 및 상품 선택 값 받아온다.
			product = service.getAllProducts();
		}
		int product_no = view.productShow(product);
		if(product_no == 0) member.MemberMainMenu();
		
		// 상품 타입 구해오는 메서드
		ProductVO product_info = service.getProduct_id(product_no);
		
		BigDecimal balance = new BigDecimal("0");
		BigDecimal deposit = new BigDecimal("0");
		if (product_info.getProduct_type() == 100) {	// 입출금 기본금입력
			balance = view.inputInitialBalance();
			if(balance.compareTo(BigDecimal.ZERO) == 0) member.MemberMainMenu();
		}else if(product_info.getProduct_type() == 200){	// 적금 입력
			deposit = view.inputDepositAmount();
			if(deposit.compareTo(BigDecimal.ZERO) == 0) member.MemberMainMenu();
		}else if(product_info.getProduct_type() == 300) {	// 예금상품 가입 시 
			List<AccountShowDTO> myAccount = service.myDepositWithdrawAccount();
			
			String accountNo;
			AccountShowDTO dto;
			while(true) {
				accountNo = view.myAccountShow(myAccount);
				dto = service.myDepositWithdrawAccountbalance(accountNo);
				if(dto.getAccountNo() == null) {
					view.printMessage("존재하지 않는 계좌번호 입니다. 다시 입력해주세요");
					continue;
				}
				balance = view.inputDeposit();
				if(balance.compareTo(BigDecimal.ZERO) == 0) {
					member.MemberMainMenu();
				}else if(balance.longValue() > dto.getBalance()) {
					view.printMessage("선택한 계좌의 잔액이 부족합니다. 계좌를 다시 선택합니다.");
					continue;
				}else {
					break;
				}
			}
		}

		String password = view.inputPassword();

		AccountVO account = service.createAccountNumber(product_no, deposit, balance, password);
		if (account != null) {
			view.successMakeAccount(account);
			view.printMessage("\n메인 메뉴로 돌아갑니다.");
			new MemberMenu().MemberMainMenu();
		} else {
			view.printMessage("계좌 생성 실패\n메인 메뉴로 돌아갑니다.");
			new MemberMenu().MemberMainMenu();
		}

	}
}
