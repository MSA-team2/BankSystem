package service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Scanner;

import dao.AccountDAO;
import dto.AccountProductDto;
import model.MemberVO;

public class MypageService {
	// DAO 생성해서 사용하기
	private final AccountDAO dao = new AccountDAO();

	// 개인신상
	// 계좌번호, 상품명, 원금, 이자율, 만기일, 총액 리스트로 뿌리기
	public void displayUserInfo(MemberVO user) {
		System.out.println("===== 마이페이지 =====");
		System.out.println("이름 : " + user.getName());
		System.out.println("전화번호 : " + user.getPhone());
		System.out.println("주소 : " + user.getAddress());

		List<AccountProductDto> accounts = dao.findAccountProductByMemberNo(user.getMemberNo());
		if (accounts.isEmpty()) {
			System.out.println("보유 중인 계좌가 없습니다.");
			return;
		}

		System.out.println("\n--- 보유 계좌 ---");
		System.out.printf("%-20s %-24s %-9s %-15s %-10s %16s\n", "계좌번호", "상품명", "이자율", "만기일", "D-Day", "총액");

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

			System.out.printf("%-20s %-24s %6.2f%%     %-15s %-10s %,15d원\n", dto.getAccountNo(), dto.getProductName(),
					dto.getInterestRate().doubleValue(), maturityStr, dDayStr, dto.getBalance().intValue());
		}
	}

	// 1 : (예금/적금) 중도해제
	public void withdrawProduct(MemberVO user) {
		// TODO 예금/적금 중도해지 메서드 호출하기

		// 적금 , 예금 만 리스트 뽑아서 보여주기
		List<AccountProductDto> accounts = dao.findYegeumJeoggeumByMemberNO(user.getMemberNo());
		if (accounts.isEmpty()) {
			System.out.println("해지할 상품이 없습니다.");
			return;
		}

		int idx = 1;
		System.out.println("\n--- 해지 가능한 상품 ---");
		System.out.printf("%-5s %-20s %-20s %-8s %-15s %-15s\n", "번호", "계좌번호", "상품명", "이자율", "만기일", "잔액");
		for (AccountProductDto dto : accounts) {

			String maturity = dto.getMaturityDate() != null ? dto.getMaturityDate().toString() : "없음";

			System.out.printf("%-5d %-20s %-20s %6.2f%%   %-15s %,15d원\n", idx++, dto.getAccountNo(),
					dto.getProductName(), dto.getInterestRate().doubleValue(), // BigDecimal → double
					maturity, dto.getBalance().intValue());
		}

		// 중도해지 메시지 보여주고 남은 돈 입출금으로 이체 -> 계좌 삭제
		System.out.print("해지할 상품의 번호를 적어주세요 : ");
		Scanner sc = new Scanner(System.in);
		int selectedYeJeok = sc.nextInt();

		// 번호 유효성 검사
		if (selectedYeJeok < 1 || selectedYeJeok > accounts.size()) {
			System.out.println("유효하지 않은 선택입니다.");
			return;
		}

		AccountProductDto selectedYeJeokAccount = accounts.get(selectedYeJeok - 1);
		System.out.println("선택한 계좌 : " + selectedYeJeokAccount.getAccountNo());

		System.out.print("맞으면 1, 아니면 2 : ");
		int num = sc.nextInt();

		if (num < 1 || num > 2) {
			System.out.println("올바른 번호를 입력해주세요.");
		}

		if (num == 1) {
			System.out.println("중도해지의 경우 이자를 적용 받을 수 없습니다. 정말 해지하시겠습니까? 1:예 2:아니요.");
			int num2 = sc.nextInt();

			if (num2 == 1) {
				List<AccountProductDto> ibchulList = dao.finIbchulgeum(user.getMemberNo());

				int idx2 = 1;
				System.out.println("\n--- 이체 받을 입출금 계좌를 선택하세요  ---");
				System.out.printf("%-5s %-20s %-20s %-8s %-15s %-15s\n", "번호", "계좌번호", "상품명", "이자율", "만기일", "잔액");
				for (AccountProductDto dto : ibchulList) {

					String maturity = dto.getMaturityDate() != null ? dto.getMaturityDate().toString() : "없음";

					System.out.printf("%-5d %-20s %-20s %6.2f%%   %-15s %,15d원\n", idx2++, dto.getAccountNo(),
							dto.getProductName(), dto.getInterestRate().doubleValue(), // BigDecimal → double
							maturity, dto.getBalance().intValue());
				}
				int selectedIbchul = sc.nextInt();

				if (selectedIbchul < 1 || selectedIbchul > ibchulList.size()) {
					System.out.println("올바른 번호를 입력해주세요.");
				}

				AccountProductDto selectedIbchulAccount = ibchulList.get(selectedIbchul - 1);

				dao.cancelAccount(selectedYeJeokAccount.getAccountNo(), selectedIbchulAccount.getAccountNo());
			}
		}

	}

	// 2 : 회원탈퇴
	public void deleteAccount() {
		// TODO 회원 탈퇴 메서드 호출하기
		System.out.println("TODO 회원 탈퇴 메서드 호출하기");

	}

}
