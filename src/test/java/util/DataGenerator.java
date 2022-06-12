package util;

import static java.lang.System.currentTimeMillis;
import static org.apache.commons.lang3.RandomUtils.nextInt;

import api.model.UserDto;

public abstract class DataGenerator {

  public static UserDto generateUserDto() {
    var name = generateLogin();
    var email = generateEmail();
    var password = "at_pwd";
    return new UserDto(name, email, password);
  }

  public static String generateLogin() {
    return "at_" + generate36RadixId();
  }

  public static String generateEmail() {
    return "at_" + generate36RadixId() + "@ya.ru";
  }

  public static String generate36RadixId() {
    return to36Radix(currentTimeMillis()) + to36Radix(nextInt(0, 36 * 36 * 36 * 36));
  }

  private static String to36Radix(long n) {
    return Long.toString(n, 36);
  }

}
