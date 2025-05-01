package view;

import controller.MemberController;

public class MainEntry {
	public static void main(String[] args) {
		
		// 첫화면
		public void MainMenu() {
			
			while(true) {
				System.out.println("\n======== 투게더 은행 ========");
				System.out.println("\t1. 회원가입");
				System.out.println("\t2. 로그인");
				System.out.println("\t3. 아이디 찾기");			
				System.out.println("\t4. 비밀번호 찾기");			
				System.out.println("\t0. 종료");
				System.out.println("===========================");
				System.out.print("메뉴 선택: ");
				int menu = sc.nextInt();
				sc.nextLine();
				
				switch (menu) {
				case 1: mc.joinMember(); break;
				case 2: mc.loginMember(); break;
				case 3: mc.findId(); break;
				case 4: mc.findPwd(); break;
				case 0: System.out.println("감사합니다. 안녕히가세요"); System.exit(0); break;
				default: System.out.println("잘못 입력하셨습니다. 다시 입력해주세요");
				}
			}
		}
		
		
	}

}