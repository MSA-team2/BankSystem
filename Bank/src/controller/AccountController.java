package controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Random;
import java.util.Scanner;

import dbConn.util.CloseHelper;
import dbConn.util.ConnectionSingletonHelper;
import model.MemberVO;

/*
 * 계좌개설/상품가입/계좌 비번 설정
 * 계좌번호 생성, 상품 선택, 비밀번호 설정, 계좌 리스트
 * ### 2. 계좌 관리 담당 (B)

- 계좌 개설(상품 선택 → 금액 입력 → 비밀번호 입력 → 계좌 생성)
- 계좌번호 생성 (상품번호-휴대폰뒤4자리-랜덤4자리 조합)
- 가입 기간, 금리 계산
- 상품가입 완료 시 계좌에 매핑

**중요**

- 계좌비밀번호 입력 → 암호화/검증 필요
- 가입 상품은 여러 개 가능 (N:M 구조 고려)
 */

public class AccountController {
	// 연결, 삽입, 삭제, 수정, 검색
	static Scanner sc = new Scanner(System.in);
	static PreparedStatement pstmt;
	static Statement stmt;
	static ResultSet rs;
	static Connection conn;
	static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

	// connect
	public static void connect() {
		try {
			conn = ConnectionSingletonHelper.getConnection("mysql");
			stmt = conn.createStatement();
			conn.setAutoCommit(false); // auto commit off
		} catch (Exception e) {
			e.printStackTrace();
		}
	} // end connect

	// close
	public static void close() {
		try {
			CloseHelper.close(rs);
			CloseHelper.close(stmt);
			CloseHelper.close(pstmt);
			CloseHelper.close(conn);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void AccountMenu() {
		if (!SessionManager.isLoggedIn()) {
            System.out.println("로그인이 필요합니다.");
            return;
        }

		System.out.println("===== 계좌 개설 =====");
		System.out.println("=== 상품 선택 ===");

		try {
			String sql1 = "select * from product";
			pstmt = conn.prepareStatement(sql1);
			rs = pstmt.executeQuery();

			// 어떤 상품이 있는지 콘솔에 뿌리기
			while (rs.next()) {
				int product_id = rs.getInt("product_id");
				String product_name = rs.getString("product_name");
				double interest_rate = rs.getDouble("interest_rate");
				System.out.println(product_id + ". " + product_name + " (" + interest_rate + ")");
			}
			System.out.print("번호 선택: ");

			// 번호 입력에 따른 계좌 번호 생성 시작
			createAccount();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// 계좌 생성 메서드
	public static void createAccount() {
		MemberVO currentUser = SessionManager.getCurrentUser();
		
		try {
			StringBuilder sb = new StringBuilder();
			Random rand = new Random();
			int randomNumber = rand.nextInt(9999) + 1;
			String account_uniqNumber = String.format("%04d", randomNumber);
			switch (sc.nextInt()) {
			case 1: {
				sb.append("300-");
				sb.append(currentUser.getPhone().substring(7)+"-");
				sb.append(account_uniqNumber);
				break;
			}
			case 2: {
				sb.append("200-");
				sb.append(currentUser.getPhone().substring(7)+"-");
				sb.append(account_uniqNumber);
				break;
			}
			case 3: {
				sb.append("100-");
				sb.append(currentUser.getPhone().substring(7)+"-");
				sb.append(account_uniqNumber);
				break;
			}
			}
			String account_no = sb.toString();
			System.out.print("초기 입금액: ");
			long balance = sc.nextLong();
			System.out.println("\n계좌 비밀번호 (4자리): ");
			String account_password = sc.next();
			
			String sql2 = "INSERT INTO ACCOUNT (ACCOUNT_NO, MEMBER_NO, ACCOUNT_PWD, BALANCE, STATUS) VALUES (?, ?, ?, ?, ?)";
			pstmt = conn.prepareStatement(sql2);
			pstmt.setString(1, account_no);
			pstmt.setInt(2, currentUser.getMemberNo());
			pstmt.setString(3, account_password);
			pstmt.setLong(4, balance);
			pstmt.setString(5, "Y");
			int result = pstmt.executeUpdate();
			
			if(result > 0) {
				System.out.println("계좌가 성공적으로 개설되었습니다.");
				System.out.println("계좌번호: " + sb.toString());
				conn.commit();
			}else {
				System.out.println("계좌 생성에 실패하였습니다. 다시 시도해주시기 바랍니다.");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
