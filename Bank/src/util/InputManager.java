package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class InputManager {
	public static final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

	// static 메서드를 통해 입력 받기
	public static String readLine() throws IOException {
		return reader.readLine();
	}
}
