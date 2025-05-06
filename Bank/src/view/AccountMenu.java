package view;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

import dto.AccountShowDTO;
import model.AccountVO;
import model.ProductVO;

public class AccountMenu {
	private final Scanner sc = new Scanner(System.in);
	
	// 상품 정보 받아와 콘솔 표시
	public int productShow(List<ProductVO> list) {
		System.out.println("===== 계좌 개설 =====");
		System.out.println("=== 상품 선택 ===");
		for (ProductVO p : list) {
			System.out.println(p.getProductId() + ". " + p.getProductName() + " (" + p.getInterestRate() + "%)");
		}
		System.out.print("상품 번호 선택\n(0입력 시 메인메뉴로 이동합니다): ");
		return sc.nextInt();
	}
	
	public String myAccountShow(List<AccountShowDTO> list) {
		System.out.println("번호  계좌번호\t잔액");
		for (AccountShowDTO a : list) {
			System.out.println(a.getAccountNum() + ". " + a.getAccountNo() + " " + a.getBalance());
		}
		System.out.print("예금계좌로 입금할 계좌번호 입력: ");
		return sc.next();
	}
	
	public BigDecimal inputDeposit() {
		System.out.println("맡기실 금액\n(0입력 시 메인메뉴로 이동합니다): ");
		return sc.nextBigDecimal();
	}
	
	public BigDecimal inputInitialBalance() {
		System.out.print("입금액\n(0입력 시 메인메뉴로 이동합니다): ");
		return sc.nextBigDecimal();
	}
	
	public BigDecimal inputDepositAmount() {
		System.out.print("매월 납입금액\n(0입력 시 메인메뉴로 이동합니다): ");
		return sc.nextBigDecimal();
	}
	
	public String inputPassword() {
        while (true) {
            System.out.print("비밀번호(4자리): ");
            String pwd1 = sc.next();
            System.out.print("비밀번호 확인: ");
            String pwd2 = sc.next();
            if (pwd1.equals(pwd2)) return pwd1;
            System.out.println("비밀번호 불일치. 다시 입력.");
        }
    }
	
	public void successMakeAccount(AccountVO vo) {
		System.out.println("계좌 개설에 성공하였습니다!");
		if(vo.getAccountNo().substring(0, 3).equals("100")) {	// 입출금 출력
			System.out.println("계좌번호: " + vo.getAccountNo() + "\n잔액: " + vo.getBalance() +"원\n만기일: 없음");
		}else if(vo.getAccountNo().substring(0, 3).equals("300")) {	// 예금 출력
			System.out.println("계좌번호: " + vo.getAccountNo() + "\n입금액: " 
					+ vo.getBalance() + "원\n개설일: " + vo.getCreateDate() +"\n만기일: "+ vo.getMaturityDate()
					+ "\n만기시 예상금액: " + vo.getMaturity_amount() + "원");
		}else {	// 적금
		System.out.println("계좌번호: " + vo.getAccountNo() + "\n개설일: " + vo.getCreateDate() 
		+"\n만기일: "+ vo.getMaturityDate() + "\n매월 납입금: " + vo.getDeposit_amount()
		+ "원\n만기시 예상금액: " + vo.getMaturity_amount() + "원");
		}
	}

	public void printMessage(String string) {
		System.out.println(string);
	}
	
}
