package controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

import model.MemberVO;
import service.AdminMemberService;
import util.ConsoleUtils;
import util.Validator;

public class AdminMemberController {
	
	private final Scanner sc = new Scanner(System.in);
	private final AdminMemberService adminMemberService = new AdminMemberService();
	
	/**
	 * 회원 정보 수정 메서드
	 * 회원의 이름과 주민번호로 조회 후 전화번호, 주소 정보를 업데이트합니다.
	 */
	public void editMember() {
		// 헤더
	    System.out.println("\n+--------------------------------------------+");
	    System.out.println("|          👤 회원 정보 수정 👤             |");
	    System.out.println("+--------------------------------------------+");
	    
	    // 회원 조회 정보 입력
	    System.out.println("\n📝 회원 정보를 수정하려면 다음 정보를 입력하세요:");
	    
	    System.out.print("🔍 회원 이름: ");
	    String inputName = sc.nextLine();
	    
	    System.out.print("🔍 주민번호: ");
	    String inputJumin = sc.nextLine();
	    
	    // 입력 형식 검증
	    if (inputName.trim().isEmpty() || inputJumin.trim().isEmpty()) {
	        System.out.println("❌ 이름과 주민번호는 필수 입력 사항입니다.");
	        return;
	    }
	    
	    // 주민번호 형식 검증
	    if (!Validator.isValidHyphenJumin(inputJumin)) {
	        System.out.println("⚠️ 주민번호 형식이 올바르지 않습니다.");
	        return;
	    }
	    
	    // 구분선
	    System.out.println("\n+--------------------------------------------+");
	    System.out.println("|          📝 새로운 정보 입력 📝           |");
	    System.out.println("+--------------------------------------------+");
	    
	    // 새 정보 입력
	    System.out.print("📱 새 전화번호: ");
	    String newPhone = sc.nextLine();
	    
	    System.out.print("🏠 새 주소: ");
	    String newAddress = sc.nextLine();
	    
	    // 입력 정보 확인
	    System.out.println("\n+--------------------------------------------+");
	    System.out.println("|          ✅ 수정 정보 확인 ✅             |");
	    System.out.println("+--------------------------------------------+");
	    System.out.println("│ 회원 이름   : " + inputName);
	    System.out.println("│ 주민번호    : " + inputJumin);
	    System.out.println("│ 새 전화번호 : " + newPhone);
	    System.out.println("│ 새 주소     : " + newAddress);
	    System.out.println("+--------------------------------------------+");
	    
	    // 확인 요청
	    System.out.print("\n✅ 위 정보로 회원 정보를 수정하시겠습니까? (Y/N): ");
	    String confirm = sc.nextLine();
	    
	    if (confirm.equalsIgnoreCase("Y")) {
	        // 회원 정보 업데이트 요청
	        String result = adminMemberService.updateMemberInfo(inputName, inputJumin, newPhone, newAddress);
	        
	     // 결과 처리
	        if (result.contains("찾을 수 없습니다")) {
	            System.out.println("\n❌ " + result);
	            System.out.println("💡 이름과 주민번호가 정확한지 확인하세요.");
	        } else if (result.contains("실패")) {
	            System.out.println("\n❌ " + result);
	            System.out.println("💡 시스템 관리자에게 문의하세요.");
	        } else {
	            System.out.println("\n🎉 " + result);
	            System.out.println("📆 수정 일시: " + java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
	        }
	    } else {
	        System.out.println("\n🔄 회원 정보 수정이 취소되었습니다.");
	    }
    }
	
	/**
	 * 회원 검색 메서드
	 * 이름과 주민번호로 회원을 검색하고 상세 정보를 표시합니다.
	 */
    public void findMember() {	
    	// 헤더
        System.out.println("\n+--------------------------------------------+");
        System.out.println("|          🔍 회원 정보 검색 🔍             |");
        System.out.println("+--------------------------------------------+");
        
        // 검색 정보 입력
        System.out.println("\n📋 회원을 검색하려면 다음 정보를 입력하세요:");
        
        System.out.print("👤 회원 이름: ");
        String name = sc.nextLine();
        
        System.out.print("🪪 주민번호: ");
        String jumin = sc.nextLine();
        
        // 입력 형식 검증
        if (name.trim().isEmpty() || jumin.trim().isEmpty()) {
            System.out.println("❌ 이름과 주민번호는 필수 입력 사항입니다.");
            return;
        }
        
        // 검색 중임을 표시
        System.out.println("\n🔄 회원 정보를 검색 중입니다...");
        
        // 회원 정보 조회
        MemberVO member = adminMemberService.findMemberByNameAndJumin(name, jumin);
        if (member == null) {
            System.out.println("\n❌ 회원을 찾을 수 없습니다!");
            System.out.println("💡 이름과 주민번호가 정확한지 확인하세요.");
            return;
        }
        
        // 계정 상태 표시
        String statusText = member.getStatus() == 'Y' ? "🔓 정상" : "🔒 잠금";
        
        // 역할 표시
        String roleText = member.getRole() == 1 ? "👑 관리자" : "👤 일반 회원";
        
        // 회원 정보 표시
        System.out.println("\n+--------------------------------------------+");
        System.out.println("|          🎯 회원 상세 정보 🎯             |");
        System.out.println("+--------------------------------------------+");
        System.out.println("│ 이름         : " + member.getName());
        System.out.println("│ 주민번호     : " + member.getJumin());
        System.out.println("│ 아이디       : " + member.getMemberId());
        System.out.println("│ 전화번호     : " + member.getPhone());
        System.out.println("│ 주소         : " + member.getAddress());
        System.out.println("│ 계정 상태    : " + statusText);
        System.out.println("│ 회원 권한    : " + roleText);
        System.out.println("+--------------------------------------------+");
        
        // 조회 시간 표시
        System.out.println("\n📆 조회 일시: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }
    
    /**
	 * 회원 조회 메서드
	 * 전체 회원을 조회하고 상세 정보를 표시합니다.
	 */
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
	    System.out.println("🕒 조회 일시: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
	}
}
