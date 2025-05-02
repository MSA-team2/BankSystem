package controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Scanner;

import dbConn.util.CloseHelper;
import dbConn.util.ConnectionHelper;
import model.AccountVO;
import model.MemberVO;

public class AdminMemberController {
	static Scanner sc = new Scanner(System.in);
	static PreparedStatement pstmt = null;
	static ResultSet rs = null;
	static Connection conn = null;
	static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

	// connect
	public static void connect() {
		try {
			conn = ConnectionHelper.getConnection("mysql");
			conn.setAutoCommit(true);
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

//	public static void menu() throws SQLException, IOException {
//
//		while (true) {
//			System.out.println("===== 회원 관리 =====");
//			System.out.println("1. 계좌 잠금 관리");
//			System.out.println("2. 회원 검색");
//			System.out.println("2. 회원 정보 수정");
//			System.out.println("4. 이전 메뉴");
//			System.out.println("5. 로그아웃");
//			System.out.println("6. 메인화면");
//			System.out.print("원하는 메뉴 선택하세요. : ");
//
//			switch (sc.nextInt()) {
//			case 0:
//				selectMember();
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
	
	public static void editMember() throws SQLException {
		MemberVO loginMember = SessionManager.getCurrentUser();
		System.out.println("===== 회원 정보 수정 =====");
		System.out.println("회원 이름 : " + loginMember.getName());
		System.out.println("주민번호 : " + loginMember.getJumin());
		System.out.println("");
		
		System.out.println("===== 현재 정보 =====");
		System.out.println("이름 : " + loginMember.getName());
		System.out.println("전화번호 : " + loginMember.getPhone());
		System.out.println("주소 : " + loginMember.getAddress());
		
		System.out.println("수정할 정보를 입력하세요.");
		System.out.print("새 전화번호 : ");
		String newPhone = sc.next();
		
		System.out.print("새 주소");
		String newAddress = sc.next();
		
		PreparedStatement pstmt = conn.prepareStatement(
				"update member"
				+ "set phone = ?,"
				+ "address = ?"
				+ "wherer member_no = ?");
		
		pstmt.setString(1, newPhone);
		pstmt.setString(2, newAddress);
		pstmt.setInt(3, loginMember.getMemberNo());
		
		pstmt.executeUpdate();
		
		System.out.print("회원정보가 수정되었습니다.");
	}
	
	public static void findMember() {
		MemberVO loginMember = SessionManager.getCurrentUser();
		System.out.println("===== 회원 검색 =====");
		System.out.print("회원 이름 : ");
		String findMemberName = sc.next();
		
		System.out.print("주민번호 : ");
		String findMemberJumin = sc.next();
		
		PreparedStatement pstmt;
		try {
			pstmt = conn.prepareStatement(
					"select * from member where name = ? and jumin = ?");
			pstmt.setString(1, findMemberName);
			pstmt.setString(2, findMemberJumin);
			pstmt.setInt(3, loginMember.getMemberNo());
			
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		System.out.print("회원정보가 수정되었습니다.");
	}

	public static void validationMember() throws SQLException {
		if (!SessionManager.isLoggedIn()) {
			System.out.println("로그인이 필요합니다.");
			return;
		}

		if (!SessionManager.isAdmin()) {
			System.out.println("관리자가 아닙니다.");
			return;
		}
	}

	public static void showAccountByMember() throws SQLException {
		MemberVO loginMember = SessionManager.getCurrentUser();
		System.out.println("===== 계좌 잠금 관리 =====");
		System.out.println("회원 이름 : " + loginMember.getName());
		System.out.println("주민번호 : " + loginMember.getJumin());

		PreparedStatement pstmt = conn.prepareStatement("select ROW_NUMBER() OVER (ORDER BY account_no desc) AS 번호,"
				+ " a.account_no," + " b.name," + " d.product_name, " + " a.status" + " a.created_date"
				+ " from account a " + "join member b on a.member_no = m.member_no "
				+ "join product p on c.product_id = p.product_id " + "where a.member_no = ?");
		ResultSet rs = pstmt.executeQuery();

		System.out.println("번호" + "\t" + "계좌번호" + "\t" + "계좌주" + "\t" + "상품명" + "\t" + "계좌상태" + "\t" + "개설일");

		while (rs.next()) {
			String sort = rs.getString("번호");
			int account_no = rs.getInt("a.account_no");
			int name = rs.getInt("b.name");
			String product_name = rs.getString("d.product_name");
			String status = rs.getString("status");
			String created_date = rs.getString("created_date");

			System.out.println(sort + "\t" + account_no + "\t" + account_no + "\t" + name + "\t" + product_name + "\t"
					+ status + "\t" + created_date);
		}
	}

	public static void updateAccount() throws SQLException {
		System.out.println("상태를 변경할 계좌번호를 입력해주세요");
		System.out.println("예시) xxx-xxxx-xxxx");
		System.out.println("예시) 100-2542-2222");
		System.out.print(">> ");

		String inputAccountNumber = sc.next();
		if (isValidHyphenPhoneNumber(inputAccountNumber)) {
			System.out.println("올바르지 않은 계좌번호 양식입니다.");
			return;
		}
		
		AccountVO findAccount = getAccountInfo(inputAccountNumber);

		System.out.println("현재 상태: " + findAccount.getStatus());
		System.out.print("(N: 잠금, Y: 정상): ");
		String statusCode = sc.next();

		PreparedStatement pstmt = conn.prepareStatement("update account " + "set status = ?");

		pstmt.setString(1, statusCode);
		pstmt.executeUpdate();

		findAccount = getAccountInfo(inputAccountNumber);
		System.out.println("계좌가 " + findAccount.getStatus() + "상태로 변경되었습니다.");
	}

	public static AccountVO getAccountInfo(String AccountNumber) throws SQLException {
		PreparedStatement pstmt = conn.prepareStatement("select * from account where account_no = ?");
		pstmt.setString(1, AccountNumber);
		ResultSet rs = pstmt.executeQuery();

		rs.next();
		AccountVO ao = new AccountVO();

		ao.setAccountNo(rs.getString("account_no"));
		ao.setMemberNo(rs.getInt("member_no"));
		ao.setAccountPwd(rs.getString("account_no"));
		ao.setBalance(rs.getBigDecimal("balance"));
		ao.setStatus(rs.getString("status").charAt(0));
		ao.setCreateDate(rs.getObject("created_date", LocalDate.class));

		return ao;
	}

	public static boolean isValidHyphenPhoneNumber(String input) {
		// 3-4-4 형식의 번호 (예: 101-4444-4444), 하이픈 필수
		String pattern = "^\\d{3}-\\d{4}-\\d{4}$";
		return input != null && input.matches(pattern);
	}
}
