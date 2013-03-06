package utils;

import java.util.Random;

public class PasswordGenerator {

    private static final String validChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
    private static final Random generator = new Random();

	public static String generate() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 8; i++) {
            sb.append(validChars.charAt(generator.nextInt(validChars.length())));
		}
		return sb.toString();
	}
}