package service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dao.AccountDAO;
import dao.MemberDAO;
import dao.TransactionDAO;
import dto.AccountSummaryDto;
import dto.DailyTransferSummaryDto;
import model.AccountVO;
import model.MemberVO;
import util.Validator;

public class AdminService {
    
    private final AccountDAO accountDAO = new AccountDAO();
    private final MemberDAO memberDAO = new MemberDAO();
    private final TransactionDAO transactionDAO = new TransactionDAO();
    
    /**
     * 일일 거래 내역 요약 조회
     */
    public DailyTransferSummaryDto getDailyTransferSummary(LocalDate date) {
        return transactionDAO.getDailySummary(date);
    }
    
    /**
     * 날짜 형식 검증
     */
    public String validateDateInput(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return "날짜를 입력해야 합니다.";
        }
        
        if (!Validator.isValidDate(dateStr)) {
            return "올바른 날짜 형식이 아닙니다.";
        }
        
        return null; // 검증 성공
    }
    
    /**
     * 거래량 많은지 판단
     */
    public boolean isHighTransactionVolume(int totalCount) {
        return totalCount > 10; // 10건 초과면 거래량 많음
    }
    
    /**
     * 전체 계좌 목록 조회
     */
    public List<AccountSummaryDto> getAllAccounts() {
        return accountDAO.findAllAccount();
    }
    
    /**
     * 계좌 통계 정보 계산
     */
    public Map<String, Object> calculateAccountStats(List<AccountSummaryDto> accounts) {
        Map<String, Object> stats = new HashMap<>();
        
        int vipCount = 0;
        BigDecimal totalBalance = BigDecimal.ZERO;
        BigDecimal vipThreshold = new BigDecimal("10000000"); // 1천만원 이상 VIP
        
        for (AccountSummaryDto dto : accounts) {
            if (dto.getBalance().compareTo(vipThreshold) >= 0 && dto.getStatus() == 'Y') {
                vipCount++;
            }
            if (dto.getStatus() == 'Y') {
                totalBalance = totalBalance.add(dto.getBalance());
            }
        }
        
        stats.put("total", accounts.size());
        stats.put("vipCount", vipCount);
        stats.put("totalBalance", totalBalance);
        
        return stats;
    }
    
    /**
     * 계좌 상태 변경 입력값 검증
     */
    public String validateAccountLockInput(String name, String accountNo, String statusChoice) {
        if (name == null || name.trim().isEmpty()) {
            return "회원 이름은 필수 입력 사항입니다.";
        }
        
        if (accountNo == null || accountNo.trim().isEmpty()) {
            return "계좌번호는 필수 입력 사항입니다.";
        }
        
        if (!Validator.isValidHyphenAccountNumber(accountNo)) {
            return "잘못된 계좌번호 형식입니다. 예) 100-1234-5678";
        }
        
        if (!isValidStatusChoice(statusChoice)) {
            return "잘못된 선택입니다. 작업을 취소합니다.";
        }
        
        return null; // 검증 성공
    }
    
    /**
     * 상태 선택값 검증
     */
    private boolean isValidStatusChoice(String choice) {
        return choice != null && (choice.equals("1") || choice.equals("2") || 
                choice.equalsIgnoreCase("Y") || choice.equalsIgnoreCase("N"));
    }
    
    /**
     * 상태 선택값을 상태 코드로 변환
     */
    public char getStatusFromChoice(String choice) {
        switch(choice) {
            case "1": 
            case "Y":
            case "y":
                return 'Y';
            case "2":
            case "N":
            case "n":
                return 'N';
            default:
                return ' '; // 잘못된 입력
        }
    }
    
    /**
     * 계좌 상태 변경 메서드
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
    
    /**
     * 계좌 상태 텍스트 반환
     */
    public String getAccountStatusText(char status) {
        return status == 'Y' ? "✅ Y" : "❌ N";
    }
}