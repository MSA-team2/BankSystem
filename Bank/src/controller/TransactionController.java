package controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import dbConn.util.ConnectionHelper;
import model.MemberVO;

public class TransactionController {
	
	static Scanner sc = new Scanner(System.in);
	static Connection conn = null;
	static PreparedStatement 
			pstmtSearch, pstmtDeposit, pstmtWithdraw, 
			pstmtTransfer, pstmtTSearch, pstmtPwd, pstmtsqlTDeposit = null;
	static ResultSet rs = null;  
	
	//입금
	public static void deposit() throws SQLException {
		try {
			// 트랜젝션 세팅
			transactionConnect();
			if(!rs.isBeforeFirst()) {
				System.out.println("계좌가 존재하지 않습니다.");
				return;
			}
			String sqlDeposit = "update account set balance = balance + ? where account_no = ?"; 
			pstmtDeposit = conn.prepareStatement(sqlDeposit);
			conn.setAutoCommit(false);
			
			// 계좌 선택 (정보 출력)
			Map<String, Long> d_balanceMap = new HashMap<>();
			System.out.println("------입금할 계좌 선택-------");
			System.out.println("번호\t계좌번호\t상품명\t잔액");
			while(rs.next()) {
				String number = rs.getString("번호");
				String account_no = rs.getString("account_no");
				String product_name = rs.getString("product_name");
				long balance = rs.getInt("balance");
				
				 d_balanceMap.put(account_no, balance);
				System.out.println(number+"\t"+account_no+"\t"+product_name+"\t"+balance);
			}
			
			//계좌 선택
			System.out.print("계좌 번호를 입력해주세요 (0 취소) : "); 
			String inputAccountNumber = sc.next().trim();
			if(inputAccountNumber.equals("0")) { 
				System.out.println("취소되었습니다.");
				conn.rollback();
				conn.setAutoCommit(true);
				//MemberController.menu();
				//System.exit(0);
				return; 
			}
			else if (!isValidHyphenPhoneNumber(inputAccountNumber)) {
				System.out.println("올바르지 않은 계좌번호 양식입니다.");
				return;
			}
			pstmtDeposit.setString(2, inputAccountNumber);
			
			
			//입금
			System.out.println("------ 입금 ------");
			System.out.print("입금액 : ");
			int d_money = sc.nextInt();
			pstmtDeposit.setInt(1, d_money);
			pstmtDeposit.executeUpdate();
			
			//트랜젝션 처리
			String sqlTDeposit = "insert into TRANSACTION_HISTORY (account_no, transaction_type, amount, transaction_date, target_account) VALUES (?, ?, ?, NOW(), ?);";
			pstmtsqlTDeposit = conn.prepareStatement(sqlTDeposit);
			
			pstmtsqlTDeposit.setString(1, inputAccountNumber);
			pstmtsqlTDeposit.setString(2, "입금");
			pstmtsqlTDeposit.setInt(3, d_money);
			pstmtsqlTDeposit.setString(4, "본인");
			pstmtsqlTDeposit.executeUpdate();
			
			// 완료한 후
			long updatedBalance = d_balanceMap.get(inputAccountNumber) + d_money;
			System.out.println(d_money + "원이 입금되었습니다. 현재 잔액 : " + updatedBalance);
						
			conn.commit();
			conn.setAutoCommit(true);
		} 
		catch (SQLException e) {
			conn.rollback();
			conn.setAutoCommit(true);
			e.printStackTrace();
		}
	}// end deposit()
	
	// 출금
	public static void withdraw() throws SQLException {
		try {
			// 트랜젝션 세팅
			transactionConnect();
			if(!rs.isBeforeFirst()) {
				System.out.println("계좌가 존재하지 않습니다.");
				return;
			}
			String sqlWithdraw = "update account set balance = balance - ? where account_no = ? and balance >= ?";
			pstmtWithdraw = conn.prepareStatement(sqlWithdraw);
			conn.setAutoCommit(false);
			
			// 계좌 선택 (정보 출력)
			Map<String, Long> w_balanceMap = new HashMap<>();
			System.out.println("------출금할 계좌 선택-------");
			System.out.println("번호\t계좌번호\t상품명\t잔액");
			while (rs.next()) {
				String number = rs.getString("번호");
				String account_no = rs.getString("account_no");
				String product_name = rs.getString("product_name");
				long balance = rs.getInt("balance");
				
				w_balanceMap.put(account_no, balance);
				System.out.println(number + "\t" + account_no + "\t" + product_name + "\t" + balance);
			}

			// 계좌 선택
			System.out.print("계좌 번호를 입력해주세요 (0 취소) : "); 
			String inputAccountNumber = sc.next().trim();
			if(inputAccountNumber.equals("0")) { 
				System.out.println("취소되었습니다.");
				conn.rollback();
				conn.setAutoCommit(true);
				//MemberController.menu();
				//System.exit(0);
				return; 
			}
			else if (!isValidHyphenPhoneNumber(inputAccountNumber)) {
				System.out.println("올바르지 않은 계좌번호 양식입니다.");
				return;
			}
			pstmtWithdraw.setString(2, inputAccountNumber);
			
			//비밀번호
			String sqlPwd = "select account_pwd, status, lock_cnt from account where account_no = ?";
			pstmtPwd = conn.prepareStatement(sqlPwd);
			pstmtPwd.setString(1, inputAccountNumber);
			rs = pstmtPwd.executeQuery();	
			
			if (!rs.next()) {
		            System.out.println("해당 계좌가 존재하지 않습니다.");
		            conn.rollback();
		            conn.setAutoCommit(true);
		            return;
		    }
			
			
			String pwd = rs.getString("account_pwd");
			String status = rs.getString("status");
			int lock_cnt = rs.getInt(3);
			
			if ("N".equalsIgnoreCase(status)) {
		            System.out.println("해당 계좌는 잠겨 있습니다.");
		            conn.rollback();
		            conn.setAutoCommit(true);
		            return;
		    }
			
			while (lock_cnt < 5) {
				System.out.print("계좌 비밀번호: ");
				String inputPwd = sc.next().trim();

				if (!inputPwd.equals(pwd)) {
					lock_cnt++;

					// 틀린 횟수 업데이트
					String sqlUpdateLockCnt = "UPDATE account SET lock_cnt = ? WHERE account_no = ?";
					PreparedStatement pstmtUpdate_L = conn.prepareStatement(sqlUpdateLockCnt);
					pstmtUpdate_L.setInt(1, lock_cnt);
					pstmtUpdate_L.setString(2, inputAccountNumber);
					pstmtUpdate_L.executeUpdate();

					if (lock_cnt == 5) {
						String sqlLock = "UPDATE account SET status = 'N' WHERE account_no = ?";
						PreparedStatement pstmtLock = conn.prepareStatement(sqlLock);
						pstmtLock.setString(1, inputAccountNumber);
						pstmtLock.executeUpdate();

						System.out.println("비밀번호 5회 틀렸습니다. 계좌가 잠겼습니다.");
						conn.commit();
						conn.setAutoCommit(true);
						return;
					} else {
						System.out.println("비밀번호가 틀렸습니다. 현재 " + lock_cnt + "회 오류.");
						conn.commit(); // 실시간 반영
						continue;
					}
				} else {
					// 성공 → lock_cnt 초기화
					String sqlLockReset = "UPDATE account SET lock_cnt = 0 WHERE account_no = ?";
					PreparedStatement pstmtReset = conn.prepareStatement(sqlLockReset);
					pstmtReset.setString(1, inputAccountNumber);
					pstmtReset.executeUpdate();
					break;
				}
			}
			
			// 출금
			System.out.println("------ 출금 ------");
			System.out.print("출금액 : ");
			int w_money = sc.nextInt();
			pstmtWithdraw.setInt(1, w_money);
			pstmtWithdraw.setInt(3, w_money);
			int updatedRows = pstmtWithdraw.executeUpdate();
			if (updatedRows == 0) { // 바뀐 행이 없다면.
			    System.out.println("잔액이 부족합니다. 다시 시도해 주세요.");
			    conn.rollback();
			    conn.setAutoCommit(true);
			    return;
			}
			
			//트랜젝션 처리
			String sqlTWithdraw = "insert into TRANSACTION_HISTORY (account_no, transaction_type, amount, transaction_date, target_account) VALUES (?, ?, ?, NOW(), ?);";
			PreparedStatement pstmtsqlTWithdraw = conn.prepareStatement(sqlTWithdraw);
			
			pstmtsqlTWithdraw.setString(1, inputAccountNumber);
			pstmtsqlTWithdraw.setString(2, "출금");
			pstmtsqlTWithdraw.setInt(3, w_money);
			pstmtsqlTWithdraw.setString(4, "본인");
			pstmtsqlTWithdraw.executeUpdate();
			
			// 완료한 후
			long updatedBalance = w_balanceMap.get(inputAccountNumber) - w_money;
			System.out.println(w_money + "원이 출금되었습니다. 현재 잔액 : " + updatedBalance);
			conn.commit();
			conn.setAutoCommit(true);
			
		} catch (SQLException e) {
			conn.rollback();
			conn.setAutoCommit(true);
			e.printStackTrace();
		}
		
	}// end withdraw()
	
	
	public static void transfer() throws SQLException {
			try {
				// 트랜젝션 세팅
				transactionConnect();
				if(!rs.isBeforeFirst()) {
					System.out.println("계좌가 존재하지 않습니다.");
					return;
				}
				String sqlWithdraw = "update account set balance = balance - ? where account_no = ? and balance >= ?";
				pstmtWithdraw = conn.prepareStatement(sqlWithdraw);
				conn.setAutoCommit(false);
				
				// 계좌 선택 (정보 출력)
				Map<String, Long> w_balanceMap = new HashMap<>();
				System.out.println("------이체할 계좌 선택-------");
				System.out.println("번호\t계좌번호\t상품명\t잔액");
				while (rs.next()) {
					String number = rs.getString("번호");
					String account_no = rs.getString("account_no");
					String product_name = rs.getString("product_name");
					long balance = rs.getInt("balance");
					
					w_balanceMap.put(account_no, balance);
					System.out.println(number + "\t" + account_no + "\t" + product_name + "\t" + balance);
				}

				// 계좌 선택
				System.out.print("계좌 번호를 입력해주세요 (0 취소) : "); 
				String w_inputAccountNumber = sc.next().trim();
				if(w_inputAccountNumber.equals("0")) { 
					System.out.println("취소되었습니다.");
					conn.rollback();
					conn.setAutoCommit(true);
					//MemberController.menu();
					//System.exit(0);
					return; 
				}
				else if (!isValidHyphenPhoneNumber(w_inputAccountNumber)) {
					System.out.println("올바르지 않은 계좌번호 양식입니다.");
					return;
				}
				pstmtWithdraw.setString(2, w_inputAccountNumber);
				
				//비밀번호
				String sqlPwd = "select account_pwd, status, lock_cnt from account where account_no = ?";
				pstmtPwd = conn.prepareStatement(sqlPwd);
				pstmtPwd.setString(1, w_inputAccountNumber);
				rs = pstmtPwd.executeQuery();	
				
				if (!rs.next()) {
			            System.out.println("해당 계좌가 존재하지 않습니다.");
			            conn.rollback();
			            conn.setAutoCommit(true);
			            return;
			    }
				
				
				String pwd = rs.getString("account_pwd");
				String status = rs.getString("status");
				int lock_cnt = rs.getInt(3);
				
				if ("N".equalsIgnoreCase(status)) {
			            System.out.println("해당 계좌는 잠겨 있습니다.");
			            conn.rollback();
			            conn.setAutoCommit(true);
			            return;
			    }
				
				while (lock_cnt < 5) {
					System.out.print("계좌 비밀번호: ");
					String inputPwd = sc.next().trim();

					if (!inputPwd.equals(pwd)) {
						lock_cnt++;

						// 틀린 횟수 업데이트
						String sqlUpdateLockCnt = "UPDATE account SET lock_cnt = ? WHERE account_no = ?";
						PreparedStatement pstmtUpdate_L = conn.prepareStatement(sqlUpdateLockCnt);
						pstmtUpdate_L.setInt(1, lock_cnt);
						pstmtUpdate_L.setString(2, w_inputAccountNumber);
						pstmtUpdate_L.executeUpdate();

						if (lock_cnt == 5) {
							String sqlLock = "UPDATE account SET status = 'N' WHERE account_no = ?";
							PreparedStatement pstmtLock = conn.prepareStatement(sqlLock);
							pstmtLock.setString(1, w_inputAccountNumber);
							pstmtLock.executeUpdate();

							System.out.println("비밀번호 5회 틀렸습니다. 계좌가 잠겼습니다.");
							conn.commit();
							conn.setAutoCommit(true);
							return;
						} else {
							System.out.println("비밀번호가 틀렸습니다. 현재 " + lock_cnt + "회 오류.");
							conn.commit(); // 실시간 반영
							continue;
						}
					} else {
						// 성공 → lock_cnt 초기화
						String sqlLockReset = "UPDATE account SET lock_cnt = 0 WHERE account_no = ?";
						PreparedStatement pstmtReset = conn.prepareStatement(sqlLockReset);
						pstmtReset.setString(1, w_inputAccountNumber);
						pstmtReset.executeUpdate();
						break;
					}
				}
				
				//상대방 계좌 입력 
				String sqlDeposit = "update account set balance = balance + ? where account_no = ?"; 
				pstmtDeposit = conn.prepareStatement(sqlDeposit);
				
				System.out.print("상대방 계좌 번호를 입력해주세요 (0 취소) : ");
				String d_inputAccountNumber = sc.next().trim();
				if (d_inputAccountNumber.equals("0")) {
					System.out.println("취소되었습니다.");
					conn.rollback();
					conn.setAutoCommit(true);
					return;
				} else if (!isValidHyphenPhoneNumber(d_inputAccountNumber)) {
					System.out.println("올바르지 않은 계좌번호 양식입니다.");
					return;
				}
				
				// 이체
				System.out.println("------ 이체 ------");
				System.out.print("이체액 : ");
				int w_money = sc.nextInt();
				pstmtWithdraw.setInt(1, w_money);
				pstmtWithdraw.setInt(3, w_money);
				int updatedRows = pstmtWithdraw.executeUpdate();
				if (updatedRows == 0) { // 바뀐 행이 없다면.
				    System.out.println("잔액이 부족합니다. 다시 시도해 주세요.");
				    conn.rollback();
				    conn.setAutoCommit(true);
				    return;
				}
				
				//상대방 입금처리 
				pstmtDeposit.setInt(1, w_money);
				pstmtDeposit.setString(2, d_inputAccountNumber);
				pstmtDeposit.executeUpdate();
				
				// 완료한 후
				long updatedBalance = w_balanceMap.get(w_inputAccountNumber) - w_money;
				System.out.println(w_money + "원이 이체되었습니다. 현재 잔액 : " + updatedBalance);
			
			// 트랜젝션 처리
			// 나 -> 타인 
			PreparedStatement pstmtT_Withdraw = conn.prepareStatement(
					 "INSERT INTO TRANSACTION_HISTORY (account_no, transaction_type, amount, transaction_date, target_account) VALUES (?, ?, ?, NOW(), ?)"
				    );
			 pstmtT_Withdraw.setString(1, w_inputAccountNumber);
			 pstmtT_Withdraw.setString(2, "이체");
			 pstmtT_Withdraw.setInt(3, w_money);
			 pstmtT_Withdraw.setString(4, d_inputAccountNumber);
			 pstmtT_Withdraw.executeUpdate();
			
			 //타인 -> 나
			 PreparedStatement pstmtT_Deposit = conn.prepareStatement(
					 "INSERT INTO TRANSACTION_HISTORY (account_no, transaction_type, amount, transaction_date, target_account) VALUES (?, ?, ?, NOW(), ?)"
				    );
			 pstmtT_Deposit.setString(1, d_inputAccountNumber);
			 pstmtT_Deposit.setString(2, "이체");
			 pstmtT_Deposit.setInt(3, w_money);
			 pstmtT_Deposit.setString(4, w_inputAccountNumber);
			 pstmtT_Deposit.executeUpdate();
			 
			conn.commit();
			conn.setAutoCommit(true);
		} catch (SQLException e) {
			conn.rollback();
			conn.setAutoCommit(true);
			e.printStackTrace();
			}
	}// end transfer()
	
	
	public static void transactionHistroy() throws SQLException {
		transactionConnect();
		if(!rs.isBeforeFirst()) {
			System.out.println("계좌가 존재하지 않습니다.");
			return;
		}
		//계좌 선택
		System.out.println("------계좌 선택-------");
		System.out.println("번호\t계좌번호\t상품명\t잔액");
		while(rs.next()) {
			String number = rs.getString("번호");
			String account_no = rs.getString("account_no");
			String product_name = rs.getString("product_name");
			long balance = rs.getInt("balance");
			
			System.out.println(number+"\t"+account_no+"\t"+product_name+"\t"+balance);
		}
		
		//계좌 입력
		System.out.print("계좌 번호를 입력해주세요 (0을 누르시면 취소): ");
		String inputAccountNumber= sc.next().trim();
		
		
		// 거래 내역
		String sqlTSearch = "SELECT \n"
				+ "t.transaction_date AS 날짜, \n"
				+ "a.account_no AS 계좌번호,\n"
				+ "p.product_name AS 상품명,\n"
				+ "CASE \n"
				+ "WHEN t.transaction_type = 'DEPOSIT' THEN '입금'\n"
				+ "WHEN t.transaction_type = 'WITHDRAW' THEN '출금'\n"
				+ "        WHEN t.transaction_type = 'TRANSFER' THEN '이체'\n"
				+ "        ELSE t.transaction_type\n"
				+ "    END AS 구분, \n"
				+ "    t.amount AS 금액, \n"
//				+ "		a.balance AS 잔액,\n"
				+ "    CASE \n"
				+ "        WHEN t.transaction_type = '이체' THEN \n"
				+ "            (SELECT m2.name \n"
				+ "             FROM MEMBER m2 \n"
				+ "             JOIN ACCOUNT a2 ON m2.member_no = a2.member_no \n"
				+ "             WHERE a2.account_no = t.target_account)\n"
				+ "        ELSE '본인'\n"
				+ "    END AS 상대방\n"
				+ "FROM \n"
				+ "    TRANSACTION_HISTORY t\n"
				+ "JOIN \n"
				+ "    ACCOUNT a ON t.account_no = a.account_no\n"
				+ "JOIN \n"
				+ "    PRODUCT p ON a.product_id = p.product_id\n"
				+ "JOIN \n"
				+ "    MEMBER m ON a.member_no = m.member_no\n"
				+ "WHERE \n"
				+ "    m.member_no = ? and a.account_no = ?\n"
				+ "ORDER BY \n"
				+ "    t.transaction_date DESC";
		pstmtTSearch = conn.prepareStatement(sqlTSearch);
		pstmtTSearch.setInt(1, 2);
		pstmtTSearch.setString(2, inputAccountNumber);
		rs = pstmtTSearch.executeQuery();
		
		System.out.println("날짜\t구분\t금액\t상대방");
		while(rs.next()) {
			String date = rs.getString("날짜");
			String transaction_type = rs.getString("구분");
			String amount = rs.getString("금액");
			String name = rs.getString("상대방");
			
			System.out.println(date+"\t"+transaction_type+"\t"+amount+"\t"+name);
		}
		
		
	}//end transactionHistory()
	
	public static void transactionConnect() throws SQLException {
		conn = ConnectionHelper.getConnection("mysql");
		
		// 로그인 체크
		if (!SessionManager.isLoggedIn()) {
            System.out.println("로그인이 필요합니다.");
            return;
        }
		
		MemberVO currentUser = SessionManager.getCurrentUser(); 
		
		
			String sqlSearch = "select ROW_NUMBER() OVER (ORDER BY account_no) AS 번호, account_no, balance, p.product_name from account a\n"
					+ "join member b on a.member_no = b.member_no\n"
					+ "join product p on a.product_id = p.product_id\n"
					+ "where a.member_no = ?;";
		pstmtSearch = conn.prepareStatement(sqlSearch);
		pstmtSearch.setInt(1, currentUser.getMemberNo());
		rs = pstmtSearch.executeQuery();
	}
	
	public static boolean isValidHyphenPhoneNumber(String input) {
		// 3-4-4 형식의 번호 (예: 101-4444-4444), 하이픈 필수
		String pattern = "^\\d{3}-\\d{4}-\\d{4}$";
		return input != null && input.matches(pattern);
	}	
	
}// end transactionController

