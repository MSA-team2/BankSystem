package dto;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class DailyTransferSummaryDto {
	
	private LocalDate date;
    private int depositCount;
    private int depositAmount;
    private int withdrawCount;
    private int withdrawAmount;
    private int netTotalAmount;
}
