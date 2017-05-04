package ale.java_training.classes;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class RobotTest {

  private PrintStream ps;

  @BeforeClass
  public void savePS() {
    ps = System.out;
  }
  
  @Test
  public void stop() {
    fail("Tests are not yet ready");
  }
  
  //@Test
  public void test_simple() throws IOException {
    try (ByteArrayOutputStream os = new ByteArrayOutputStream(); PrintStream sps = new PrintStream(os)) {
      System.setOut(sps);
      Robot r = new FancyRobot();
      r.queueCommand(FancyRobot.Commands.sing("We are the robots"));
      r.queueCommand(FancyRobot.Commands.dance(FancyRobot.Movement.RIGHT));
      r.queueCommand(FancyRobot.Commands.dance(FancyRobot.Movement.LEFT));
      r.queueCommand(FancyRobot.Commands.dance(FancyRobot.Movement.UP));
      r.queueCommand(FancyRobot.Commands.work());
      r.queueCommand(FancyRobot.Commands.serve());
      r.execute();
      sps.flush();
      assertEquals(os.toString(), "We are the robots\nd[(0)(0)]b -->\n<-- d[(0)(0)]b\n\\[(0)(0)]/\nJa tvoi rabotnik\nJa tvoi sluga\n");
    } 
  }
  
  @AfterClass
  public void restorePS() {
    System.setOut(ps);
  }
  
}
