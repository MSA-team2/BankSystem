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
	 private final AccountService as = new AccountService(); 
	 
	 public void deposit(TransactionDTO dto) throws SQLException {
			td.deposit(dto);
			td.saveTransaction(dto);
		}

		public void withdraw(TransactionDTO dto) throws SQLException {
			td.withdraw(dto);
			td.saveTransaction(dto);
		}

		public List<TransactionHistoryVO> getTransactionHistory(String accountNo) {
			return td.getTransactionHistory(accountNo);
		}

		public List<AccountShowDTO> getMyAccounts() {
		    return ad.showMyAccounts();
		}
		

}
