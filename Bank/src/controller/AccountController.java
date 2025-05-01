package controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
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
		
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		
		try {
			String sql1 = "select * from product";
			pstmt = conn.prepareStatement(sql1);
			rs = pstmt.executeQuery();

			// 어떤 상품이 있는지 콘솔에 뿌리기 & 상품 id와 이름 map에 저장
			while (rs.next()) {
				int product_id = rs.getInt("product_id");
				String product_name = rs.getString("product_name");
				double interest_rate = rs.getDouble("interest_rate");
				int period_months = rs.getInt("period_months");
				map.put(product_id, period_months);		// map에 저장
				
				System.out.println(product_id + ". " + product_name + " (금리 " + interest_rate + "%)");
			}
			System.out.print("번호 선택: ");

			// 번호 입력에 따른 계좌 번호 생성 시작
			createAccount(map);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// 계좌 생성 메서드
	public static void createAccount(Map map) {
		MemberVO currentUser = SessionManager.getCurrentUser();
		
		try {
			// 계좌번호 생성
			StringBuilder sb = new StringBuilder();
			Random rand = new Random();
			int randomNumber = rand.nextInt(9999) + 1;
			String account_uniqNumber = String.format("%04d", randomNumber);
			int product_no = sc.nextInt();
			System.out.println("\n\n");
			long deposit_amount = 0;
			switch (product_no) {
			case 1: {	// 입출금
				sb.append("100-");
				sb.append(currentUser.getPhone().substring(9)+"-");
				sb.append(account_uniqNumber);
				break;
			}
			case 2, 3, 7: {		// 예금
				sb.append("200-");
				sb.append(currentUser.getPhone().substring(9)+"-");
				sb.append(account_uniqNumber);
				break;
			}
			case 4, 5, 6, 8, 9, 10: {	// 적금
				sb.append("300-");
				sb.append(currentUser.getPhone().substring(9)+"-");
				sb.append(account_uniqNumber);
				System.out.println("매월 얼마를 납입할 지 입력해주세요");
				deposit_amount = sc.nextLong();
				break;
			}
			}
			String account_no = sb.toString();
			
			
			System.out.print("초기 입금액: ");
			long balance = sc.nextLong();
			
			String account_password;
			String account_password_check;
			while(true) {
				System.out.print("\n계좌 비밀번호 (4자리): ");
				account_password = sc.next();
				System.out.print("계좌 비밀번호 확인: ");
				account_password_check = sc.next();
				if(account_password.equals(account_password_check)) break;
				else {
					System.out.println("비밀번호가 일치하지 않습니다. 다시 입력해주세요");
					continue;
				}
			}
			
			// 계좌 등록
			String sql = "INSERT INTO ACCOUNT (account_no, member_no, product_id, account_pwd,"
					+ " balance, status, lock_cnt, created_date, deposit_amount, maturity_date) "
					+ "VALUES (?, ?, ?, ?, ?, 'Y', 0, NOW(), ?, ?)";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, account_no);
			pstmt.setInt(2, currentUser.getMemberNo());
			pstmt.setInt(3, product_no);
			pstmt.setString(4, account_password);
			pstmt.setLong(5, balance);
			if(account_no.substring(0,3).equals("300")) {
				pstmt.setLong(6, deposit_amount);	// 납입금액
			}else {
				pstmt.setNull(6, java.sql.Types.BIGINT); 
			}
			if(account_no.substring(0,3).equals("200") || account_no.substring(0,3).equals("300")) {
				int maturity_months = (int) map.get(product_no);
			    LocalDate maturityDate = LocalDate.now().plusMonths(maturity_months);
			    pstmt.setDate(7, Date.valueOf(maturityDate));
			} else {
			    pstmt.setNull(7, java.sql.Types.DATE);
			}
			int result = pstmt.executeUpdate();
			
			if(result > 0) {
				System.out.println("계좌 개설에 성공하였습니다.");
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
