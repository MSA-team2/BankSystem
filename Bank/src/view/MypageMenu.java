package view;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import controller.MypageController;
import controller.SessionManager;

public class MypageMenu {
	private final MypageController controller = new MypageController();
	private BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

	public void UserInfo() {
		if (!SessionManager.isLoggedIn()) {
			new FirstMainMenu().mainMenu();
			return;
		}

		while (true) {
			controller.displayOwnedAccounts(); // 계좌 정보 출력

			System.out.print("\n\t[1] 예금/적금 중도해지\n\t[2] 계좌 잠김 문의\n\t[0] 사용자 메뉴\n");
			System.out.print("👉 메뉴를 선택하세요 : ");

			try {
				int sel = Integer.parseInt(br.readLine());

				switch (sel) {
				case 1 -> controller.withdrawBeforeMaturity();
				case 2 -> controller.accountLocktoAdmin();
				case 0 -> {
					return; // 사용자 메뉴로 돌아가기
				}
				default -> System.out.println("올바른 번호를 입력해주세요.");
				}
			} catch (Exception e) {
				System.out.println("잘못된 입력입니다.");
			}
		}
	}

}
