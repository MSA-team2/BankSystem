package util;

public class ConsoleUtils {
	/**
     * 문자열 오른쪽에 공백을 추가하여 지정된 길이로 만드는 메서드
     * 콘솔 출력 시 열 너비를 일정하게 유지하는 데 사용
     * 
     * @param s 원본 문자열
     * @param n 원하는 최종 길이
     * @return 지정된 길이로 오른쪽에 공백이 추가된 문자열
     */
    public static String padRight(String s, int n) {
        return String.format("%-" + n + "s", s);
    }
}
