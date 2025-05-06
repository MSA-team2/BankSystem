package controller;


import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
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
		 
		 System.out.println("------입금 할 계좌 선택------");
		 System.out.println("※ 입력 중 '0'을 입력하면 메인메뉴로 돌아갑니다.");
		 System.out.println("번호\t계좌번호\t상품명\t잔액");
		 
		 for(AccountShowDTO dto : list) {
			 System.out.println(dto.getAccountNum() + "\t" + dto.getAccountNo() 
			 + "\t" + dto.getProductName() + "\t" + dto.getBalance());
		 }
		 
		 String accountNo;
		 while(true) {
		 accountNo = getInput("계좌번호 : ");
		 	if(accountNo == null) return;
		 	if(!Validator.isValidHyphenAccountNumber(accountNo)) {
		 		System.out.println("올바른 계좌번호 형식이 아닙니다."); continue;
		 	}
		 	if(ts.checkAccountNo(accountNo)) break;
		 	else System.out.println("예적금 계좌이거나 소지하고 계신 계좌번호가 아닙니다. 다시 시도해주세요.");
		 }
		 
		 BigDecimal amount;
		 String a_input;
		 while(true) {
				a_input = getInput("입금 금액 : ");
				if(a_input == null) return;
				amount = new BigDecimal(a_input);
				if (amount.compareTo(BigDecimal.ZERO) < 0) {
			           System.out.println("다시 입력해주세요.");
			           continue;
				}
				break;
		 }
		 	
			String pwd;
			while (true) {
				pwd = getInput("계좌 비밀번호 : ");
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
		 System.out.println("입금되었습니다. 남은 잔액 : " + ts.getbalance(accountNo));
	 }
	
	// 출금
	public void withdraw() throws SQLException {
		List<String> types = Arrays.asList("100");
		List<AccountShowDTO> list = ts.getMyAccounts(types);

		System.out.println("------출금 할 계좌 선택------");
		System.out.println("※ 입력 중 '0'을 입력하면 메인메뉴로 돌아갑니다.");
		System.out.println("번호\t계좌번호\t상품명\t잔액");

		for (AccountShowDTO dto : list) {
			System.out.println(dto.getAccountNum() + "\t" + dto.getAccountNo() + "\t" + dto.getProductName() + "\t"
					+ dto.getBalance());
		}

		String accountNo;
		 while(true) {
		 accountNo = getInput("계좌번호 : ");
		 	if(accountNo == null) return;
		 	if(!Validator.isValidHyphenAccountNumber(accountNo)) {
		 		System.out.println("올바른 계좌번호 형식이 아닙니다."); continue;
		 	}
		 	if(ts.checkAccountNo(accountNo)) break;
		 	else System.out.println("예적금 계좌이거나 소지하고 계신 계좌번호가 아닙니다. 다시 시도해주세요.");
		 }
		 
		 BigDecimal amount;
		 String a_input;
		 while(true) {
				a_input = getInput("출금 금액 : ");
				if(a_input == null) return;
				amount = new BigDecimal(a_input);
				if (amount.compareTo(BigDecimal.ZERO) < 0) {
			           System.out.println("다시 입력해주세요.");
			           continue;
				}
				break;
		 }
		 	
			String pwd;
			while (true) {
				pwd = getInput("계좌 비밀번호 : ");
				if(pwd == null) return;
				if (!as.verifyPassword(accountNo, pwd)) {
					continue;
				}
				else {
					break;
				}
			}
			
		 TransactionDTO dto = new TransactionDTO(accountNo, "WITHDRAW", amount.negate(), "본인");
		 ts.withdraw(dto);
		 System.out.println("출금되었습니다. 남은 잔액 : " + ts.getbalance(accountNo));
	 }
	
	//이체
	public void transfer() throws SQLException {
		List<String> types = Arrays.asList("100");
		List<AccountShowDTO> list = ts.getMyAccounts(types);
		
		System.out.println("------이체 할 계좌 선택------");
		System.out.println("※ 입력 중 '0'을 입력하면 메인메뉴로 돌아갑니다.");
		System.out.println("번호\t계좌번호\t상품명\t잔액");

		for (AccountShowDTO dto : list) {
			System.out.println(dto.getAccountNum() + "\t" + dto.getAccountNo() + "\t" + dto.getProductName() + "\t"
					+ dto.getBalance());
		}

		String w_accountNo;
		 while(true) {
		 w_accountNo = getInput("계좌번호 : ");
		 	if(w_accountNo == null) return;
		 	if(!Validator.isValidHyphenAccountNumber(w_accountNo)) {
		 		System.out.println("올바른 계좌번호 형식이 아닙니다."); continue;
		 	}
		 	if(ts.checkAccountNo(w_accountNo)) break;
		 	else System.out.println("예적금 계좌이거나 등록되어 있는 계좌번호가 아닙니다. 다시 시도해주세요.");
		 }
		
		 String d_accountNo;
		 while(true) {
		 d_accountNo = getInput("상대 계좌번호 : ");
		 	if(d_accountNo == null) return;
		 	if(!Validator.isValidHyphenAccountNumber(d_accountNo)) {
		 		System.out.println("올바른 계좌번호 형식이 아닙니다."); continue;
		 	}
		 	if(ts.targetCheckAccountNo(d_accountNo)) break;
		 	else System.out.println("예금 계좌이거나 등록되지 않은 계좌번호 입니다. 다시 시도해주세요.");
		 }
		
		int productType = ts.productType(d_accountNo);
		BigDecimal amount = new BigDecimal("0");
		String a_input;
		
		if(productType == 100) {
			while(true) {
				a_input = getInput("이체 금액 : ");
				if(a_input == null) return;
				amount = new BigDecimal(a_input);
				if (amount.compareTo(BigDecimal.ZERO) < 0) {
			           System.out.println("다시 입력해주세요.");
			           continue;
				}
				break;
			}
		}
		
		if(productType == 200) {
			BigDecimal maxMonthlyDeposit = ts.maxMonthlyDeposit(d_accountNo);
			BigDecimal monthlyDepositAmount = ts.monthlyDepositAmount(d_accountNo);
			BigDecimal remainAmount = maxMonthlyDeposit.subtract(monthlyDepositAmount);
			
			System.out.println("[적금 계좌 안내]");
			System.out.println("최소 입금 가능 금액 : 1원");
			System.out.println("월 입금 한도 : " + maxMonthlyDeposit + "원");
			System.out.println("이번 달 누적 입금액 : " + monthlyDepositAmount + "원");
			System.out.println("이체 가능 잔여 한도: " + remainAmount + "원");
			System.out.println("----------------------------------------------");
			System.out.println("출금 계좌 현재 잔액 : " + ts.getbalance(w_accountNo) + "원");
			
			while(true) {
				a_input = getInput("이체 금액 : ");
				if(a_input == null) return;
				amount = new BigDecimal(a_input);
				if(amount.compareTo(remainAmount) > 0) {
					System.out.println("최대 입금 한도를 초과했습니다. 다시 입력해 주십시오."); continue;
				}
				if(amount.compareTo(ts.getbalance(w_accountNo)) > 0) {
					System.out.println("출금 가능 잔액을 초과하였습니다. 다시 입력해 주십시오."); continue;
				}
				if (amount.compareTo(BigDecimal.ZERO) < 0) {
			           System.out.println("다시 입력해주세요.");
			           continue;
				}
				break;
			}
		}
		 
		String pwd;
		while (true) {
			pwd = getInput("계좌 비밀번호 : ");
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
		
		 System.out.println("출금되었습니다. 남은 잔액 : " + ts.getbalance(w_accountNo) + "원");	        
		}
		
	// 거래내역
	 public void transactionHistory () {
		 List<String> types = Arrays.asList("100","200","300");
		 List<AccountShowDTO> accountList = ts.getMyAccounts(types);
		 System.out.println("------계좌 선택------");
		 System.out.println("※ 입력 중 '0'을 입력하면 메인메뉴로 돌아갑니다.");
		 System.out.println("번호\t계좌번호\t상품명\t잔액");

			for (AccountShowDTO dto : accountList) {
				System.out.println(dto.getAccountNum() + "\t" + dto.getAccountNo() + "\t" + dto.getProductName() + "\t"
						+ dto.getBalance());
			}
		 
			String accountNo;
			 while(true) {
				accountNo = getInput("계좌번호 : ");
			 	if(accountNo == null) return;
			 	if(!Validator.isValidHyphenAccountNumber(accountNo)) {
			 		System.out.println("올바른 계좌번호 형식이 아닙니다."); continue;
			 	}
			 	if(ts.transactionCheckAccountNo(accountNo)) break;
			 	else System.out.println("소지하지 않은 계좌번호 입니다. 다시 시도해주세요.");
			 }
		 
		 
		 List<TransactionHistoryVO> TransactionList = ts.TransactionHistory(accountNo);
		 System.out.println("날짜\t구분\t금액\t상대방");
		 for(TransactionHistoryVO vo : TransactionList) {
			 System.out.println(vo.getTransactionDate() + "\t" + vo.getTransactionType() + "\t" + vo.getAmount() + "\t" + vo.getTargetAccount());
		 }
	 }
	 
	 public String getInput(String info) {
		    System.out.print(info);
		    String input = sc.next().trim();
		    return input.equals("0") ? null : input;
		}
}
