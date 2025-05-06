package dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Getter; //lombok 오류때문에 추가적으로 임포트를 해야하는 상황
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TransactionDTO {
	private int transactionId; // 거래ID
	private String accountNo; // 계좌번호
	private String transactionType; // 거래종류(입금/출금/이체)
	private BigDecimal amount; // 거래금액
	private LocalDateTime transactionDate; // 거래일시
	private String targetAccount; // 이체할 상대 계좌

	public TransactionDTO( ) {}
	public TransactionDTO (String accountNo, String transactionType, 
			BigDecimal amount, String targetAccount) {
			this.accountNo = accountNo;
			this.transactionType = transactionType;
			this.amount = amount;
			this.targetAccount = targetAccount;
		}
	public TransactionDTO (String accountNo, String transactionType, 
		BigDecimal amount, LocalDateTime transactionDate, String targetAccount) {
		this.accountNo = accountNo;
		this.transactionType = transactionType;
		this.amount = amount;
		this.transactionDate = transactionDate;
		this.targetAccount = targetAccount;
	}
}
