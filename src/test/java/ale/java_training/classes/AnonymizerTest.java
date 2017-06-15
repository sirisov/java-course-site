package ale.java_training.classes;

import static org.testng.Assert.assertEquals;

public class AnonymizerTest {

  public void test() {
    Anonymizer dv = Anonymizer.pretendAs("Dart Vader");
    assertEquals(dv.getClass().getSimpleName(), "", "Not an anonymous class");
    assertEquals(dv.pretend(), "Dart Vader", "Pretend failed:");
    assertEquals(Anonymizer.pretendAs("").pretend(), "", "Pretend failed:");
    assertEquals(Anonymizer.pretendAs(null).pretend(), null, "Pretend failed:");
  }
  
}
