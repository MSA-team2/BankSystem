package service;


import java.sql.SQLException;
import java.util.List;

import controller.SessionManager;
import dao.AccountDAO;
import dao.TransactionDAO;
import dto.AccountShowDTO;
import dto.TransactionDTO;
import model.MemberVO;
import model.TransactionHistoryVO;


public class TransactionService {
	
	 private final AccountDAO ad = new AccountDAO();
	 private final TransactionDAO td = new TransactionDAO();
	 	
	 	// 입금
	 	public void deposit(TransactionDTO dto) throws SQLException {
			td.transfer(dto);
			td.saveTransaction(dto);
		}
	 	
	 	// 출금
		public void withdraw(TransactionDTO dto) throws SQLException {
			td.transfer(dto);
			td.saveTransaction(dto);
		}
		
		// 모든 거래내역 가져오기
		public List<TransactionHistoryVO> getTransactionHistory(String accountNo) {
			return td.getTransactionHistory(accountNo);
		}
		
		// 내 계좌 보여주기
		public List<AccountShowDTO> getMyAccounts(List<String> productTypes) {
		    return ad.showMyAccounts(productTypes);
		}
		
		// 입, 출금 시 내 계좌 확인 
		public boolean checkAccountNo(String accountNo) {
			return td.checkAccountNo(accountNo);
		}
		
		// 이체 시 상대방 계좌 확인
		public boolean targetCheckAccountNo(String accountNo) {
			return td.targetCheckAccountNo(accountNo);
		}
		
		// 거래 내역 계좌 확인
		public boolean transactionCheckAccountNo(String accountNo) {
			return td.transactionCheckAccountNo(accountNo);
		}
}
