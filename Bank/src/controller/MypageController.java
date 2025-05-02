package controller;

import model.MemberVO;
import service.MypageService;

public class MypageController {
	
	private final MypageService service;

	// 생성자 주입 방식
	public MypageController(MypageService service) {
		this.service = service;
	}


	// 계좌번호, 상품명, 원금, 이자율, 만기일, 총액 리스트로 뿌리기
	public void displayOwnedAccounts(MemberVO user) {
		service.displayUserInfo(user);
	}

	// 1 : (예금/적금) 중도해제
	public void withdrawBeforeMaturity() {
		// TODO
		service.withdrawProduct();

	}

	// 2 : 회원탈퇴
	public void deleteMemberProfile() {
		// TODO
		service.deleteAccount();

	}

}
