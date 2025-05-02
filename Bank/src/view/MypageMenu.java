package view;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import controller.MypageController;
import controller.SessionManager;
import model.MemberVO;

public class MypageMenu {
	private final MypageController controller = new MypageController();
	private  BufferedReader br = new BufferedReader(new InputStreamReader(System.in));


	public void UserInfo() {
		if (SessionManager.isLoggedIn() == false) {
			new FirstMainMenu().mainMenu();
		} else {
			MemberVO user = SessionManager.getCurrentUser();
			// 개인신상, 계좌번호, 상품명, 원금, 이자율, 만기일, 총액 리스트로 뿌리기
			controller.displayOwnedAccounts(user);
		}
	}

	public void etcMenu() {

		System.out.print("1.예금/적금 중도해지\n2.회원탈퇴\n0.사용자 메뉴\n선택 : ");

		try {
			switch (Integer.parseInt(br.readLine())) {
			case 1: {
				// 1 : (예금/적금) 중도해제
				controller.withdrawBeforeMaturity();
				break;
			}
			case 2: {
				// 2 : 회원탈퇴
				controller.deleteMemberProfile();
				break;

			}
			case 0: {
				// 0 : 사용자메뉴로 돌아가기
				MemberMenu.MemberMainMenu();
				break;
			}
			default: {
				System.out.println("올바른 번호를 입력해주세요. ");
			}
			}
		}

		catch (NumberFormatException | IOException e) {
			e.printStackTrace();
		}
	}

}
