package service;

import java.util.Iterator;
import java.util.List;

import dao.AccountDAO;
import model.AccountDTO;
import model.MemberVO;

public class MypageService {
	// DAO 생성해서 사용하기
	private final AccountDAO dao = new AccountDAO();

	// 개인신상
	// 계좌번호, 상품명, 원금, 이자율, 만기일, 총액 리스트로 뿌리기
	public void displayUserInfo(MemberVO user) {
		System.out.println("===== 마이페이지 =====");
		System.out.println("이름 : " + user.getName());
		System.out.println("전화번호 : " + user.getPhone());
		System.out.println("주소 : " + user.getAddress());

		//TODO : 구현해야함
//		List<AccountDTO> accounts = dao.findAccountsByMemberNo(user.getMemberNo());
//
//		if (accounts.isEmpty()) {
//			System.out.println("보유 중인 계좌가 없습니다.");
//			return;
//		}
//
//		System.out.println("\n--- 보유 계좌---");
//		// TODO 멤버 계좌 정보 출력하기
//		System.out.println("계좌번호\t\t상품명\t원금\t\t이자율\t만기일\t\t총액\n");
//		for (AccountDTO dto : accounts) {
////			System.out.println(dto.getAccountNo()+" "+dto.getProductId()+" "+ dto.get);
//		}
	}

	// 1 : (예금/적금) 중도해제
	public void withdrawProduct() {
		// TODO 예금/적금 중도해지 메서드 호출하기
		System.out.println("TODO 예금/적금 중도해지 메서드 호출하기");

	}

	// 2 : 회원탈퇴
	public void deleteAccount() {
		// TODO 회원 탈퇴 메서드 호출하기
		System.out.println("TODO 회원 탈퇴 메서드 호출하기");

	}

}
