package service;

import java.time.LocalDate;
import java.util.List;

import dao.AccountDAO;
import dao.MemberDAO;
import dao.TransactionDAO;
import dto.AccountSummaryDto;
import dto.DailyTransferSummaryDto;
import model.AccountVO;
import model.MemberVO;

public class AdminService {
	
	private final AccountDAO accountDAO = new AccountDAO();
	private final MemberDAO memberDAO = new MemberDAO();
    private final TransactionDAO transactionDAO = new TransactionDAO();
	
	public DailyTransferSummaryDto getDailyTransferSummary(LocalDate date) {
        return transactionDAO.getDailySummary(date);
    }
	
	public List<AccountSummaryDto> getAllAccounts() {
        return accountDAO.findAllAccount();
    }
	
	public String changeAccountStatus(String name, String jumin, String accountNo, char newStatus) {
        MemberVO member = memberDAO.findMemberByNameAndJumin(name, jumin);
        if (member == null) return "[!] 회원을 찾을 수 없습니다.";

        AccountVO account = accountDAO.findByAccountNo(accountNo);
        if (account == null) return "[!] 계좌를 찾을 수 없습니다.";

        if (account.getMemberNo() != member.getMemberNo()) {
            return "[!] 계좌가 해당 회원의 것이 아닙니다.";
        }

        if (account.getStatus() == newStatus) {
            return "[!] 계좌 상태가 이미 " + newStatus + "입니다.";
        }

        boolean updated = accountDAO.updateStatus(accountNo, newStatus) > 0;
        return updated ? "계좌 상태가 변경되었습니다." : "[!] 계좌 상태 변경 실패.";
    }
}
