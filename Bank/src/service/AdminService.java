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
	
	/**
	 * 계좌 상태 변경 메서드
	 * @param name 회원 이름
	 * @param accountNo 계좌번호
	 * @param newStatus 새 상태 (Y/N)
	 * @return 처리 결과 메시지
	 */
	public String changeAccountStatus(String name, String accountNo, char newStatus) {
	    // 계좌 조회
	    AccountVO account = accountDAO.findByAccountNo(accountNo);
	    if (account == null) return "[!] 계좌를 찾을 수 없습니다.";
	    
	    // 계좌 소유자 확인
	    MemberVO member = memberDAO.findMemberByNo(account.getMemberNo());
	    if (member == null) return "[!] 계좌 소유자 정보를 찾을 수 없습니다.";
	    
	    // 이름 일치 확인
	    if (!member.getName().equals(name)) {
	        return "[!] 입력한 이름과 계좌 소유자 이름이 일치하지 않습니다.";
	    }
	    
	    // 상태 변경 필요 여부 확인
	    if (account.getStatus() == newStatus) {
	        return "[!] 계좌 상태가 이미 " + (newStatus == 'Y' ? "정상" : "잠금") + " 상태입니다.";
	    }
	    
	    // 상태 업데이트
	    boolean updated = accountDAO.updateStatus(accountNo, newStatus) > 0;
	    
	    return updated ? 
	        "계좌 상태가 " + (newStatus == 'Y' ? "정상" : "잠금") + "으로 변경되었습니다." : 
	        "[!] 계좌 상태 변경에 실패했습니다.";
	}
}
