package controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

import dbConn.util.CloseHelper;
import dbConn.util.ConnectionHelper;

public class AdminController {
	static Scanner sc = new Scanner(System.in);
	static PreparedStatement pstmt = null;
	static ResultSet rs = null;
	static Connection conn = null;
	static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

	// connect
	public static void connect() {
		try {
			conn = ConnectionHelper.getConnection("mysql");
			conn.setAutoCommit(false); // 자동커밋 끄기
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// close
	public static void close() {
		try {
			CloseHelper.close(rs);
			CloseHelper.close(pstmt);
			CloseHelper.close(conn);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// menu
//	public static void menu() throws SQLException, IOException {
//
//		while (true) {
//			System.out.println("===== 관리자 메뉴 =====");
//			System.out.println("1. 전체 회원 조회");
//			System.out.println("2. 전체 계좌 조회");
//			System.out.println("3. 일일 거래량");
//			System.out.println("4. 회원 관리");
//			System.out.println("5. 레코드 삭제");
//			System.out.println("6. 로그아웃");
//			System.out.println("7. 메인화면");
//			System.out.print("원하는 메뉴 선택하세요. : ");
//
//			switch (sc.nextInt()) {
//			case 0:
//				selectAllMember();
//				break;
//
//			case 1:
//				selectAccount();
//				break;
//
//			case 2:
//				showDailyTransferHistory();
//				break;
//			}
//		}
//	}

	public static void showDailyTransferHistory() throws SQLException {
		System.out.println("검색할 날짜를 입력해주세요 ex) 2025-03-15");
		System.out.print(">> ");
		String inputLocalDate = sc.nextLine();
		
		if (isValidDate(inputLocalDate)) {
			System.out.println("올바른 날짜를 입력해주세요");
			return;
		}

		PreparedStatement pstmt = conn.prepareStatement("SELECT " + "TRANSACTION_DATE," + " TRANSACTION_TYPE,"
				+ " COUNT(*) AS COUNT,"
				+ " SUM(AMOUNT) AS AMOUNT,"
				+ " SUM(CASE"
				+ "		WHEN TRANSACTION_TYPE = 'DEPOSIT' THEN AMOUNT"
				+ "		WHEN TRANSACTION_TYPE = 'WITHDRAWAL' THEN -AMOUNT"
				+ " ELSE 0 " + "END) AS NET_AMOUNT"
				+ " FROM TRANSACTION_HISTORY"
				+ " WHERE TRANSACTION_DATE = ?"
				+ " GROUP BY TRANSACTION_TYPE");
		pstmt.setDate(1, Date.valueOf(LocalDate.parse(inputLocalDate)));

		ResultSet rs = pstmt.executeQuery();

//		System.out.println("transaction_id" + "account_no" + "transaction_type" + "amount" + "transaction_date" + "target_account");

		int depositCount = 0, depositAmount = 0;
		int withdrawCount = 0, withdrawAmount = 0;
		int netTotalAmount = 0;
		LocalDate transaction_date = null;
		System.out.println("===== 일일 거래량 =====");
		while (rs.next()) {
			Date sqlTransacetion_date = rs.getDate("transaction_date");
			transaction_date = sqlTransacetion_date.toLocalDate();

			String transaction_type = rs.getString("transaction_type");
			int count = rs.getInt("count");
			int amount = rs.getInt("amount");
			int net_amount = rs.getInt("net_amount");

			switch (transaction_type) {
			case "DEPOSIT":
				depositCount += count;
				depositAmount += amount;
				break;
			case "WITHDRAWAL":
				withdrawCount += count;
				withdrawAmount += amount;
				break;
			}
			netTotalAmount += net_amount;
		}
		System.out.println("날짜 : " + transaction_date);
		System.out.println("입금 : " + depositCount + "건, " + depositAmount + "원");
		System.out.println("출금 : " + withdrawCount + "건, " + withdrawAmount + "원");
		System.out.println("자금 순유입 : " + netTotalAmount + "원");
	}

	public static void selectAccount() throws SQLException {
		String sql = "SELECT ACCOUNT_NO, M.NAME, ACCOUNT_PWD, BALANCE, STATUS, CREATED_DATE FROM ACCOUNT A"
				+ "JOIN MEMBER M ON A.MEMBER_NO = M.MEMBER_NO"
				+ "JOIN ";

		PreparedStatement pstmt = conn.prepareStatement(sql);
		ResultSet rs = pstmt.executeQuery();
		
		System.out.println("===== 전체 계좌 조회 =====");
		System.out.println("계좌번호" + "이름" + "계좌 비밀번호" + "잔액" + "계좌상태" + "계좌개설일");

		while (rs.next()) {
			int account_no = rs.getInt("account_no");
			String name = rs.getString("name");
			String account_pwd = rs.getString("account_pwd");
			String balance = rs.getString("balance");
			String status = rs.getString("status");
			String created_date = rs.getString("created_date");

			System.out.println(account_no + "\t" + name + "\t" + account_pwd + "\t" + balance + "\t" + status
					+ "\t" + created_date);
		}

	}

	public static void selectAllMember() throws SQLException {
		String sql = "SELECT * FROM MEMBER ORDER BY ROLE DESC";

		PreparedStatement pstmt = conn.prepareStatement(sql);
		ResultSet rs = pstmt.executeQuery();
		
		System.out.println("===== 전체 회원 조회 =====");
		System.out.println("이름" + "주민번호" + "비밀번호" + "주소" + "전화번호" + "계좌잠금여부" + "역할");

		while (rs.next()) {
			String name = rs.getString("name");
			String jumin = rs.getString("jumin");
			String member_id = rs.getString("member_id");
			String password = rs.getString("password");
			String address = rs.getString("address");
			String phone = rs.getString("phone");
			String lock_yn = rs.getString("lock_yn");
			String role;
			if (rs.getInt("role") == 0) {
				role = "User";
			} else {
				role = "Admin";
			}

			System.out.println(name + "\t" + jumin + "\t" + member_id + "\t" + password + "\t"
					+ address + "\t" + phone + "\t" + lock_yn + "\t" + role);
		}
	}

	public static boolean isValidDate(String input) {
		if (input == null || !input.matches("^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])$"))
			return false;

		try {
			LocalDate.parse(input);
			return true;
		} catch (DateTimeParseException e) {
			return false;
		}
	}
}
