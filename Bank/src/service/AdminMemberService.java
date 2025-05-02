package service;

import java.util.List;

import dao.AccountDAO;
import dao.MemberDAO;
import model.AccountVO;
import model.MemberVO;

public class AdminMemberService {
	private final MemberDAO memberDAO = new MemberDAO();
    private final AccountDAO accountDAO = new AccountDAO();

    public String updateMemberInfo(String name, String jumin, String phone, String address) {
        MemberVO member = memberDAO.findMemberByNameAndJumin(name, jumin);
        if (member == null) return "[!] 해당 회원을 찾을 수 없습니다.";

        boolean updated = memberDAO.updateMemberInfo(member.getMemberNo(), phone, address) > 0;
        return updated ? "회원 정보가 수정되었습니다." : "회원 정보 수정에 실패했습니다.";
    }

    public List<AccountVO> getAccountsByMember(int memberNo) {
        return accountDAO.findAccountsByMemberNo(memberNo);
    }

    public MemberVO findMemberByNameAndJumin(String name, String jumin) {
        return memberDAO.findMemberByNameAndJumin(name, jumin);
    }

    public AccountVO getAccountInfo(String accountNo) {
        return accountDAO.findByAccountNo(accountNo);
    }
    
    public List<MemberVO> getAllMembers() {
        return memberDAO.findAllMembers();
    }
}
