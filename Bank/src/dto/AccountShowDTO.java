package dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class AccountShowDTO {
	private int accountNum;
	private String accountNo;
	private String productName;
	private long balance;
	
	public AccountShowDTO(int accountNum, String accountNo, String productName, long balance) {
	        this.accountNum = accountNum;
			this.accountNo = accountNo;
	        this.productName = productName;
	        this.balance = balance;
	    }

}

