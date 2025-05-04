package controller;


import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

import dto.AccountShowDTO;
import dto.TransactionDTO;
import model.AccountVO;
import model.TransactionHistoryVO;
import service.AccountService;
import service.TransactionService;

public class TransactionController {
	private final TransactionService ts = new TransactionService();
	private final AccountService as = new AccountService();
	private final Scanner sc = new Scanner(System.in);
	// 입금
	
	public void deposit() throws SQLException {
		 List<AccountShowDTO> list = ts.getMyAccounts();
		 
		 System.out.println("------입금 할 계좌 선택------");
		 System.out.println("번호\t계좌번호\t상품명\t잔액");
		 
		 for(AccountShowDTO dto : list) {
			 System.out.println(dto.getAccountNum() + "\t" + dto.getAccountNo() 
			 + "\t" + dto.getProductName() + "\t" + dto.getBalance());
		 }
		 
		 System.out.print("계좌번호를 입력해주세요 (0을 입력하면 취소): ");
		 String accountNo = sc.next().trim();
//		 sc.nextLine();
		 if(accountNo.equals("0")) {
			 System.out.println("취소되었습니다.");
			 return;
		 }
		 
		 BigDecimal amount;
		 while(true) {
				System.out.print("입금하실 금액을 입력해주세요 : ");
				String input = sc.next();
				amount = new BigDecimal(input);
				if (amount.compareTo(BigDecimal.ZERO) <= 0) {
			           System.out.println("다시 입력해주세요.");
			           continue;
				}
				break;
		 }
		 
			while (true) {
				System.out.print("계좌 비밀번호를 입력해주세요 : ");
				String pwd = sc.next();
				if (!as.verifyPassword(accountNo, pwd)) {
					continue;
				}
				else {
					break;
				}
			}
		
		 TransactionDTO dto = new TransactionDTO(accountNo, "DEPOSIT", amount, accountNo);
		 ts.deposit(dto);
		 System.out.println("입금 되셨습니다.");
	 }
	public void withdraw() throws SQLException {
		List<AccountShowDTO> list = ts.getMyAccounts();

		System.out.println("------출금 할 계좌 선택------");
		System.out.println("번호\t계좌번호\t상품명\t잔액");

		for (AccountShowDTO dto : list) {
			System.out.println(dto.getAccountNum() + "\t" + dto.getAccountNo() + "\t" + dto.getProductName() + "\t"
					+ dto.getBalance());
		}

		System.out.print("계좌번호를 입력해주세요 (0을 입력하면 취소): ");
		String accountNo = sc.next().trim();
		if (accountNo.equals("0")) {
			System.out.println("취소되었습니다.");
			return;
		}
		BigDecimal amount;
		 while(true) {
				System.out.print("출금하실 금액을 입력해주세요 : ");
				String input = sc.next();
				amount = new BigDecimal(input);
				if (amount.compareTo(BigDecimal.ZERO) <= 0) {
			           System.out.println("다시 입력해주세요.");
			           continue;
				}
				break;
		 }
		 
			while (true) {
				System.out.print("계좌 비밀번호를 입력해주세요 : ");
				String pwd = sc.next();
				if (!as.verifyPassword(accountNo, pwd)) {
					continue;
				}
				else {
					break;
				}
			}
		
		 TransactionDTO dto = new TransactionDTO(accountNo, "WITHDRAW", amount, "본인");
		 ts.withdraw(dto);
		 System.out.println("출금 되셨습니다. 잔액 : " );
	 }
		
	public void transfer() throws SQLException {
		List<AccountShowDTO> list = ts.getMyAccounts();

		System.out.println("------이체 할 계좌 선택------");
		System.out.println("번호\t계좌번호\t상품명\t잔액");

		for (AccountShowDTO dto : list) {
			System.out.println(dto.getAccountNum() + "\t" + dto.getAccountNo() + "\t" + dto.getProductName() + "\t"
					+ dto.getBalance());
		}

		System.out.print("계좌번호를 입력해주세요 (0을 입력하면 취소): ");
		String w_accountNo = sc.next().trim();
		if (w_accountNo.equals("0")) {
			System.out.println("취소되었습니다.");
			return;
			}
		
		System.out.print("상대방 계좌번호를 입력해주세요 (0을 입력하면 취소): ");
		String d_accountNo = sc.next().trim();
		if (d_accountNo.equals("0")) {
			System.out.println("취소되었습니다.");
			return;
			}
		
		BigDecimal amount;
		 while(true) {
				System.out.print("이체하실 금액을 입력해주세요 : ");
				String input = sc.next();
				amount = new BigDecimal(input);
				if (amount.compareTo(BigDecimal.ZERO) <= 0) {
			           System.out.println("다시 입력해주세요.");
			           continue;
				}
				break;
		 }
		 
		 while (true) {
				System.out.print("계좌 비밀번호를 입력해주세요 : ");
				String pwd = sc.next();
				if (!as.verifyPassword(w_accountNo, pwd)) {
					continue;
				}
				else {
					break;
				}
			}
		 TransactionDTO w_dto = new TransactionDTO(w_accountNo, "TRANSFER", amount, d_accountNo);
		 TransactionDTO d_dto = new TransactionDTO(d_accountNo, "TRANSFER", amount, w_accountNo);
		 ts.withdraw(w_dto);
		 ts.deposit(d_dto);
		 System.out.println("이체되었습니다.");
		}
		
		
	 public void transactionHistory () {
		 List<AccountShowDTO> accountList = ts.getMyAccounts();
		 System.out.println("------계좌 선택------");
		 System.out.println("번호\t계좌번호\t상품명\t잔액");

			for (AccountShowDTO dto : accountList) {
				System.out.println(dto.getAccountNum() + "\t" + dto.getAccountNo() + "\t" + dto.getProductName() + "\t"
						+ dto.getBalance());
			}
		 
		 System.out.print("계좌번호를 입력해주세요 (0을 입력하면 취소): ");
		 String accountNo = sc.next().trim();
		 List<TransactionHistoryVO> TransactionList = ts.getTransactionHistory(accountNo);
		 
		 System.out.println("날짜\t구분\t금액\t상대방");
		 for(TransactionHistoryVO vo : TransactionList) {
			 System.out.println(vo.getTransactionDate() + "\t" + vo.getTransactionType() + "\t" + vo.getAmount() + "\t" + vo.getTargetAccount());
		 }
	 }
	
}
