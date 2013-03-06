package utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SecurityHelper {

	public static String createPasswordHash(String password) {
        StringBuilder sb = new StringBuilder();
		try {
            MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(password.getBytes());
		byte byteData[] = md.digest();

            for (byte aByteData : byteData) {
                sb.append(Integer.toString((aByteData & 0xff) + 0x100, 16)
					.substring(1));
		}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	public static void main(String[] args){
		System.out.println(createPasswordHash("test123"));
	}
	
	
}
