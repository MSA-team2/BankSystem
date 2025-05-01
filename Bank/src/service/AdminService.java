package service;

import java.time.LocalDate;
import java.util.List;

import dao.AccountDAO;
import dao.TransactionDAO;
import dto.AccountSummaryDto;
import dto.AdminMemberDto;
import dto.DailyTransferSummaryDto;

public class AdminService {
	
	private final AccountDAO accountDAO = new AccountDAO();
	private final MemberDAO memberDAO = new MemberDAO();
    private final TransactionDAO transactionDAO = new TransactionDAO();
	
	public DailyTransferSummaryDto getDailyTransferSummary(LocalDate date) {
        return transactionDAO.getDailySummary(date);
    }
	
	public List<AdminMemberDto> getAllMembers() {
        return memberDAO.findAll();
    }
	
	public List<AccountSummaryDto> getAllAccounts() {
        return accountDAO.findAllAccount();
    }
}
