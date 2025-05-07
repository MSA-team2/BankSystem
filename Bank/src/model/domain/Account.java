package model.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Account {
	private String accountNo; // 계좌번호
	private int memberNo; // 회원번호
	private int product_id;	// 상품번호
	private String accountPwd; // 계좌비밀번호
	private BigDecimal balance; // 잔액
	private char status; // 계좌 상태
	private int lock_cnt; // 틀린 횟수
	private LocalDateTime createDate; // 개설일
	private BigDecimal deposit_amount; // 납입금액
	private LocalDateTime maturityDate; // 만기일자
	private BigDecimal maturity_amount;
}
