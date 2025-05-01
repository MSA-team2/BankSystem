package dto;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AdminMemberDto {
	private String name;
    private String jumin;
    private String memberId;
    private String password;
    private String address;
    private String phone;
    private String lockYn;
    private int role; // 0 = User, 1 = Admin
    
    @Builder
	public AdminMemberDto(String name, String jumin, String memberId, String password, String address, String phone,
			String lockYn, int role) {
		super();
		this.name = name;
		this.jumin = jumin;
		this.memberId = memberId;
		this.password = password;
		this.address = address;
		this.phone = phone;
		this.lockYn = lockYn;
		this.role = role;
	}
}
