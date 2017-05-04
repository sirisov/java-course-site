package ale.java_training;

import static org.testng.Assert.assertTrue;

import java.util.Objects;

import org.testng.annotations.Test;

@Test(timeOut = 5_000)
public class StrongPasswordTest {
  
  public void test() {
    assertTrue(Objects.requireNonNull(HackUtils.makeStronger("qwerty"), "null is not expected").matches("Qwerty\\d"));
    assertTrue(Objects.requireNonNull(HackUtils.makeStronger("Qwerty"), "null is not expected").matches("Qwerty\\d"));
    assertTrue(Objects.requireNonNull(HackUtils.makeStronger(""), "null is not expected").matches("\\d"));
    assertTrue(Objects.requireNonNull(HackUtils.makeStronger("asd"), "null is not expected").matches("Asd\\d"));
  }

}
