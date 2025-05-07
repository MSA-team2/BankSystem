package util;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class Validator {
	
	public static boolean isValidHyphenJumin(String input) {
		/**
		 * 하이픈이 포함된 6-7 형식의 주민번호 유효성 검사
		 * 예: 880902-1233521
		 */
		String regExp = "^\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12][0-9]|[3][01])\\-[1-4][0-9]{6}$";
		return input != null && input.matches(regExp);
	}
	
	/**
     * 하이픈이 포함된 3-4-4 형식의 계좌번호 유효성 검사
     * 예: 010-1234-5678
     */
    public static boolean isValidHyphenAccountNumber(String input) {
        String pattern = "^\\d{3}-\\d{4}-\\d{4}$";
        return input != null && input.matches(pattern);
    }
    
    /**
     * yyyy-MM-dd 형식의 날짜 문자열이 유효한지 검사
     * 예: 2025-05-01
     */
    public static boolean isValidDate(String input) {
        if (input == null || !input.matches("^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])$"))
            return false;

        try {
            LocalDate.parse(input);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
    
    public static boolean isValidNumber(String input) {
        String pattern = "^[0-9]+$";
        return input != null && input.matches(pattern);
    }
}
