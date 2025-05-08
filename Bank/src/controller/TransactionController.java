package controller;


import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import dto.AccountShowDTO;
import dto.TransactionDTO;
import model.TransactionHistoryVO;
import service.AccountService;
import service.TransactionService;
import util.Validator;

public class TransactionController {
	private final TransactionService ts = new TransactionService();
	private final AccountService as = new AccountService();
	private final Scanner sc = new Scanner(System.in);
	
	
	// 입금
	public void deposit() throws SQLException {
		 List<String> types = Arrays.asList("100");
		 List<AccountShowDTO> list = ts.getMyAccounts(types);
		 NumberFormat formatter = NumberFormat.getNumberInstance(Locale.KOREA);
		 
		 if(list == null || list.isEmpty()) {
			 System.out.println("📢 소지하고 있는 계좌가 없습니다. 먼저 계좌를 개설해 주세요."); return;
		 }
		 
		 System.out.println("================ [💰 입금 할 계좌 선택] ================");
		 System.out.println("❗ 중간에 '0'을 입력하면 메인 메뉴로 돌아갑니다.");
		 System.out.printf("%-5s %-20s %-12s %-10s%n", "번호", "계좌번호", "상품명", "잔액");
		 System.out.println("---------------------------------------------------");
		 for (AccountShowDTO dto : list) {
		        System.out.printf("%-6s %-21s %-11s %,5d원%n",
		            dto.getAccountNum(),
		            dto.getAccountNo(),
		            dto.getProductName(),
		            dto.getBalance()
		        	);
		        }
		 System.out.println("===================================================");
		 
		 String accountNo;
		 while(true) {
		 accountNo = getInput("🖊️ 계좌번호를 입력하세요 : ");
		 	if(accountNo == null) return;
		 	if(!Validator.isValidHyphenAccountNumber(accountNo)) {
		 		System.out.println("⚠️ 잘못된 계좌번호 형식입니다. 예: 123-456-7890"); continue;
		 	}
		 	if(ts.checkAccountNo(accountNo)) break;
		 	else System.out.println("🚫 예적금 계좌이거나 본인 명의 계좌가 아닙니다. 다시 시도해 주세요.");
		 }
		 
		 BigDecimal amount;
		 String a_input;
		 while(true) {
				a_input = getInput("💸 입금할 금액을 입력하세요 : ");
				if(a_input == null) return;
				if(!Validator.isValidNumber(a_input)) {
					System.out.println("⚠️ 잘못된 금액 형식입니다. 숫자로만 입력해주세요."); continue;
				}
				amount = new BigDecimal(a_input);
				if (amount.compareTo(BigDecimal.ZERO) < 0) {
			           System.out.println("⚠️ 금액은 0보다 커야 합니다. 다시 입력해 주세요.");
			           continue;
				}
				break;
		 }
		 	
			String pwd;
			while (true) {
				pwd = getInput("🔐 계좌 비밀번호 입력: ");
				if(pwd == null) return;
				if (!as.verifyPassword(accountNo, pwd)) {
					continue;
				}
				else {
					break;
				}
			}
		
		 TransactionDTO dto = new TransactionDTO(accountNo, "DEPOSIT", amount, accountNo);
		 ts.deposit(dto);
		 System.out.println("✅ 입금이 완료되었습니다!");
		 System.out.println("남은 잔액 : " + formatter.format(ts.getbalance(accountNo)) + "원");
	 }
	
	// 출금
	public void withdraw() throws SQLException {
		List<String> types = Arrays.asList("100");
		List<AccountShowDTO> list = ts.getMyAccounts(types);
		NumberFormat formatter = NumberFormat.getNumberInstance(Locale.KOREA);
		
		if(list == null || list.isEmpty()) {
			 System.out.println("📢 소지하고 있는 계좌가 없습니다. 먼저 계좌를 개설해 주세요."); return;
		 }
		
		System.out.println("================ [💸 출금 할 계좌 선택] ================");
		 System.out.println("❗ 중간에 '0'을 입력하면 메인 메뉴로 돌아갑니다.");
		 System.out.printf("%-5s %-20s %-12s %-10s%n", "번호", "계좌번호", "상품명", "잔액");
		 System.out.println("--------------------------------------------------");
		 for (AccountShowDTO dto : list) {
		        System.out.printf("%-6s %-21s %-11s %,5d원%n",
		            dto.getAccountNum(),
		            dto.getAccountNo(),
		            dto.getProductName(),
		            dto.getBalance()
		        	);
		        }
		 System.out.println("====================================================");

		String accountNo;
		 while(true) {
		 accountNo = getInput("🖊️ 출금할 계좌번호 입력: ");
		 	if(accountNo == null) return;
		 	if(!Validator.isValidHyphenAccountNumber(accountNo)) {
		 		System.out.println("⚠️ 잘못된 계좌번호 형식입니다."); continue;
		 	}
		 	if(ts.checkAccountNo(accountNo)) break;
		 	else System.out.println("🚫 예적금 계좌이거나 본인 명의 계좌가 아닙니다. 다시 시도해 주세요.");
		 }
		 
		 BigDecimal amount;
		 String a_input;
		 while(true) {
				a_input = getInput("💰 출금 금액 입력: ");
				if(a_input == null) return;
				if(!Validator.isValidNumber(a_input)) {
					System.out.println("⚠️ 잘못된 금액 형식입니다. 숫자로만 입력해주세요."); continue;
				}
				amount = new BigDecimal(a_input);
				if (amount.compareTo(BigDecimal.ZERO) < 0) {
			           System.out.println("⚠️ 금액은 0보다 커야 합니다.");
			           continue;
				}
				break;
		 }
		 	
			String pwd;
			while (true) {
				pwd = getInput("🔐 계좌 비밀번호 입력: ");
				if(pwd == null) return;
				if (!as.verifyPassword(accountNo, pwd)) {
					continue;
				}
				else {
					break;
				}
			}
			
		 TransactionDTO dto = new TransactionDTO(accountNo, "WITHDRAW", amount.negate(), accountNo);
		 ts.withdraw(dto);
		 System.out.println("✅ 출금 완료!" );
		 System.out.println("남은 잔액 : " + formatter.format(ts.getbalance(accountNo)) + "원");
	 }
	
	//이체
	public void transfer() throws SQLException {
		List<String> types = Arrays.asList("100");
		List<AccountShowDTO> list = ts.getMyAccounts(types);
		NumberFormat formatter = NumberFormat.getNumberInstance(Locale.KOREA);
		
		if(list == null || list.isEmpty()) {
			 System.out.println("📢 소지하고 있는 계좌가 없습니다. 먼저 계좌를 개설해 주세요."); return;
		 }
		
		System.out.println("================ [🔁 이체 할 출금 계좌 선택] ================");
		 System.out.println("❗ 중간에 '0'을 입력하면 메인 메뉴로 돌아갑니다.");
		 System.out.printf("%-5s %-20s %-12s %-10s%n", "번호", "계좌번호", "상품명", "잔액");
		 System.out.println("------------------------------------------------------");
		 for (AccountShowDTO dto : list) {
		        System.out.printf("%-6s %-21s %-11s %,5d원%n",
		            dto.getAccountNum(),
		            dto.getAccountNo(),
		            dto.getProductName(),
		            dto.getBalance()
		        	);
		        }
		 System.out.println("====================================================");

		String w_accountNo;
		 while(true) {
		 w_accountNo = getInput("🖊️ 출금할 계좌번호 입력: ");
		 	if(w_accountNo == null) return;
		 	if(!Validator.isValidHyphenAccountNumber(w_accountNo)) {
		 		System.out.println("⚠️ 잘못된 계좌번호 형식입니다."); continue;
		 	}
		 	if(ts.checkAccountNo(w_accountNo)) break;
		 	else System.out.println("🚫 예적금 계좌이거나 본인 명의 계좌가 아닙니다. 다시 시도해 주세요.");
		 }
		
		 String d_accountNo;
		 while(true) {
		 d_accountNo = getInput("📥 이체 받을 계좌번호 입력: ");
		 	if(d_accountNo == null) return;
		 	if(!Validator.isValidHyphenAccountNumber(d_accountNo)) {
		 		System.out.println("⚠️ 잘못된 계좌번호 형식입니다."); continue;
		 	}
		 	if(ts.targetCheckAccountNo(d_accountNo)) break;
		 	else System.out.println("🚫 예금 계좌이거나 존재하지 않는 계좌번호입니다. 다시 시도해 주세요.");
		 }
		
		int productType = ts.productType(d_accountNo);
		BigDecimal amount = BigDecimal.ZERO;
		String a_input;
		
		if(productType == 100) {
			while(true) {
				a_input = getInput("💸 이체 금액 입력: ");
				if(a_input == null) return;
				if(!Validator.isValidNumber(a_input)) {
					System.out.println("⚠️ 잘못된 금액 형식입니다. 숫자로만 입력해주세요."); continue;
				}
				amount = new BigDecimal(a_input);
				if (amount.compareTo(BigDecimal.ZERO) < 0) {
			           System.out.println("⚠️ 금액은 0보다 커야 합니다.");
			           continue;
				}
				break;
			}
		}
		
		if(productType == 200) {
			BigDecimal maxMonthlyDeposit = ts.maxMonthlyDeposit(d_accountNo);
			BigDecimal monthlyDepositAmount = ts.monthlyDepositAmount(d_accountNo);
			BigDecimal remainAmount = maxMonthlyDeposit.subtract(monthlyDepositAmount);
			
			
			System.out.println("============ 🏦 적금 계좌 안내 ============");
			System.out.println("✅ 최소 입금 가능 금액 : 1원");
			System.out.println("💰 월 입금 한도      : " + formatter.format(maxMonthlyDeposit) + "원");
			System.out.println("📈 이번 달 누적 입금액 : " + formatter.format(monthlyDepositAmount) + "원");
			System.out.println("📉 잔여 입금 가능액   : " + formatter.format(remainAmount) + "원");
			System.out.println("========================================");
			System.out.println("💳 출금 계좌 잔액 : " + formatter.format(ts.getbalance(w_accountNo)) + "원");
			
			while(true) {
				a_input = getInput("💸 이체 금액 입력: ");
				if(a_input == null) return;
				if(!Validator.isValidNumber(a_input)) {
					System.out.println("⚠️ 잘못된 금액 형식입니다. 숫자로만 입력해주세요."); continue;
				}
				amount = new BigDecimal(a_input);
				if(amount.compareTo(remainAmount) > 0) {
					System.out.println("⚠️ 이체 가능 금액을 초과했습니다. 다시 입력해 주십시오."); continue;
				}
				if(amount.compareTo(ts.getbalance(w_accountNo)) > 0) {
					System.out.println("⚠️ 출금 계좌의 잔액을 초과했습니다. 다시 입력해 주십시오."); continue;
				}
				if (amount.compareTo(BigDecimal.ZERO) < 0) {
			           System.out.println("⚠️ 금액은 0보다 커야 합니다.");
			           continue;
				}
				break;
			}
		}
		 
		String pwd;
		while (true) {
			pwd = getInput("🔐 계좌 비밀번호 입력: ");
			if(pwd == null) return;
			if (!as.verifyPassword(w_accountNo, pwd)) {
				continue;
			}
			else {
				break;
			}
		}

		 TransactionDTO w_dto = new TransactionDTO(w_accountNo, "TRANSFER", amount.negate(), d_accountNo);
		 TransactionDTO d_dto = new TransactionDTO(d_accountNo, "TRANSFER", amount, w_accountNo);
		 ts.withdraw(w_dto);
		 ts.deposit(d_dto);
		
		 System.out.println("✅ 이체 완료!");	        
		 System.out.println("출금 계좌 남은 잔액 : " + formatter.format(ts.getbalance(w_accountNo)) + "원");
		}
		
	// 거래내역
	 public void transactionHistory () {
		 List<String> types = Arrays.asList("100","200","300");
		 List<AccountShowDTO> accountList = ts.getMyAccounts(types);
		 NumberFormat formatter = NumberFormat.getNumberInstance(Locale.KOREA);
		 
		 if(accountList == null || accountList.isEmpty()) {
			 System.out.println("📢 소지하고 있는 계좌가 없습니다. 먼저 계좌를 개설해 주세요."); return;
		 }
		 
		 System.out.println("================ [📄 계좌 선택] =======================");
		 System.out.println("❗ 중간에 '0'을 입력하면 메인 메뉴로 돌아갑니다.");
		 System.out.printf("%-5s %-20s %-12s %-10s%n", "번호", "계좌번호", "상품명", "잔액");
		 System.out.println("----------------------------------------------------");
		 for (AccountShowDTO dto : accountList) {
		        System.out.printf("%-6s %-21s %-11s %,5d원%n",
		            dto.getAccountNum(),
		            dto.getAccountNo(),
		            dto.getProductName(),
		            dto.getBalance()
		        	);
		        }
		 System.out.println("====================================================");

		 
			String accountNo;
			 while(true) {
				accountNo = getInput("🖊️ 조회할 계좌번호 입력: ");
			 	if(accountNo == null) return;
			 	if(!Validator.isValidHyphenAccountNumber(accountNo)) {
			 		System.out.println("⚠️ 올바른 계좌번호 형식이 아닙니다."); continue;
			 	}
			 	if(ts.transactionCheckAccountNo(accountNo)) break;
			 	else System.out.println("🚫 본인 명의 계좌가 아닙니다. 다시 시도해 주세요.");
			 }
		 
		 
		 List<TransactionHistoryVO> TransactionList = ts.TransactionHistory(accountNo);
		 DateTimeFormatter d_formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH:mm:ss");
		 
		 if (TransactionList == null || TransactionList.isEmpty()) {
		        System.out.println("\n📭 거래 내역이 없습니다.");
		        return;
		    }
		 
		 System.out.println("=========================[📊 거래 내역]=========================");
		 System.out.printf("%-22s %-6s %-12s %-10s%n", "날짜", "구분", "금액", "상대방");
		 System.out.println("--------------------------------------------------------------");

		 for (TransactionHistoryVO vo : TransactionList) {
		     String date = vo.getTransactionDate().format(d_formatter);
		     String type = vo.getTransactionType();
		     String amount = formatter.format(vo.getAmount()) + "원";
		     String target = vo.getTargetAccount() == null ? "" : vo.getTargetAccount();

		     System.out.printf("%-22s %-6s %-12s %-10s%n", date, type, amount, target);
		 }

		 System.out.println("==============================================================");

	 }
	 
	 public String getInput(String info) {
		    System.out.print(info);
		    String input = sc.next().trim();
		    return input.equals("0") ? null : input;
		}
}
