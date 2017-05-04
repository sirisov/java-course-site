package ale.java_training.classes;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.fail;

import org.testng.annotations.Test;

public class T1000Test {

  @Test
  public void test() {
    Class<?> t1000c = T1000.class;
    try {
      t1000c.getConstructor();
    } catch (NoSuchMethodException | SecurityException ex) {
      fail("No no-arg constructor", ex);
    }
    T1000 t1000 = new T1000();
    assertFalse(t1000.isSmiling(), "T1000 should be born without smile");
    t1000.setSmile(true);
    assertFalse(t1000.isSmiling(), "T1000 should never smile");
  }
  
}
