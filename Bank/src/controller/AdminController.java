package controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

import dto.AccountSummaryDto;
import dto.DailyTransferSummaryDto;
import model.MemberVO;
import service.AdminMemberService;
import service.AdminService;
import util.ConsoleUtils;
import util.Validator;

public class AdminController {

	private final AdminService adminService = new AdminService();
	private final Scanner sc = new Scanner(System.in);

	public void showDailyTransferHistory() {
		System.out.print("📅 검색할 날짜를 입력해주세요 (예: 2025-03-15) >> ");
	    String input = sc.nextLine();

	    if (!Validator.isValidDate(input)) {
	        System.out.println("❌ 올바른 날짜 형식이 아닙니다.");
	        return;
	    }

	    DailyTransferSummaryDto summary = adminService.getDailyTransferSummary(LocalDate.parse(input));
	    // 헤더
	    System.out.println("\n+-------------------------------------------+");
	    System.out.println("|          💰 일일 거래량 요약 💰         	  |");
	    System.out.println("+---------------------------------------------+");
	    
	    // 날짜 정보
	    System.out.println("\n📆 조회 날짜: " + summary.getDate());
	    
	    // 거래 정보 테이블
	    System.out.println("\n+---------------+--------+--------------------+");
	    System.out.println("| 거래 유형     | 건수   | 금액              	|");
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
	    
	    // 추가 정보
	    System.out.println("\n💡 총 거래 건수: " + (summary.getDepositCount() + summary.getWithdrawCount()) + "건");
	    
	    // 거래량이 많은지 적은지 판단하여 메시지 표시
	    if ((summary.getDepositCount() + summary.getWithdrawCount()) > 10) {
	        System.out.println("📊 오늘은 거래량이 많은 날입니다.");
	    } else {
	        System.out.println("📊 오늘은 거래량이 적은 날입니다.");
	    }
	    
	    // 현재 시간 표시
	    System.out.println("🕒 조회 시간: " + java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
	}

	public void getAllAccounts() {
		List<AccountSummaryDto> list = adminService.getAllAccounts();
		
	    System.out.println("\n" +
	            "+----------------------------------------------+\n" +
	            "|          🏦 은행 계좌 관리 시스템 🏦         |\n" +
	            "+----------------------------------------------+");
	    
	    // 열 너비 정의
	    String format = "| %-12s | %-10s | %-15s | %-12s | %-10s | %-18s |%n";
	    
	    // 구분선
	    String line = "+----------------+------------+----------------+--------------+------------+------------------------+%n";
	    
	    // 헤더 출력
	    System.out.printf(line);
	    System.out.printf(format, "계좌번호", "이름", "계좌 비밀번호", "잔액", "계좌상태", "계좌개설일");
	    System.out.printf(line);
	    
	    // 데이터 출력
	    for (AccountSummaryDto dto : list) {
	        // 금액에 천 단위 구분자 추가
	        String formattedBalance = String.format("%,d", dto.getBalance().longValue());
	        
	        // 계좌 상태에 이모지 추가 (char 타입 비교)
	        String status = dto.getStatus() == 'Y' ? "✅ Y" : "❌ N";
	        
	        // 날짜 포맷팅
	        String formattedDate = dto.getCreatedDate().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
	        
	        System.out.printf(format, 
	                dto.getAccountNo(), 
	                dto.getName(), 
	                dto.getAccountPwd(), 
	                formattedBalance + " 원", 
	                status, 
	                formattedDate);
	    }
	    
	    System.out.printf(line);
	    System.out.println("📊 총 " + list.size() + "개의 계좌가 조회되었습니다.");
	    
	    // 잔액이 일정 금액 이상인 VIP 계좌 표시
	    int vipCount = 0;
	    BigDecimal totalBalance = BigDecimal.ZERO;
	    BigDecimal vipThreshold = new BigDecimal("10000000");
	    
	    for (AccountSummaryDto dto : list) {
	        if (dto.getBalance().compareTo(vipThreshold) >= 0 && dto.getStatus() == 'Y') {
	            vipCount++;
	        }
	        if (dto.getStatus() == 'Y') {
	            totalBalance = totalBalance.add(dto.getBalance());
	        }
	    }
	    
	    System.out.println("💎 VIP 계좌 수: " + vipCount + "개");
	    System.out.println("💰 활성 계좌 총 잔액: " + String.format("%,d", totalBalance.longValue()) + " 원");
	    System.out.println("📆 조회 일시: " + java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
	}
	
	public void manageAccountLock() {
		if (!SessionManager.isAdmin()) {
	        System.out.println("[!] 관리자만 접근할 수 있는 기능입니다.");
	        return;
	    }

	    // 헤더
	    System.out.println("\n+--------------------------------------------+");
	    System.out.println("|          🔒 계좌 잠금 관리 🔒             |");
	    System.out.println("+--------------------------------------------+");
	    
	    // 회원 이름 입력
	    System.out.print("👤 회원 이름: ");
	    String name = sc.nextLine();
	    
	    // 계좌번호 입력
	    System.out.print("💳 잠금/해제할 계좌번호를 입력하세요: ");
	    String accountNo = sc.nextLine();
	    
	    if (!Validator.isValidHyphenAccountNumber(accountNo)) {
	        System.out.println("❌ 잘못된 계좌번호 형식입니다. 예) 100-1234-5678");
	        return;
	    }
	    
	    // 변경할 상태 입력
	    System.out.println("\n계좌 상태 선택:");
	    System.out.println("┌─────────────────────────────┐");
	    System.out.println("│  1. 🔓 잠금 해제 (Y)        │");
	    System.out.println("│  2. 🔒 잠금 (N)             │");
	    System.out.println("└─────────────────────────────┘");
	    System.out.print("👉 선택: ");
	    
	    String statusChoice = sc.nextLine();
	    char status;
	    
	    switch(statusChoice) {
		    case "1": 
		    case "Y":
		    case "y":
		        status = 'Y';
		        break;
		    case "2":
		    case "N":
		    case "n":
		        status = 'N';
		        break;
	        default:
	            System.out.println("❌ 잘못된 선택입니다. 작업을 취소합니다.");
	            return;
	    }
	    
	    // 서비스 메서드 호출 (주민번호 제거)
	    String result = adminService.changeAccountStatus(name, accountNo, status);
	    
	    // 결과 출력
	    if (result.startsWith("[!]")) {
	        System.out.println("❌ " + result);
	    } else {
	        System.out.println("✅ " + result);
	        System.out.println("📆 변경 시간: " + java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
	    }
    }
}
