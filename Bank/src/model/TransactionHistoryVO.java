package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionHistoryVO {
	private int transactionId;// 거래ID
	private String accountNo;// 계좌번호
	private String transactionTime;// 거래종류(입금/출금/이체)
	private BigDecimal ammount;// 거래금액
	private LocalDateTime transactionDate;// 거래일시
	private String targetAccount; // 이체할 상대 계좌

}
