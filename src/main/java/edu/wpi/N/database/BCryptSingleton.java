package edu.wpi.N.database;

import at.favre.lib.crypto.bcrypt.*;

import java.nio.charset.StandardCharsets;

public class BCryptSingleton {
	private int cost;

	private static BCryptSingleton instance = null;

	private BCryptSingleton(){
		this.cost = 10;
	}

	public static BCryptSingleton getInstance(){
		if(instance == null) instance = new BCryptSingleton();

		return instance;
	}

	public void setCost(int cost) {
		this.cost = cost;
	}

	public byte[] hash(String pw){
		return BCrypt.with(LongPasswordStrategies.hashSha512(BCrypt.Version.VERSION_2Y)).hash(cost, pw.getBytes(StandardCharsets.UTF_8));
	}

	public boolean verifyPW(String pw, byte[] hash) throws IllegalBCryptFormatException {
		BCrypt.Result res = BCrypt.verifyer().verify(pw.getBytes(StandardCharsets.UTF_8), hash);
		if(!res.validFormat) throw new IllegalBCryptFormatException(res.formatErrorMessage);
		return res.verified;
	}
}
