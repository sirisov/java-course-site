package ale.java_training;

import static org.testng.Assert.assertEquals;

import java.util.Random;
import java.util.stream.IntStream;

import org.testng.annotations.Test;

@Test(timeOut = 5_000)
public class MaximizeProfitTest {

  public static int profitOf(int[] accounts, int n) {
    int max = Integer.MIN_VALUE;
    for (int i = 0; i < accounts.length - n + 1; i++) {
      int sum = 0;
      for (int j = i; j < i + n; j++) {
        sum += accounts[j];
      }
      if (sum > max) {
        max = sum;
      }
    }
    return max;
  }
  
  public void test() {
    assertEquals(HackUtils.profitOf(new int[]{0,1,2,3,4,5}, 1), 5);
    assertEquals(HackUtils.profitOf(new int[]{0,1,2,3,4,5}, 2), 9);
    assertEquals(HackUtils.profitOf(new int[]{0,21,2,3,4,5}, 2), 23);
    assertEquals(HackUtils.profitOf(new int[]{0,1,2,3,4,5}, 3), 12);
    assertEquals(HackUtils.profitOf(new int[]{0,1,2,3,4,5}, 6), 15);
  }
  
  public void test_random() {
    Random random = new Random();
    IntStream.range(0, 10).mapToObj(i -> random.ints(10).toArray())
             .forEach(a -> {
               int n = random.nextInt(a.length);
               assertEquals(HackUtils.profitOf(a, n), profitOf(a, n));
             });
  }
  
}
