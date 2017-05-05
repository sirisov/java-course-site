package ale.java_training.classes;


import static org.testng.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class RobotTest {

  private static String lf = System.lineSeparator();
  private PrintStream ps;

  @BeforeClass
  public void savePS() {
    ps = System.out;
  }
  
  private void assertRobot(Robot r, String expected, String message) throws IOException {
    try (ByteArrayOutputStream os = new ByteArrayOutputStream(); PrintStream sps = new PrintStream(os)) {
      System.setOut(sps);
      r.execute();
      sps.flush();
      assertEquals(os.toString(), expected, message);
    } 
  }
  
  private void assertRobot(Robot r, int n, String expected, String message) throws IOException {
    try (ByteArrayOutputStream os = new ByteArrayOutputStream(); PrintStream sps = new PrintStream(os)) {
      System.setOut(sps);
      r.execute(n);
      sps.flush();
      assertEquals(os.toString(), expected, message);
    } 
  }
  
  @Test
  public void test_simple() throws IOException {
    Robot r = new FancyRobot();
    r.queueCommand(FancyRobot.Commands.sing("We are the robots"));
    r.queueCommand(FancyRobot.Commands.dance(FancyRobot.Movement.RIGHT));
    r.queueCommand(FancyRobot.Commands.dance(FancyRobot.Movement.LEFT));
    r.queueCommand(FancyRobot.Commands.dance(FancyRobot.Movement.UP));
    r.queueCommand(FancyRobot.Commands.work());
    r.queueCommand(FancyRobot.Commands.serve());
    assertRobot(r, "We are the robots" + lf +
                                  "d[(0)(0)]b -->" + lf +
                                  "<-- d[(0)(0)]b" + lf +
                                  "\\[(0)(0)]/" + lf +
                                  "Ja tvoi rabotnik" + lf +
                                  "Ja tvoi sluga" + lf, null);
  }
  
  @Test
  public void test_clear() throws IOException {
    Robot r = new FancyRobot();
    r.queueCommand(FancyRobot.Commands.dance(FancyRobot.Movement.DOWN));
    assertRobot(r, "/[(0)(0)]\\" + lf, "FancyRobot.Movement.DOWN movement should be correct:");
    assertRobot(r, "", "Excecution should clear the queue:");
  }
  
  @Test
  public void test_limited() throws IOException {
    Robot r = new FancyRobot();
    r.queueCommand(FancyRobot.Commands.sing("We are the robots"));
    r.queueCommand(FancyRobot.Commands.work());
    r.queueCommand(FancyRobot.Commands.serve());
    r.queueCommand(FancyRobot.Commands.dance(FancyRobot.Movement.RIGHT));
    r.queueCommand(FancyRobot.Commands.dance(FancyRobot.Movement.LEFT));
    r.queueCommand(FancyRobot.Commands.dance(FancyRobot.Movement.UP));
    assertRobot(r, 1, "We are the robots" + lf, "First command only");
    assertRobot(r, 2, "Ja tvoi rabotnik" + lf +
                      "Ja tvoi sluga" + lf, "Two next commands");
    assertRobot(r, 10, "d[(0)(0)]b -->" + lf +
                   "<-- d[(0)(0)]b" + lf +
                   "\\[(0)(0)]/" + lf, "Rest of the commands");
    assertRobot(r, 1, "", "No more commands");
  }
  
  @Test
  public void test_mixed() throws IOException {
    Robot r = new FancyRobot();
    r.queueCommand(FancyRobot.Commands.sing("We are the robots"));
    r.queueCommand(FancyRobot.Commands.work());
    r.queueCommand(FancyRobot.Commands.serve());
    r.queueCommand(FancyRobot.Commands.dance(FancyRobot.Movement.RIGHT));
    r.queueCommand(FancyRobot.Commands.dance(FancyRobot.Movement.LEFT));
    r.queueCommand(FancyRobot.Commands.dance(FancyRobot.Movement.UP));
    assertRobot(r, 1, "We are the robots" + lf, "First command only");
    assertRobot(r, 2, "Ja tvoi rabotnik" + lf +
                      "Ja tvoi sluga" + lf, "Two next commands");
    assertRobot(r, "d[(0)(0)]b -->" + lf +
                   "<-- d[(0)(0)]b" + lf +
                   "\\[(0)(0)]/" + lf, "Rest of the commands");
    assertRobot(r, "", "No more commands");
  }
  
  @AfterClass
  public void restorePS() {
    System.setOut(ps);
  }
  
}
