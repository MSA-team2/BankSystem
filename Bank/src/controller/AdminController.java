package controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import dto.AccountSummaryDto;
import dto.DailyTransferSummaryDto;
import service.AdminService;
import util.ConsoleUtils;

public class AdminController {

    private final AdminService adminService = new AdminService();
    private final Scanner sc = new Scanner(System.in);

    /**
     * 일일 거래량 조회 메서드
     */
    public void showDailyTransferHistory() {
        System.out.println("\n============ [일일 거래량 조회] ============");
        
        System.out.print("📅 검색할 날짜를 입력해주세요 (예: 2025-03-15) >> ");
        String input = sc.nextLine();

        // 입력값 검증
        String validationError = adminService.validateDateInput(input);
        if (validationError != null) {
            System.out.println("❌ " + validationError);
            return;
        }

        DailyTransferSummaryDto summary = adminService.getDailyTransferSummary(LocalDate.parse(input));

        // 거래 정보 테이블 출력
        System.out.println("\n+---------------+--------+--------------------+");
        System.out.println("| 거래 유형     | 건수   | 금액               |");
        System.out.println("+---------------+--------+--------------------+");
        
        // 입금 정보
        String depositAmount = String.format("%,d 원", summary.getDepositAmount());
        System.out.println("| 💵 입금       | " + 
                ConsoleUtils.padRight(summary.getDepositCount() + "건", 6) + " | " + 
                ConsoleUtils.padRight(depositAmount, 17) + " |");
        
        // 출금 정보
        String withdrawAmount = String.format("%,d 원", summary.getWithdrawAmount());
        System.out.println("| 💸 출금       | " + 
                ConsoleUtils.padRight(summary.getWithdrawCount() + "건", 6) + " | " + 
                ConsoleUtils.padRight(withdrawAmount, 17) + " |");
        
        // 구분선
        System.out.println("+---------------+--------+-------------------+");
        
        // 순유입 정보
        String netTotalAmount = String.format("%,d 원", summary.getNetTotalAmount());
        String netTotalPrefix = summary.getNetTotalAmount() >= 0 ? "📈 자금 순유입" : "📉 자금 순유출";
        System.out.println("| " + netTotalPrefix + " | " + 
                ConsoleUtils.padRight("", 6) + " | " + 
                ConsoleUtils.padRight(netTotalAmount, 17) + " |");
        
        // 푸터
        System.out.println("+---------------+--------+-------------------+");
        
        // 날짜 정보 및 통계
        System.out.println("\n📆 조회 날짜: " + summary.getDate());
        int totalCount = summary.getDepositCount() + summary.getWithdrawCount();
        System.out.println("\n💡 총 거래 건수: " + totalCount + "건");
        
        // 서비스를 통한 거래량 판단
        boolean isHighVolume = adminService.isHighTransactionVolume(totalCount);
        System.out.println("📊 오늘은 거래량이 " + (isHighVolume ? "많은" : "적은") + " 날입니다.");
        System.out.println("🕒 조회 시간: " + java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }

    /**
     * 전체 계좌 조회 메서드
     */
    public void getAllAccounts() {
        System.out.println("\n============ [전체 계좌 조회] ============");
        
        List<AccountSummaryDto> accounts = adminService.getAllAccounts();
        Map<String, Object> stats = adminService.calculateAccountStats(accounts);
        
        // 계좌 목록 출력
        String format = "| %-12s | %-10s | %-15s | %-12s | %-10s | %-18s |%n";
        String line = "+----------------+------------+----------------+--------------+------------+------------------------+%n";
        
        System.out.printf(line);
        System.out.printf(format, "계좌번호", "이름", "계좌 비밀번호", "잔액", "계좌상태", "계좌개설일");
        System.out.printf(line);
        
        for (AccountSummaryDto dto : accounts) {
            String formattedBalance = String.format("%,d", dto.getBalance().longValue());
            String status = adminService.getAccountStatusText(dto.getStatus());
            String formattedDate = dto.getCreatedDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            
            System.out.printf(format, 
                    dto.getAccountNo(), 
                    dto.getName(), 
                    dto.getAccountPwd(), 
                    formattedBalance + " 원", 
                    status, 
                    formattedDate);
        }
        
        System.out.printf(line);
        
        // 통계 정보 출력
        System.out.println("📊 총 " + accounts.size() + "개의 계좌가 조회되었습니다.");
        System.out.println("💰 활성 계좌 총 잔액: " + String.format("%,d", ((BigDecimal)stats.get("totalBalance")).longValue()) + " 원");
        System.out.println("📆 조회 일시: " + java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }
    
    /**
     * 계좌 잠금 관리 메서드
     */
    public void manageAccountLock() {
        System.out.println("\n============ [계좌 잠금 관리] ============");
        
        if (!SessionManager.isAdmin()) {
            System.out.println("[!] 관리자만 접근할 수 있는 기능입니다.");
            return;
        }
        
        // 회원 이름 입력
        System.out.print("👤 회원 이름: ");
        String name = sc.nextLine();
        
        // 계좌번호 입력
        System.out.print("💳 잠금/해제할 계좌번호를 입력하세요: ");
        String accountNo = sc.nextLine();
        
        // 변경할 상태 입력
        System.out.println("\n계좌 상태 선택:");
        System.out.println("┌─────────────────────────────┐");
        System.out.println("│  1. 🔓 잠금 해제 (Y)        │");
        System.out.println("│  2. 🔒 잠금 (N)             │");
        System.out.println("└─────────────────────────────┘");
        System.out.print("👉 선택: ");
        
        String statusChoice = sc.nextLine();
        
        // 입력값 검증
        String validationError = adminService.validateAccountLockInput(name, accountNo, statusChoice);
        if (validationError != null) {
            System.out.println("❌ " + validationError);
            return;
        }
        
        // 상태 코드 변환
        char status = adminService.getStatusFromChoice(statusChoice);
        
        // 서비스 호출 및 결과 출력
        String result = adminService.changeAccountStatus(name, accountNo, status);
        
        if (result.startsWith("[!]")) {
            System.out.println("❌ " + result);
        } else {
            System.out.println("✅ " + result);
            System.out.println("📆 변경 시간: " + java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
    }
}