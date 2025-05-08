package model.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TransactionHistory {
	private int transactionId; // 거래ID
	private String accountNo; // 계좌번호
	private String transactionType; // 거래종류(입금/출금/이체)
	private BigDecimal amount; // 거래금액
	private LocalDateTime transactionDate; // 거래일시
	private String targetAccount; // 이체할 상대 계좌
	
	public TransactionHistory() {}
	public TransactionHistory(LocalDateTime transactionDate, String transactionType, 
			BigDecimal amount,  String targetAccount) {
		this.transactionDate = transactionDate;
		this.transactionType = transactionType;
		this.amount = amount;
		this.targetAccount = targetAccount;
	}
	
	public TransactionHistory (String accountNo, String transactionType, 
			BigDecimal amount, LocalDateTime transactionDate, String targetAccount) {
			this.accountNo = accountNo;
			this.transactionType = transactionType;
			this.amount = amount;
			this.transactionDate = transactionDate;
			this.targetAccount = targetAccount;
		}
}
