package ale.java_training;

import static org.testng.Assert.assertEquals;

public class MinMaxTest {
  
  public void test_min() {
    assertEquals(AccountSearcher.poorest(new int[]{0,1,2,3,4,5}), 0);
    assertEquals(AccountSearcher.poorest(new int[]{1,3,0,9,2,1}), 0);
    assertEquals(AccountSearcher.poorest(new int[]{5,4,3,2,1,0}), 0);
    assertEquals(AccountSearcher.poorest(new int[]{0,0,0,0,0}), 0);
    assertEquals(AccountSearcher.poorest(new int[]{0}), 0);
    assertEquals(AccountSearcher.poorest(new int[]{}), Integer.MAX_VALUE);
  }
  
  public void test_max() {
    assertEquals(AccountSearcher.richest(new int[]{3,1,2,9,4,5}), 9);
    assertEquals(AccountSearcher.richest(new int[]{0,1,2,3,4,5}), 5);
    assertEquals(AccountSearcher.richest(new int[]{5,4,3,2,1,0}), 5);
    assertEquals(AccountSearcher.richest(new int[]{0,0,0,0,0}), 0);
    assertEquals(AccountSearcher.richest(new int[]{0}), 0);
    assertEquals(AccountSearcher.richest(new int[]{}), Integer.MIN_VALUE);
  }

}
