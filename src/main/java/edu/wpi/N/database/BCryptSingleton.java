package edu.wpi.N.database;

import at.favre.lib.crypto.bcrypt.*;
import java.nio.charset.StandardCharsets;

public class BCryptSingleton {
  private int cost;
  private static BCryptSingleton instance = null;

  /** Constructs a new BCryptSingleton with a cost of 10. */
  private BCryptSingleton() {
    this.cost = 10;
  }

  /**
   * Used to retrieve an instance of BCryptSingleton. The cost value defaults to 10, but can be
   * modified using the setCost function.
   *
   * @return An instance of BCryptSingleton
   */
  public static BCryptSingleton getInstance() {
    if (instance == null) instance = new BCryptSingleton();
    return instance;
  }

  /**
   * Sets the cost value to the given value.
   *
   * @param cost The new value for cost
   */
  public void setCost(int cost) {
    this.cost = cost;
  }

  /**
   * Creates a hash for a given password
   *
   * @param pw The password to hash
   * @return A byte array representing the hash
   */
  public byte[] hash(String pw) {
    return BCrypt.with(LongPasswordStrategies.hashSha512(BCrypt.Version.VERSION_2Y))
        .hash(cost, pw.getBytes(StandardCharsets.UTF_8));
  }

  /**
   * Verifies that a password matches a given hash
   *
   * @param pw The password to check
   * @param hash The hash to verify the password against
   * @return True if the password matches the hash
   * @throws IllegalBCryptFormatException If the given hash is not of a valid format
   */
  public boolean verifyPW(String pw, byte[] hash) throws IllegalBCryptFormatException {
    BCrypt.Result res =
        BCrypt.verifyer(
                BCrypt.Version.VERSION_2Y,
                LongPasswordStrategies.hashSha512(BCrypt.Version.VERSION_2Y))
            .verify(pw.getBytes(StandardCharsets.UTF_8), hash);
    if (!res.validFormat) throw new IllegalBCryptFormatException(res.formatErrorMessage);
    return res.verified;
  }
}
