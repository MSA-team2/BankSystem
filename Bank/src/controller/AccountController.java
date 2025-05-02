package controller;

import java.math.BigDecimal;
import java.util.List;

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

		// 개설 메뉴 들어오자마자 화면 출력 및 상품 선택 값 받아온다.
		List<ProductVO> product = service.getAllProducts();
		int product_no = view.productShow(product);
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
