package dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AccountSummaryDto {
	private String accountNo;
	private String name;
	private String accountPwd;
	private BigDecimal balance;
	private char status;
	private LocalDateTime createdDate;
	
	@Builder
	private AccountSummaryDto(String accountNo, String name, String accountPwd, BigDecimal balance, char status,
			LocalDateTime createdDate) {
		super();
		this.accountNo = accountNo;
		this.name = name;
		this.accountPwd = accountPwd;
		this.balance = balance;
		this.status = status;
		this.createdDate = createdDate;
	}
}
