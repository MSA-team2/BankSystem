package controller;

import java.math.BigDecimal;
import java.util.List;

import model.AccountVO;
import model.ProductVO;
import service.AccountService;
import view.AccountMenu;

/*
 * 계좌개설/상품가입/계좌 비번 설정
 * 계좌번호 생성, 상품 선택, 비밀번호 설정, 계좌 리스트
 * ### 2. 계좌 관리 담당 (B)

- 계좌 개설(상품 선택 → 금액 입력 → 비밀번호 입력 → 계좌 생성)
- 계좌번호 생성 (상품번호-휴대폰뒤4자리-랜덤4자리 조합)
- 가입 기간, 금리 계산
- 상품가입 완료 시 계좌에 매핑

**중요**

- 계좌비밀번호 입력 → 암호화/검증 필요
- 가입 상품은 여러 개 가능 (N:M 구조 고려)
 */

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
