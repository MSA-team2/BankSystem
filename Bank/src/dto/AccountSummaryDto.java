package dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class AccountSummaryDto {
	private String accountNo;
	private String name;
	private String accountPwd;
	private BigDecimal balance;
	private char status;
	private LocalDateTime createdDate;
	
}
