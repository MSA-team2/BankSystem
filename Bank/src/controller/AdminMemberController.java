package controller;

import java.util.List;
import java.util.Scanner;

import model.MemberVO;
import service.AdminMemberService;
import util.ConsoleUtils;
import util.Validator;

public class AdminMemberController {
	
	private final Scanner sc = new Scanner(System.in);
	private final AdminMemberService adminMemberService = new AdminMemberService();
	
	public void editMember() {
        System.out.println("===== 회원 정보 수정 =====");
        System.out.print("회원 이름 : ");
        String inputName = sc.nextLine();

        System.out.print("주민번호 : ");
        String inputJumin = sc.nextLine();

        System.out.print("새 전화번호 : ");
        String newPhone = sc.nextLine();

        System.out.print("새 주소 : ");
        String newAddress = sc.nextLine();

        String result = adminMemberService.updateMemberInfo(inputName, inputJumin, newPhone, newAddress);
        System.out.println(result);
    }

    public void findMember() {
        System.out.println("===== 회원 검색 =====");
        System.out.print("회원 이름 : ");
        String name = sc.nextLine();

        System.out.print("주민번호 : ");
        String jumin = sc.nextLine();

        MemberVO member = adminMemberService.findMemberByNameAndJumin(name, jumin);
        if (member == null) {
            System.out.println("[!] 회원을 찾을 수 없습니다.");
            return;
        }

        System.out.println("===== 회원 정보 =====");
        System.out.println("이름 : " + member.getName());
        System.out.println("주민번호 : " + member.getJumin());
        System.out.println("전화번호 : " + member.getPhone());
        System.out.println("주소 : " + member.getAddress());
    }
    
    public void findAllMembers() {
		List<MemberVO> list = adminMemberService.getAllMembers();
		
		// 헤더
	    System.out.println("\n+------------------------------------------------------+");
	    System.out.println("|             👤 전체 회원 조회 👤                    |");
	    System.out.println("+------------------------------------------------------+");
	    
	    // 열 너비 정의 및 구분선
	    System.out.println("+----------+-------------------+---------------+-------------------------------------+--------------+----------+----------+");
	    System.out.println("| 이름      | 주민번호             | 비밀번호        | 주소                            	     | 전화번호       | 계정상태    | 역할      |");
	    System.out.println("+----------+-------------------+---------------+-------------------------------------+--------------+----------+----------+");
	    
	    // 데이터 출력
	    for (MemberVO dto : list) {
	        // 주소가 너무 길 경우 줄이기
	        String address = dto.getAddress();
	        if (address.length() > 30) {
	            address = address.substring(0, 27) + "...";
	        }
	        
	        // 계정 상태 표시
	        String status = dto.getStatus() == 'Y' ? "🔓 정상" : "🔒 잠금";
	        
	        // 역할 표시
	        String role = dto.getRole() == 1 ? "👑 Admin" : "👤 User";
	        
	        System.out.println("| " + ConsoleUtils.padRight(dto.getName(), 8) + 
	                " | " + ConsoleUtils.padRight(dto.getJumin(), 17) + 
	                " | " + ConsoleUtils.padRight(dto.getPassword(), 13) + 
	                " | " + ConsoleUtils.padRight(address, 30) + 
	                " | " + ConsoleUtils.padRight(dto.getPhone(), 14) + 
	                " | " + ConsoleUtils.padRight(status, 8) + 
	                " | " + ConsoleUtils.padRight(role, 8) + " |");
	    }
	    
	    // 푸터 라인
	    System.out.println("+----------+-------------------+---------------+--------------------------------+----------------+----------+----------+");
	    
	    // 통계 정보
	    int totalMembers = list.size();
	    int adminCount = 0;
	    int lockedCount = 0;
	    
	    for (MemberVO dto : list) {
	        if (dto.getRole() == 1) {
	            adminCount++;
	        }
	        if (dto.getStatus() == 'N') {
	            lockedCount++;
	        }
	    }
	    
	    System.out.println("👥 총 회원 수: " + totalMembers + "명");
	    System.out.println("👑 관리자 수: " + adminCount + "명");
	    System.out.println("👤 일반 회원 수: " + (totalMembers - adminCount) + "명");
	    System.out.println("🔒 계좌 잠금 회원 수: " + lockedCount + "명");
	    System.out.println("🕒 조회 일시: " + java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
	}
}
