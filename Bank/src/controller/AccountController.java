package controller;

import java.math.BigDecimal;
import java.util.List;

import dto.AccountShowDTO;
import model.AccountVO;
import model.ProductVO;
import service.AccountService;
import view.AccountMenu;

public class AccountController {
	private final AccountService service = new AccountService();
	private final AccountMenu view = new AccountMenu();

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
		int product_no = 0;
		if(list.size() < 1) {
			view.printMessage("가입 상품이 없어 입출금 상품만 출력됩니다.");
			product = service.getProductbyType(100);	// 입출금상품 타입 100번
			product_no = view.productShow(product);
		}else {
			// 개설 메뉴 들어오자마자 화면 출력 및 상품 선택 값 받아온다.
			product = service.getAllProducts();
			product_no = view.productShow(product);
		}

		BigDecimal balance = new BigDecimal("0");
		BigDecimal deposit = new BigDecimal("0");
		if (product_no <= 3 || product_no == 7) {	// 입출금 기본금, 예금 예치금 입력
			balance = view.inputInitialBalance();
		}else {	// 적금 입력
			deposit = view.inputDepositAmount();
		}

		String password = view.inputPassword();

		AccountVO account = service.createAccountNumber(product_no, deposit, balance, password);

		if (account != null) {
			view.successMakeAccount(account);
		} else {
			view.printMessage("계좌 생성 실패");
		}

	}
}
