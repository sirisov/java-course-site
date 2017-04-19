package ale.java_training;

import static org.testng.Assert.assertEquals;

import java.util.Arrays;

import org.testng.annotations.Test;

@Test(timeOut = 5_000)
public class LogShufflerTest {
  
  public void test() {
    String[] original = {"a", "b", "c", "d", "e"};
    String[] shuffled = Arrays.copyOf(original, original.length);
    LogShuffler.shuffle(shuffled);
    assertEquals(shuffled.length, original.length, "Size of shuffled differs");
    if (Arrays.equals(shuffled, original)) {
      throw new AssertionError("You didn't shuffle the array");
    }
    if (!Arrays.asList(original).containsAll(Arrays.asList(shuffled))) {
      throw new AssertionError("Shuffled array doesn't contains the same elements as original");
    }
    String[] first = Arrays.copyOf(shuffled, shuffled.length);
    LogShuffler.shuffle(original);
    if (Arrays.equals(shuffled, first)) {
      throw new AssertionError("Shuffle should produce random order each run");
    }
  }
  
}
