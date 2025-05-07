package service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Scanner;

import dao.AccountDAO;
import dao.TransactionDAO;
import dto.AccountProductDto;
import model.domain.Member;

public class MypageService {
	// DAO 생성해서 사용하기
	private final AccountDAO dao = new AccountDAO();
	private final TransactionDAO transactionDao = new TransactionDAO();
	Scanner sc = new Scanner(System.in);

	// 개인신상
	// 계좌번호, 상품명, 원금, 이자율, 만기일, 총액 리스트로 뿌리기
	public void displayUserInfo(Member user) {
		System.out.println("\n================================");
		System.out.println("\t🔹 마이페이지");
		System.out.println("================================");

		System.out.println("이름 : " + user.getName());
		System.out.println("전화번호 : " + user.getPhone());
		System.out.println("주소 : " + user.getAddress());

		List<AccountProductDto> accounts = dao.findAccountProductByMemberNo(user.getMemberNo());
		if (accounts.isEmpty()) {
			System.out.println("보유 중인 계좌가 없습니다.");
			return;
		}

		System.out.println("\n[ 💰 보유 계좌 ]");
		System.out.printf("\n%-20s %-24s %-9s %-15s %-10s %20s\n", "계좌번호", "상품명", "이자율", "만기일", "D-Day", "총액");
		System.out.println("─────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────");

		for (AccountProductDto dto : accounts) {
		    String maturityStr;
		    String dDayStr;

		    if (dto.getMaturityDate() != null) {
		        LocalDate maturityDate = dto.getMaturityDate().toLocalDate();
		        LocalDate today = LocalDate.now();
		        long days = ChronoUnit.DAYS.between(today, maturityDate);

		        if (days > 0) {
		            dDayStr = "D-" + days;
		        } else if (days == 0) {
		            dDayStr = "D-day";
		        } else {
		            dDayStr = "D+" + Math.abs(days); // 이미 지난 경우
		        }

		        maturityStr = maturityDate.toString();
		    } else {
		        maturityStr = "없음";
		        dDayStr = "-";
		    }

		    // 컬럼 간격 조절
		    System.out.printf("%-20s %-24s %8.2f%%   %-15s %-10s %,20d원\n", 
		        dto.getAccountNo(), 
		        dto.getProductName(), 
		        dto.getInterestRate().doubleValue(), 
		        maturityStr, 
		        dDayStr, 
		        dto.getBalance().intValue());
		}
	}

	// 1 : (예금/적금) 중도해제
	public void withdrawProduct(Member user) {

		while (true) {

			// 적금 , 예금 만 리스트 뽑아서 보여주기
			List<AccountProductDto> accounts = dao.findYegeumJeoggeumByMemberNO(user.getMemberNo());
			if (accounts.isEmpty()) {
				System.out.println("해지할 상품이 없습니다.");
				return;
			}

			System.out.println("\n[ 🛈 해지 가능한 상품 ]");
			System.out.println("─────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────");
			System.out.printf("%-5s %-20s %-20s %-8s %-15s %-15s\n", "번호", "계좌번호", "상품명", "이자율", "만기일", "잔액");
			int idx = 1;
			for (AccountProductDto dto : accounts) {
				String maturity = dto.getMaturityDate() != null ? dto.getMaturityDate().toString() : "없음";
				System.out.printf("%-5d %-20s %-20s %6.2f%%   %-15s %,15d원\n", idx++, dto.getAccountNo(),
						dto.getProductName(), dto.getInterestRate().doubleValue(), maturity,
						dto.getBalance().intValue());
			}

			System.out.print("\n👉 해지할 상품의 번호를 입력하세요 (0. 뒤로가기) : ");
			int selected = sc.nextInt();
			sc.nextLine(); // 버퍼 클리어

			if (selected == 0)
				return;
			if (selected < 1 || selected > accounts.size()) {
				System.out.println("잘못된 번호입니다. 다시 입력해주세요.");
				continue;
			}

			AccountProductDto target = accounts.get(selected - 1);
			System.out.println("▶️ 선택한 계좌: " + target.getAccountNo());
			System.out.print("\n맞으면 1, 아니면 2 : ");
			int confirm = sc.nextInt();
			sc.nextLine();

			if (confirm != 1) {
				System.out.println("취소되었습니다.");
				continue;
			}

			System.out.print("⚠️ 중도해지 시 이자는 지급되지 않습니다. 해지하시겠습니까? (1: 예, 2: 아니요): ");
			int finalConfirm = sc.nextInt();
			sc.nextLine();
			if (finalConfirm != 1) {
				System.out.println("해지 요청이 취소되었습니다.");
				continue;
			}

			List<AccountProductDto> depositTargets = dao.finIbchulgeum(user.getMemberNo());
			if (depositTargets.isEmpty()) {
				System.out.println("입금 가능한 입출금 계좌가 없습니다.");
				return;
			}

			System.out.println("\n[ 💲 이체받을 입출금 계좌 선택 ]");
			System.out.println("─────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────");
			System.out.printf("%-5s %-20s %-20s %-8s %-15s %-15s\n", "번호", "계좌번호", "상품명", "이자율", "만기일", "잔액");
			idx = 1;
			for (AccountProductDto dto : depositTargets) {
				String maturity = dto.getMaturityDate() != null ? dto.getMaturityDate().toString() : "없음";
				System.out.printf("%-5d %-20s %-20s %6.2f%%   %-15s %,15d원\n", idx++, dto.getAccountNo(),
						dto.getProductName(), dto.getInterestRate().doubleValue(), maturity,
						dto.getBalance().intValue());
			}

			System.out.print("👉 입금 계좌를 선택하세요. (0. 뒤로가기) : ");

			int selectedDeposit = sc.nextInt();
			sc.nextLine();
			if (selectedDeposit == 0) {
				System.out.println("이체가 취소되었습니다.");
				continue;
			}

			if (selectedDeposit < 1 || selectedDeposit > depositTargets.size()) {
				System.out.println("잘못된 번호입니다. 처음으로 돌아갑니다.");
				continue;
			}

			AccountProductDto depositAccount = depositTargets.get(selectedDeposit - 1);
			transactionDao.transferAllBalanceAndCloseAndDelete(target.getAccountNo(), depositAccount.getAccountNo(),
					target.getBalance());
			System.out.println("중도해지가 완료되었습니다. 입출금 계좌로 잔액이 이체되었습니다.");

			// 중도해지 후 다시 목록 보기
			System.out.print("다른 상품도 해지하시겠습니까? (Y/N): ");
			String again = sc.nextLine().trim().toUpperCase();
			if (!again.equals("Y"))
				break;
		}

	}

	// 2 : 관리자 문의
	public void callAdmimAboutAccountLock() {
		
		System.out.println("관리자에게 문의 접수되었습니다.");

	}

}
