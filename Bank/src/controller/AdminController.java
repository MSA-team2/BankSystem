package controller;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

import dto.AccountSummaryDto;
import dto.AdminMemberDto;
import dto.DailyTransferSummaryDto;
import service.AdminService;
import util.Validator;

public class AdminController {

	private final AdminService adminService = new AdminService();

	static Scanner sc = new Scanner(System.in);

	public void showDailyTransferHistory() throws SQLException {
		System.out.print("검색할 날짜를 입력해주세요 (예: 2025-03-15) >> ");
		String input = sc.nextLine();

		if (!Validator.isValidDate(input)) {
			System.out.println("올바른 날짜 형식이 아닙니다.");
			return;
		}

		DailyTransferSummaryDto summary = adminService.getDailyTransferSummary(LocalDate.parse(input));

		System.out.println("===== 일일 거래량 =====");
		System.out.println("날짜 : " + summary.getDate());
		System.out.println("입금 : " + summary.getDepositCount() + "건, " + summary.getDepositAmount() + "원");
		System.out.println("출금 : " + summary.getWithdrawCount() + "건, " + summary.getWithdrawAmount() + "원");
		System.out.println("자금 순유입 : " + summary.getNetTotalAmount() + "원");
	}

	public void selectAllMember() throws SQLException {
		List<AdminMemberDto> list = adminService.getAllMembers();

		System.out.println("===== 전체 회원 조회 =====");
		System.out.println("이름" + "주민번호" + "비밀번호" + "주소" + "전화번호" + "계좌잠금여부" + "역할");

		for (MemberDto dto : list) {
			System.out.println(dto.getName() + "\t" + dto.getJumin() + "\t" + dto.getMemberId() + "\t"
					+ dto.getPassword() + "\t" + dto.getAddress() + "\t" + dto.getPhone() + "\t" + dto.getLockYn()
					+ "\t" + (dto.getRole() == 1 ? "Admin" : "User"));
		}
	}

	}

	public void selectAllAccounts() {
		List<AccountSummaryDto> list = adminService.getAllAccounts();

		System.out.println("===== 전체 계좌 조회 =====");
		System.out.println("계좌번호\t이름\t계좌 비밀번호\t잔액\t계좌상태\t계좌개설일");

		for (AccountSummaryDto dto : list) {
			System.out.println(dto.getAccountNo() + "\t" + dto.getName() + "\t" + dto.getAccountPwd() + "\t"
					+ dto.getBalance() + "\t" + dto.getStatus() + "\t" + dto.getCreatedDate());
		}
	}
}
