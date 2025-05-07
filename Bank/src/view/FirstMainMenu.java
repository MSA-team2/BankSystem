package view;

import java.util.Scanner;
import controller.MemberController;

public class FirstMainMenu {

	private Scanner sc = new Scanner(System.in);
	private MemberController mc = new MemberController();

	// 첫화면
	public void mainMenu() {
		while(true) {
			System.out.println("\n================================");
			System.out.println("\t💼 TOGETHER BANK");
			System.out.println("================================");
			System.out.println("\t[1] 회원가입");
			System.out.println("\t[2] 로그인");
			System.out.println("\t[3] 아이디 찾기");
			System.out.println("\t[4] 비밀번호 재설정");
			System.out.println("\t[5] 관리자 문의");
			System.out.println("\t[0] 종료");
			System.out.println("================================");
			System.out.print(" 👉 메뉴를 선택하세요: ");
			int menu = sc.nextInt();
			sc.nextLine();

			switch (menu) {
			case 1: mc.insertMember(); break;
			case 2: mc.loginMember();; break;
			case 3: mc.findMemberId(); break;
			case 4: mc.findMemberPwd(); break;
			case 5: mc.isAccountLocked(); break;
			case 0: System.out.println(" 🙇‍♀️ 감사합니다. 안녕히가세요"); System.exit(0); break;
			default: System.out.println(" ⚠️ 잘못 입력하셨습니다. 다시 입력해주세요");
			}
		} // while
	} // mainMenu()

}