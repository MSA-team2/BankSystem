package util;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class PasswordCipher {
	// 키 반환
	public static SecretKey getKey() throws Exception {
		KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
		keyGenerator.init(128);
		SecretKey secretKey = keyGenerator.generateKey();
		return secretKey;
	}

	// 초기화 벡터 반환
	public static IvParameterSpec getIv() {
		byte[] iv = new byte[16];
		new SecureRandom().nextBytes(iv);
		return new IvParameterSpec(iv);
	}

	// 이어지는 암호화 및 복호화 예제 코드
	public static String encrypt(String specName, SecretKey key, IvParameterSpec iv, String plainText)
			throws Exception {
		Cipher cipher = Cipher.getInstance(specName);
		cipher.init(Cipher.ENCRYPT_MODE, key, iv);
		byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
		return new String(Base64.getEncoder().encode(encrypted));
	}

	public static String decrypt(String specName, SecretKey key, IvParameterSpec iv, String cipherText)
			throws Exception {
		Cipher cipher = Cipher.getInstance(specName);
		cipher.init(Cipher.DECRYPT_MODE, key, iv); // 모드가 다르다.
		// decoding은 encoding의 역순으로 진행
		byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(cipherText));
		return new String(decrypted, StandardCharsets.UTF_8);
	}

	
	// 사용 예제
//	public static void main(String[] args) throws Exception {
//		String str = "test 성공하냐 못하냐 그것이 문제";
//
//		SecretKey key = getKey();
//		IvParameterSpec ivParameterSpec = getIv();
//		String specName = "AES/CBC/PKCS5Padding";
//
//		String encryptedText = encrypt(specName, key, ivParameterSpec, str);
//		String decryptedText = decrypt(specName, key, ivParameterSpec, encryptedText);
//
//		System.out.println("cipherText: " + encryptedText);
//		System.out.println("plainText: " + decryptedText);
//	}
}
