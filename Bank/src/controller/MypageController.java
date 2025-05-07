package controller;

import model.MemberVO;
import service.MypageService;

public class MypageController {

	private final MypageService service = new MypageService();
	MemberVO user = SessionManager.getCurrentUser();


	// 계좌번호, 상품명, 원금, 이자율, 만기일, 총액 리스트로 뿌리기
	public void displayOwnedAccounts() {
		service.displayUserInfo(user);
	}

	// 1 : (예금/적금) 중도해제
	public void withdrawBeforeMaturity() {
		service.withdrawProduct(user);

	}

	// 2 : 계정 잠김 관리자 문의  
	public void accountLocktoAdmin() {
		// TODO
		service.callAdmimAboutAccountLock();

	}

}
