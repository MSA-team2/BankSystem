package model;

import java.math.BigDecimal;
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
	private String accountPwd; // 계좌비밀번호
	private BigDecimal balance; // 잔액
	private char status; // 잠금여부(Y,N)
	private LocalDateTime createDate; // 개설일
}
