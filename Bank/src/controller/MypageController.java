package controller;

import model.MemberVO;
import view.FirstMainMenu;

public class MypageController {

	public static void UserInfo() {

		if (SessionManager.isLoggedIn() == false) {
			new FirstMainMenu().mainMenu();

		} else {
			MemberVO user = SessionManager.getCurrentUser();

			System.out.println("===== 마이페이지 =====");
			System.out.println("이름 : " + user.getName());
			System.out.println("전화번호 : " + user.getPhone());
			System.out.println("주소 : " + user.getAddress());

			System.out.println();
			System.out.println("--- 보유 계좌---");
			
			/*
			 이름,  
			 */

		}

	}
}
