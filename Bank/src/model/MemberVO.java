package model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MemberVO {
	private int memberNo; // 회원번호
	private String name; // 이름
	private String jumin; // 주민번호
	private String memberId; // 회원아이디
	private String password; // 비밀번호
	private String address; // 주소
	private String phone; // 전화번호
	private char status; // 잠금여부 (Y,N)
	private int lockCnt; // 틀린횟수
	private int role; // 관리자 여부(0,1) 0: 일반회원 / 1: 관리자
}
