package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AccountProductVO {
	private int accountProductId; // 가입상품번호
	private String accountNo; // 계좌번호
	private int productId; // 상품번호
	private BigDecimal depositAmount; // 납입금액
	private LocalDateTime joinDate; // 가입일자
	private LocalDateTime maturityDate; // 만기일자

}
