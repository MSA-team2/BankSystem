package model;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString

public class ProductVO {
	private int productId; // 상품번호
	private String productName; // 상품명
	private BigDecimal interestRate; // 이자율
	private int periodMonths; // 가입기간

}
