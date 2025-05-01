package controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import dbConn.util.CloseHelper;
import dbConn.util.ConnectionSingletonHelper;
import model.MemberVO;

public class MemberController {
	static Scanner sc = new Scanner(System.in);
	static Connection conn = null;
	static PreparedStatement ps = null;
	static ResultSet rs = null;
	
	// connect
	public static void connect() {
		try {
			conn = ConnectionSingletonHelper.getConnection("mysql");
			conn.setAutoCommit(true); // 자동커밋 켬
			
		} catch (Exception e) {
			e.printStackTrace();
		} 
	} 
	// close
	public static void close() {
		try {
			CloseHelper.close(rs);
			CloseHelper.close(ps);
			CloseHelper.close(conn);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	// 첫화면
	public static void MainMenu() {
		MemberVO vo = new MemberVO();
		
		while(true) {
			System.out.println("\n======== 투게더 은행 ========");
			System.out.println("\t1. 로그인");
			System.out.println("\t2. 회원가입");
			System.out.println("\t0. 종료");
			System.out.println("===========================");
			System.out.print("메뉴 선택: ");
			int menu = sc.nextInt();
			sc.nextLine();
			
			switch (menu) {
			case 1: loginMember(); break;
			case 2: joinMember(); break;
			case 0: System.out.println("감사합니다. 안녕히가세요"); System.exit(0); break;
			default: System.out.println("잘못 입력하셨습니다. 다시 입력해주세요");
			}
		}
	}
	
	// 로그인
	public static void loginMember() {
		
	    while (true) {
	        System.out.println("\n========== 로그인 ==========");
	        System.out.print("아이디: ");
	        String id = sc.nextLine();

	        System.out.print("비밀번호: ");
	        String pwd = sc.nextLine();

	        String sql = "SELECT * FROM MEMBER WHERE member_id = ? AND password = ?";

	        try {
	            ps = conn.prepareStatement(sql);
	            ps.setString(1, id);
	            ps.setString(2, pwd);
	            rs = ps.executeQuery();

	            if (rs.next()) {
	            	MemberVO member = new MemberVO();
	                
	            	member.setMemberNo(rs.getInt("member_no"));
	            	member.setName(rs.getString("name"));
	            	member.setJumin(rs.getString("jumin"));
	            	member.setMemberId(rs.getString("member_id"));
	            	member.setPassword(rs.getString("password"));
	            	member.setAddress(rs.getString("address"));
	            	member.setPhone(rs.getString("phone"));
	            	member.setRockYn(rs.getString("lock_yn").charAt(0));
	            	member.setRole(rs.getInt("role"));
	            	
	            	SessionManager.login(member);
	            	
	                System.out.println("[" + member.getName() + "] 님, 환영합니다!");	                
	             
	                if (SessionManager.isAdmin()) { // 관리자
	                	// AdminController.AdminMenu();	          
	                } else { // 일반 회원 
	                	// 사용자 메뉴
	                	UserController.MemberMenu();	                    
	                }
	                return; // 로그인 성공 -> 메서드 종료	                
	            } else {
	            	// 추가사항: 아이디/비밀번호 찾기
	                System.out.println("아이디 또는 비밀번호가 올바르지 않습니다.");
	                System.out.print("다시 시도하시겠습니까? (Y/N): ");
	                
	                String retry = sc.nextLine().trim().toUpperCase();
	                if (!retry.equals("Y")) {
	                    break;
	                }
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    } // while
	}
	
	// 회원가입
	public static void joinMember() {
		// 입력시 중간에 나갈수 있는
		System.out.println("\n======= 회원가입 =======");
		System.out.println("※ 입력 중 '0'을 입력하면 메인메뉴로 돌아갑니다.");
		String sql = "INSERT INTO MEMBER (name, jumin, member_id, password, address, phone)"
						+ "VALUES(?, ?, ?, ?, ?, ?)";
		try {
			ps = conn.prepareStatement(sql);
			
			String name = getInput("이름: ");
	        if (name == null) return;

	        String jumin = getInput("주민번호(-): ");
	        if (jumin == null) return;
	        
	        // 아이디 중복체크
	        String memberId = getInput("아이디: ");
	        if (memberId == null) return;

	        String password = getInput("비밀번호: ");
	        if (password == null) return;

	        String address = getInput("주소: ");
	        if (address == null) return;

	        String phone = getInput("전화번호: ");
	        if (phone == null) return;
			
			ps.setString(1, name);
			ps.setString(2, jumin);
			ps.setString(3, memberId);
			ps.setString(4, password);
			ps.setString(5, address);
			ps.setString(6, phone);
			
			int result = ps.executeUpdate();

			if (result == 1) {
			    System.out.println("회원가입이 완료되었습니다. 로그인 페이지로 이동합니다.");
			    loginMember();
			} else {
			    System.out.println("회원가입에 실패했습니다. 다시 시도해주세요.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static String getInput(String info) {
	    System.out.print(info);
	    String input = sc.nextLine().trim();
	    if (input.equals("0")) return null;
	    return input;
	}

	

}