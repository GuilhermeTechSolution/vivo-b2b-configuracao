package br.com.iatapp.service;

import java.security.SecureRandom;
import java.util.Random;

public class PasswordService {
	
	private final char[] SYMBOLS = (new String("~$@!&?")).toCharArray();
	private final char[] LOWERCASE = (new String("abcdefghijklmnopqrstuvwxyz")).toCharArray();
	private final char[] UPPERCASE = (new String("ABCDEFGHIJKLMNOPQRSTUVWXYZ")).toCharArray();
	private final char[] NUMBERS = (new String("0123456789")).toCharArray();
	private final char[] ALL_CHARS = (new String("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789~$@!&?")).toCharArray();
	private final Random rand = new SecureRandom();

	public String getPassword(int length) {
		char[] password = new char[length];
		
		//get the requirements out of the way
		password[0] = LOWERCASE[rand.nextInt(LOWERCASE.length)];
		password[1] = UPPERCASE[rand.nextInt(UPPERCASE.length)];
		password[2] = NUMBERS[rand.nextInt(NUMBERS.length)];
		password[3] = SYMBOLS[rand.nextInt(SYMBOLS.length)];
		
		//populate rest of the password with random chars
		for (int i = 4; i < length; i++) {
			password[i] = ALL_CHARS[rand.nextInt(ALL_CHARS.length)];
		}
		
		//shuffle it up
		for (int i = 0; i < password.length; i++) {
			int randomPosition = rand.nextInt(password.length);
			char temp = password[i];
			password[i] = password[randomPosition];
			password[randomPosition] = temp;
		}
		
		return new String(password);
	}
}
