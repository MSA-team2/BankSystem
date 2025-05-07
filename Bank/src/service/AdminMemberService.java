package service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dao.AccountDAO;
import dao.MemberDAO;
import model.AccountVO;
import model.MemberVO;
import util.Validator;

public class AdminMemberService {
    private final MemberDAO memberDAO = new MemberDAO();
    private final AccountDAO accountDAO = new AccountDAO();
    
    // 잠금 계정 관리 관련 메서드
    public boolean unlockMember(int memberNo) {
        return memberDAO.updateAccountStatus(memberNo, 'Y') > 0;
    }
    
    public List<MemberVO> getLockedAccounts() {
        return memberDAO.getLockedAccounts();
    }
    
    // 회원 정보 관리 관련 메서드
    public String updateMemberInfo(String name, String jumin, String phone, String address) {
        // 입력값 검증
        if (name == null || name.trim().isEmpty() || jumin == null || jumin.trim().isEmpty()) {
            return "[!] 이름과 주민번호는 필수 입력 사항입니다.";
        }
        
        // 주민번호 형식 검증
        if (!Validator.isValidHyphenJumin(jumin)) {
            return "[!] 주민번호 형식이 올바르지 않습니다.";
        }
        
        MemberVO member = memberDAO.findMemberByNameAndJumin(name, jumin);
        if (member == null) {
            return "[!] 해당 회원을 찾을 수 없습니다.";
        }
        
        boolean updated = memberDAO.updateMemberInfo(member.getMemberNo(), phone, address) > 0;
        return updated ? "회원 정보가 성공적으로 수정되었습니다." : "회원 정보 수정에 실패했습니다.";
    }
    
    public MemberVO findMemberByNameAndJumin(String name, String jumin) {
        return memberDAO.findMemberByNameAndJumin(name, jumin);
    }
    
    public List<MemberVO> getAllMembers() {
        return memberDAO.findAllMembers();
    }
    
    // 계좌 관련 메서드
    public List<AccountVO> getAccountsByMember(int memberNo) {
        return accountDAO.findAccountsByMemberNo(memberNo);
    }
    
    public AccountVO getAccountInfo(String accountNo) {
        return accountDAO.findByAccountNo(accountNo);
    }
    
    // 유틸리티 메서드
    public Map<String, Integer> calculateMemberStats(List<MemberVO> members) {
        Map<String, Integer> stats = new HashMap<>();
        
        int totalMembers = members.size();
        int adminCount = 0;
        int lockedCount = 0;
        
        for (MemberVO member : members) {
            if (member.getRole() == 1) {
                adminCount++;
            }
            if (member.getStatus() == 'N') {
                lockedCount++;
            }
        }
        
        stats.put("total", totalMembers);
        stats.put("admin", adminCount);
        stats.put("normal", totalMembers - adminCount);
        stats.put("locked", lockedCount);
        
        return stats;
    }
    
    public String getMemberStatusText(char status) {
        return status == 'Y' ? "🔓 정상" : "🔒 잠금";
    }
    
    public String getMemberRoleText(int role) {
        return role == 1 ? "👑 관리자" : "👤 일반 회원";
    }
}