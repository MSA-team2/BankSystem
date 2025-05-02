package model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountDTO {
	private String accountNo;
	private int memberNo;
	private int productId;
	private String accountPwd;
	private long balance;
	private String status;
	private int lockCnt;
	private LocalDateTime createdDate;
	private long depositAmount;
	private LocalDate maturityDate;

	// 기본 생성자
	public AccountDTO() {
	}
}
