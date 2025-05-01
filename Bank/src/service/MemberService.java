package service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

import controller.SessionManager;
import controller.UserController;
import dao.MemberDAO;
import dbConn.util.CloseHelper;
import dbConn.util.ConnectionHelper;
import dbConn.util.ConnectionSingletonHelper;
import model.MemberVO;

public class MemberService {
	private final MemberDAO md = new MemberDAO();
	private final Scanner sc = new Scanner(System.in);
	
	public Scanner getScanner() {
        return sc;
    }
	
	// 1. 회원가입
	public int insertMember(MemberVO mv) {
		Connection conn = ConnectionSingletonHelper.getConnection("mysql");
		int result = new MemberDAO().insertMember(conn, mv);
		
        if (result > 0) {
//        	commit(conn);
        } else {
//        	rollback(conn);
        }
        
        CloseHelper.close(conn);
        return result;
		
//        MemberVO mv = new MemberVO();
//        mv.setName(name);
//        mv.setJumin(jumin);
//        mv.setMemberId(id);
//        mv.setPassword(pwd);
//        mv.setAddress(address);
//        mv.setPhone(phone);
        
//        int result = md.insertMember(mv);        
//        if (result > 0) {
//        	System.out.println("회원가입이 완료되었습니다. 로그인 페이지로 이동합니다.");
//		    loginMember();
//        } else {
//        	System.out.println("회원가입에 실패했습니다. 다시 시도해주세요.");
//        }
	}

	// 2. 로그인
	public MemberVO loginMember() {
		Connection conn = ConnectionSingletonHelper.getConnection("mysql");
		MemberVO mv = new MemberDAO().selectByMemberIdPwd(conn, id); // id 받아와..?
		
		CloseHelper.close(conn);
		return mv;
		
		
//	        try {
//	            
//
//	            if (rs.next()) {
//
//	            	MemberVO member = new MemberVO();
//	                
//	            	member.setMemberNo(rs.getInt("member_no"));
//	            	member.setName(rs.getString("name"));
//	            	member.setJumin(rs.getString("jumin"));
//	            	member.setMemberId(rs.getString("member_id"));
//	            	member.setPassword(rs.getString("password"));
//	            	member.setAddress(rs.getString("address"));
//	            	member.setPhone(rs.getString("phone"));
//	            	member.setStatus(rs.getString("status").charAt(0));
//	            	member.setRole(rs.getInt("lock_cnt"));
//	            	member.setRole(rs.getInt("role"));
//	            	
//	            	SessionManager.login(member);
//	            	
//	                System.out.println("[" + member.getName() + "] 님, 환영합니다!");	                
//	             
//	                if (SessionManager.isAdmin()) { // 관리자
////		                	 AdminController.menu();	          
//	                } else { // 일반 회원 
//	                	UserController.MemberMenu();	                    
//	                }
//	                return; // 로그인 성공 -> 메서드 종료	                
//	            } else {
//	                System.out.println("아이디 또는 비밀번호가 올바르지 않습니다.");
//	                System.out.print("다시 시도하시겠습니까? (Y/N): ");
//	                
//	                String retry = sc.nextLine().trim().toUpperCase();
//	                if (!retry.equals("Y")) {
//	                    break;
//	                }
//	            }
//	        } catch (SQLException e) {
//	            e.printStackTrace();
//	        } catch (NumberFormatException e) {
//				e.printStackTrace();
//			}
	}

	public int updatePwd() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	
	
	// 주민번호 유효성 체크
	// 비밀번호 유효성 체크
	// 비밀번호 일치 확인
	// 전화번호 유효성 체크
	
	
	
}
