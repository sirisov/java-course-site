package ale.java_training.classes;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testng.Assert.assertEquals;

import java.util.stream.Stream;

public class GhostTest {

  public void test_randomness() {
    assertThat(Stream.generate(Ghost::new).limit(10).map(Ghost::getShell).distinct().count()).as("There should be more than 1 ghost with different shells within 10").isGreaterThan(1);
  }
  
  public void test_single() {
    Ghost g = new Ghost();
    assertEquals(g.getShell(), g.getShell(), "The same ghost should have the same shell for a whole life");
    assertEquals(g.getShell(), g.getShell(), "The same ghost should have the same shell for a whole life");
    assertEquals(g.getShell(), g.getShell(), "The same ghost should have the same shell for a whole life");
    assertEquals(g.getShell(), g.getShell(), "The same ghost should have the same shell for a whole life");
    assertEquals(g.getShell(), g.getShell(), "The same ghost should have the same shell for a whole life");
    assertEquals(g.getShell(), g.getShell(), "The same ghost should have the same shell for a whole life");
    assertEquals(g.getShell(), g.getShell(), "The same ghost should have the same shell for a whole life");
    assertEquals(g.getShell(), g.getShell(), "The same ghost should have the same shell for a whole life");
  }
  
}
