package model;
import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ProductVO {
	public final String ClassName = "PRODUCT";
	private int productId; // 상품번호
	private String productName; // 상품명
	private int product_type;
	private BigDecimal interestRate; // 이자율
	private int periodMonths; // 가입기간

}
