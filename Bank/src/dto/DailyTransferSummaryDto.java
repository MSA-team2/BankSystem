package dto;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DailyTransferSummaryDto {
	
	private LocalDate date;
    private int depositCount;
    private int depositAmount;
    private int withdrawCount;
    private int withdrawAmount;
    private int netTotalAmount;
	
    @Builder
    public DailyTransferSummaryDto(LocalDate date, int depositCount, int depositAmount, int withdrawCount,
			int withdrawAmount, int netTotalAmount) {
		super();
		this.date = date;
		this.depositCount = depositCount;
		this.depositAmount = depositAmount;
		this.withdrawCount = withdrawCount;
		this.withdrawAmount = withdrawAmount;
		this.netTotalAmount = netTotalAmount;
	}
}
