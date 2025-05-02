package view;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import controller.MypageController;
import controller.SessionManager;
import service.MypageService;

public class MemberMenu {

	public static void MemberMainMenu() {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		// 직접 객체 생성 후 의존성 주입
		MypageService service = new MypageService();
		MypageController controller = new MypageController(service);
		MypageMenu mypageMenu = new MypageMenu(controller);

		System.out.println("===== 사용자 메뉴 =====");
		System.out.print("1.마이페이지\n2.계좌개설\n3.거래내역\n4.입금\n5.출금\n6.이체\n7.로그아웃\n0.메인화면\n선택 : ");

		int num;
		try {
			num = Integer.parseInt(br.readLine());

			switch (num) {
			case 1: {
				// 마이페이지
				mypageMenu.UserInfo(); // 유저 정보를 보여 줌
				mypageMenu.etcMenu(); //
				break;
			}
			case 2: {
				// 계좌 개설
				break;
			}
			case 3: {
				// 거래 내역
				break;
			}
			case 4: {
				// 입금
				break;
			}
			case 5: {
				// 출금
				break;
			}
			case 6: {
				// 이체
				break;

			}
			case 7: {
				// 로그아웃
				System.out.println("로그아웃 되었습니다.");
				SessionManager.logout();
				new FirstMainMenu().mainMenu();
				break;
			}
			case 0: {
				// 메인화면
				System.out.println("메인화면으로 돌아갑니다.");
				new FirstMainMenu().mainMenu();
				break;
			}
			default:
				System.out.println("올바른 숫자를 입력해주세요.");
			}

		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
