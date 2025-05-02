package service;

import java.time.LocalDate;
import java.util.List;

import dao.AccountDAO;
import dao.MemberDAO;
import dao.TransactionDAO;
import dto.AccountSummaryDto;
import dto.DailyTransferSummaryDto;
import model.MemberVO;

public class AdminService {
	
	private final AccountDAO accountDAO = new AccountDAO();
	private final MemberDAO memberDAO = new MemberDAO();
    private final TransactionDAO transactionDAO = new TransactionDAO();
	
	public DailyTransferSummaryDto getDailyTransferSummary(LocalDate date) {
        return transactionDAO.getDailySummary(date);
    }
	
	public List<MemberVO> getAllMembers() {
        return memberDAO.findAllMembers();
    }
	
	public List<AccountSummaryDto> getAllAccounts() {
        return accountDAO.findAllAccount();
    }
}
