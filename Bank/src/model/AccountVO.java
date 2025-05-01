package model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AccountVO {
	private String accountNo; // 계좌번호
	private int memberNo; // 회원번호
	private int product_id;	// 상품번호
	private String accountPwd; // 계좌비밀번호
	private BigDecimal balance; // 잔액
	private char status; // 잠금여부(Y,N)
	private int lock_cnt;	// 비밀번호 틀린횟수
	private LocalDateTime createDate; // 개설 및 가입일
	private BigDecimal deposit_amount;	// 납입 금액
	private LocalDate maturity_date; // 만기일
	
}
