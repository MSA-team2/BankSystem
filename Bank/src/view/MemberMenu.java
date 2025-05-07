package view;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;

import controller.AccountController;
import controller.SessionManager;
import controller.TransactionController;

public class MemberMenu {

	public static void MemberMainMenu() {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		// 직접 객체 생성 후 의존성 주입
		MypageMenu mypageMenu = new MypageMenu();
		AccountController account = new AccountController();
		TransactionController tc = new TransactionController();

		while (true) {
			System.out.println("\n================================");
			System.out.println("\t📋 사용자 메뉴 ");
			System.out.println("================================");
			System.out.print("\t[1] 마이페이지\n\t[2] 계좌개설\n\t[3] 거래내역\n\t[4] 입금\n\t[5] 출금\n\t[6] 이체\n\t[7] 로그아웃\n");
			System.out.println("================================");
			System.out.print("👉 메뉴를 선택하세요 : ");

			int num;
			try {
				num = Integer.parseInt(br.readLine());

				switch (num) {
				case 1: {
					// 마이페이지
					mypageMenu.UserInfo(); // 유저 정보를 보여 줌
					break;
				}
				case 2: {
					// 계좌 개설
					account.insertAccount();
					break;
				}
				case 3: {
					// 거래 내역
					tc.transactionHistory();
					break;
				}
				case 4: {
					// 입금
					tc.deposit();
					break;
				}
				case 5: {
					// 출금
					tc.withdraw();
					break;
				}
				case 6: {
					// 이체
					tc.transfer();
					break;

				}
				case 7: {
					// 로그아웃
					System.out.println("로그아웃 되었습니다.");
					SessionManager.logout();
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
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}
}