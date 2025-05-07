package view;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

import dto.AccountShowDTO;
import model.domain.Account;
import model.domain.Product;

public class AccountMenu {
	private final Scanner sc = new Scanner(System.in);
	
	// 상품 정보 받아와 콘솔 표시
	public int productShow(List<Product> list) {
		System.out.println("============================= 🏦 계좌 개설 =============================");
		System.out.println("============================= [ 📋 상품 선택 ]=============================");
		System.out.println("\t번호  상품명\t이자율\t 월납입한도 \t 최대예치한도");
		System.out.println("\t-----------------------------------------------------------");
		for (Product p : list) {
			if(p.getProduct_type() == 200) {	// 적금 출력
				System.out.printf("\t  " + p.getProductId() + " " + p.getProductName() + "\t(" + p.getInterestRate() + "%%)  %,d원 \n"
						,p.getMaxMonthlyDeposit().longValue());
			}else if(p.getProduct_type() == 300) {	// 예금
				System.out.printf("\t  " +p.getProductId() + " " + p.getProductName() + "\t(" + p.getInterestRate() + "%%) \t\t%,d원 \n"
						, p.getMaxDepositAmount().longValue());
			}else if(p.getProduct_type() == 100){	// 입출금
			System.out.printf("\t  " + p.getProductId() + " " + p.getProductName() + "\t(" + p.getInterestRate() + "%%)\n");
			}
		}
		System.out.print("========================================================================="
				+ "\n(0입력 시 메인메뉴로 이동합니다)\n👉 상품을 선택하세요: ");
		return sc.nextInt();
	}
	
	public String myAccountShow(List<AccountShowDTO> list) {
		System.out.println("============== [ 💳 보유 입출금 계좌 ] ==============");
		System.out.println("\t번호  계좌번호\t   잔액");
		System.out.println("\t-----------------------------------------");
		for (AccountShowDTO a : list) {
			System.out.printf("\t  " + a.getAccountNum() + " " + a.getAccountNo() + "  %,d원 \n",a.getBalance());
		}
		System.out.println("==================================================");
		System.out.print("\n💳 예금계좌로 입금할 계좌번호 입력: ");
		return sc.next();
	}
	
	public BigDecimal inputDeposit() {
		System.out.print("ℹ️ (0입력 시 메인메뉴로 이동합니다)\n💵 맡기실 금액: ");
		return sc.nextBigDecimal();
	}
	
	public BigDecimal inputInitialBalance() {
		System.out.print("ℹ️ (0입력 시 메인메뉴로 이동합니다)\n💵 입금액: ");
		return sc.nextBigDecimal();
	}
	
	public BigDecimal inputDepositAmount() {
		System.out.print("ℹ️ (0입력 시 메인메뉴로 이동합니다)\n💵 매월 납입금액: ");
		return sc.nextBigDecimal();
	}
	
	public String inputPassword() {
        while (true) {
            System.out.print("👉 비밀번호(4자리): ");
            String pwd1 = sc.next();
            if(!pwd1.matches("\\d{4}")) {
            	System.out.println("❌잘못된 형식의 비밀번호입니다. 숫자 4자리만 입력해주세요.");
            	continue;
            }
            System.out.print("👉 비밀번호 확인: ");
            String pwd2 = sc.next();
            if(!pwd2.matches("\\d{4}")) {
            	System.out.println("❌잘못된 형식의 비밀번호입니다. 숫자 4자리만 입력해주세요.");
            	continue;
            }
            if (pwd1.equals(pwd2)) return pwd1;
            System.out.println("❌비밀번호가 일치하지 않습니다. 다시 입력해주세요.");
        }
    }
	
	public void successMakeAccount(Account vo, BigDecimal balance) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일");

	    String createDate = vo.getCreateDate().format(formatter);
	    String maturityDate = vo.getMaturityDate() != null ? vo.getMaturityDate().format(formatter) : "없음";
		System.out.println("\n==============================================================");
		System.out.println("🎉 계좌 개설에 성공하였습니다! 🎉\n");
		if(vo.getAccountNo().substring(0, 3).equals("100")) {	// 입출금 출력
			System.out.printf("💳 계좌번호: " + vo.getAccountNo() + "\n💰 잔액: %,d원\n📅 만기일: 없음", balance.longValue());
		}else if(vo.getAccountNo().substring(0, 3).equals("300")) {	// 예금 출력
			System.out.printf("💳 계좌번호: %s\n💵 입금액: %,d원\n📅 개설일: %s\n📅 만기일: %s\n💰 만기시 예상금액: %,d원\n",
		            vo.getAccountNo(),
		            balance.longValue(),
		            createDate,
		            maturityDate,
		            vo.getMaturity_amount().longValue()
		        );
		}else {	// 적금
		System.out.printf("💳 계좌번호: %s\n📅 개설일: %s\n📅 만기일: %s\n💵 매월 납입금: %,d원\n💰 만기시 예상금액: %,d원\n",
	            vo.getAccountNo(),
	            createDate,
	            maturityDate,
	            vo.getDeposit_amount().longValue(),
	            vo.getMaturity_amount().longValue());
		}
	}

	public void printMessage(String string) {
		System.out.println(string);
	}
	
}