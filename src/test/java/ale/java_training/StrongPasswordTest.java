package ale.java_training;

import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

@Test(timeOut = 5_000)
public class StrongPasswordTest {
  
  public void test() {
    assertTrue(HackUtils.makeStronger("qwerty").matches("Qwerty\\d"));
    assertTrue(HackUtils.makeStronger("Qwerty").matches("Qwerty\\d"));
    assertTrue(HackUtils.makeStronger("").matches("\\d"));
    assertTrue(HackUtils.makeStronger("asd").matches("Asd\\d"));
  }

}
