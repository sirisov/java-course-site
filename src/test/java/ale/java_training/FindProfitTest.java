package ale.java_training;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

@Test(timeOut = 5_000)
public class FindProfitTest {
  
  public void test() {
    assertEquals(HackUtils.profitOf(new int[]{0,1,2,3,4,5}, 0, 0), 0);
    assertEquals(HackUtils.profitOf(new int[]{0,1,2,3,4,5}, 0, 1), 1);
    assertEquals(HackUtils.profitOf(new int[]{0,1,2,3,4,5}, 0, 5), 15);
    assertEquals(HackUtils.profitOf(new int[]{0,1,2,3,4,5}, 3, 5), 12);
  }

}
