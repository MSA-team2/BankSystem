package controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

// 사용자 메뉴
public class UserController {
	public static void MemberMenu() throws NumberFormatException, IOException {

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		System.out.println("===== 사용자 메뉴 =====");
		System.out.print("1.마이페이지\n2.계좌개설\n3.거래내역\n4.입금\n5.출금\n6.이체\n7.로그아웃\n0.메인화면\n선택 : ");

		int num = Integer.parseInt(br.readLine());

		switch (num) {
		case 1: {
			// 마이페이지
			// MyPageController.UserInfo();
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
			System.out.println("로그아웃 되었습니다.");
			SessionManager.logout();
			MemberController.MainMenu();
			break;

		}
		case 0: {
			// 메인화면
			System.out.println("메인화면으로 돌아갑니다.");
			SessionManager.logout();
			MemberController.MainMenu();
			break;

		}
		default:
			System.out.println("올바른 숫자를 입력해주세요.");
		}
	}
}
