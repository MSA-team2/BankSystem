package view;

import dbConn.util.ConnectionHelper;

public class MainEntry {
	public static void main(String[] args) {
		/**
		 * 아래 메뉴 불러오기       
			├── MainMenu.java
			├── MemberMenu.java
			├── AccountMenu.java
			├── TransactionMenu.java
			├── MypageMenu.java
			└── AdminMenu.java
		 */
		ConnectionHelper.getConnection("mysql"); // server 뜨면 성공
		
	}

}
