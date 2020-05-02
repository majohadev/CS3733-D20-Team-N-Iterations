package edu.wpi.N.database;

import static org.junit.jupiter.api.Assertions.*;

import at.favre.lib.crypto.bcrypt.IllegalBCryptFormatException;
import java.util.Random;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class BCryptTester {
  private static BCryptSingleton hasher;

  @BeforeAll
  public static void setup() {
    hasher = BCryptSingleton.getInstance();
  }

  @Test
  public void hashAndVerifyTest() throws IllegalBCryptFormatException {
    byte[] hash1 = hasher.hash("password123");
    byte[] hash2 = hasher.hash("password123");
    byte[] hash3 = hasher.hash("ABCD");

    assertNotEquals(hash1, hash2);
    assertTrue(hasher.verifyPW("password123", hash1));
    assertTrue(hasher.verifyPW("password123", hash2));
    assertFalse(hasher.verifyPW("password123", hash3));
    assertTrue(hasher.verifyPW("ABCD", hash3));
    assertThrows(
        IllegalBCryptFormatException.class, () -> hasher.verifyPW("ABCD", "123".getBytes()));
  }

  @Test
  public void longPasswordTest() throws IllegalBCryptFormatException {
    char[] pwarr = new char[100];
    Random rand = new Random();

    for (int i = 0; i < pwarr.length; i++) {
      pwarr[i] = (char) rand.nextInt();
    }

    String pw = new String(pwarr);
    byte[] hash = hasher.hash(pw);
    assertTrue(hasher.verifyPW(pw, hash));
  }
}
