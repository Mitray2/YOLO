package utils;

import java.util.Random;

public class PasswordGenerator {

	public static String generate() {
		String validChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
		String s = "";
		Random generator = new Random();
		for (int i = 0; i < 8; i++) {
			s += validChars.charAt(generator.nextInt(validChars.length()));
		}
		return s;
	}
}