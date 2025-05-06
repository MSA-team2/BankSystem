package view;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import controller.MypageController;
import controller.SessionManager;
import model.MemberVO;

public class MypageMenu {
	private final MypageController controller = new MypageController();
	private BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

	public void UserInfo() {
		while (true) {
			if (!SessionManager.isLoggedIn()) {
				new FirstMainMenu().mainMenu();
				return;
			}

			controller.displayOwnedAccounts(); // 계좌 정보 출력
			if (!etcMenu()) {
				break;
			}
		}
	}

	public boolean etcMenu() {
		System.out.print("\n1.예금/적금 중도해지\n2.회원탈퇴\n0.사용자 메뉴\n선택 : ");

		try {
			int sel = Integer.parseInt(br.readLine());

			switch (sel) {
			case 1 -> controller.withdrawBeforeMaturity();
			case 2 -> controller.deleteMemberProfile();
			case 0 -> {
				return false; // 뒤로가기: false 반환
			}
			default -> System.out.println("올바른 번호를 입력해주세요.");
			}
		} catch (Exception e) {
			System.out.println("잘못된 입력입니다.");
		}

		return true; // 다시 반복
	}

}
