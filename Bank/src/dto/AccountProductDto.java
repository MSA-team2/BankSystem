package dto;

import java.sql.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountProductDto {
	// 계좌번호, 상품이름, 이자율, 만기일, 잔액

	private String accountNo;
	private String productName;
	private Number interestRate;
	private Date maturityDate;
	private Number balance;

}
