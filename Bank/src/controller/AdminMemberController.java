package controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import model.domain.Member;
import service.AdminMemberService;
import util.ConsoleUtils;
import util.Validator;

public class AdminMemberController {
    
    private final Scanner sc = new Scanner(System.in);
    private final AdminMemberService adminMemberService = new AdminMemberService();
    
    /**
     * 잠금 회원 수정 메서드
     */
    public void manageLockedAccounts() {
        System.out.println("\n============ [잠금 계정 관리] ============");
        
        if (!SessionManager.isAdmin()) {
            System.out.println("[!] 관리자만 접근할 수 있는 기능입니다.");
            return;
        }
        
        // 잠금 계정 목록 조회
        List<Member> lockedAccounts = adminMemberService.getLockedAccounts();
        
        if (lockedAccounts.isEmpty()) {
            System.out.println("\n🔍 현재 잠금 상태인 계정이 없습니다.");
            return;
        }
        
        // 잠금 계정 목록 출력
        displayLockedAccounts(lockedAccounts);
        
        // 잠금 해제할 계정 선택
        System.out.print("\n🔓 잠금 해제할 회원 번호를 입력하세요 (0: 취소): ");
        int memberNo;
        try {
            memberNo = Integer.parseInt(sc.nextLine());
            if (memberNo == 0) {
                System.out.println("계정 잠금 해제를 취소합니다.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ 유효하지 않은 회원 번호입니다.");
            return;
        }
        
        // 선택한 계정 잠금 해제
        boolean result = adminMemberService.unlockMember(memberNo);
        if (result) {
            System.out.println("\n✅ 계정 잠금이 성공적으로 해제되었습니다.");
            
            // 업데이트 후 남아있는 잠금 계정 확인
            List<Member> remainingLockedAccounts = adminMemberService.getLockedAccounts();
            if (remainingLockedAccounts.isEmpty()) {
                System.out.println("🎉 더 이상 잠금 상태인 계정이 없습니다!");
            } else {
                System.out.println("\n📋 남아있는 잠금 계정 목록입니다:");
                displayLockedAccounts(remainingLockedAccounts);
            }
        } else {
            System.out.println("\n❌ 계정 잠금 해제에 실패했습니다. 회원 번호를 확인하세요.");
        }
    }
    
    /**
     * 회원 정보 수정 메서드
     */
    public void editMember() {
        System.out.println("\n============ [회원 정보 수정] ============");
        
        if (!SessionManager.isAdmin()) {
            System.out.println("[!] 관리자만 접근할 수 있는 기능입니다.");
            return;
        }
        
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
                System.out.println("📆 수정 일시: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            }
        } else {
            System.out.println("\n🔄 회원 정보 수정이 취소되었습니다.");
        }
    }
    
    /**
     * 회원 검색 메서드
     */
    public void findMember() {    
        System.out.println("\n============ [회원 정보 검색] ============");
        
        if (!SessionManager.isAdmin()) {
            System.out.println("[!] 관리자만 접근할 수 있는 기능입니다.");
            return;
        }
        
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
        Member member = adminMemberService.findMemberByNameAndJumin(name, jumin);
        if (member == null) {
            System.out.println("\n❌ 회원을 찾을 수 없습니다!");
            System.out.println("💡 이름과 주민번호가 정확한지 확인하세요.");
            return;
        }
        
        // 계정 상태 표시
        String statusText = adminMemberService.getMemberStatusText(member.getStatus());
        String roleText = adminMemberService.getMemberRoleText(member.getRole());
        
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
     * 전체 회원 조회 메서드
     */
    public void findAllMembers() {
        System.out.println("\n============ [전체 회원 조회] ============");
        
        if (!SessionManager.isAdmin()) {
            System.out.println("[!] 관리자만 접근할 수 있는 기능입니다.");
            return;
        }
        
        List<Member> list = adminMemberService.getAllMembers();
        Map<String, Integer> stats = adminMemberService.calculateMemberStats(list);

        // 열 너비 정의 및 구분선
        System.out.println("+----------+------+-------------------+---------------+-------------------------------------+--------------+----------+----------+");
        System.out.println("| 회원번호 | 이름 | 주민번호          | 아이디        | 주소                                | 전화번호     | 계정상태 | 역할     |");
        System.out.println("+----------+------+-------------------+---------------+-------------------------------------+--------------+----------+----------+");

        // 데이터 출력
        for (Member dto : list) {
            // 주소가 너무 길 경우 줄이기
            String address = dto.getAddress();
            if (address.length() > 30) {
                address = address.substring(0, 27) + "...";
            }

            // 계정 상태 표시
            String status = adminMemberService.getMemberStatusText(dto.getStatus());
            // 역할 표시 (짧은 버전)
            String role = dto.getRole() == 1 ? "👑 Admin" : "👤 User";

            System.out.println("| " + ConsoleUtils.padRight(String.valueOf(dto.getMemberNo()), 8) +
                     " | " + ConsoleUtils.padRight(dto.getName(), 4) +
                     " | " + ConsoleUtils.padRight(dto.getJumin(), 17) +
                     " | " + ConsoleUtils.padRight(dto.getMemberId(), 13) +
                     " | " + ConsoleUtils.padRight(address, 35) +
                     " | " + ConsoleUtils.padRight(dto.getPhone(), 12) +
                     " | " + ConsoleUtils.padRight(status, 8) +
                     " | " + ConsoleUtils.padRight(role, 8) + " |");
        }

        // 푸터 라인
        System.out.println("+----------+------+-------------------+---------------+-------------------------------------+--------------+----------+----------+");

        // 통계 정보
        System.out.println("👥 총 회원 수: " + stats.get("total") + "명");
        System.out.println("👑 관리자 수: " + stats.get("admin") + "명");
        System.out.println("👤 일반 회원 수: " + stats.get("normal") + "명");
        System.out.println("🔒 계좌 잠금 회원 수: " + stats.get("locked") + "명");
        System.out.println("🕒 조회 일시: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }
    
    // 헬퍼 메서드
    private void displayLockedAccounts(List<Member> accounts) {
        System.out.println("\n+----------------------------------------------------------+");
        System.out.println("|                  🔒 잠금 계정 목록 🔒                   |");
        System.out.println("+----------------------------------------------------------+");
        System.out.println("| 회원번호 | 이름      | 주민번호          | 전화번호        | 잠금횟수 |");
        System.out.println("+----------+-----------+------------------+----------------+----------+");
        
        for (Member member : accounts) {
            System.out.printf("| %-8d | %-9s | %-16s | %-14s | %-8d |\n", 
                    member.getMemberNo(),
                    member.getName(),
                    member.getJumin(),
                    member.getPhone(),
                    member.getLockCnt());
        }
        
        System.out.println("+----------+-----------+------------------+----------------+----------+");
        System.out.println("📊 총 " + accounts.size() + "개의 잠금 계정이 있습니다.");
    }
}