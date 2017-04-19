package ale.java_training;


import static org.testng.Assert.assertEquals;

import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.testng.annotations.Test;

@Test(timeOut = 5_000)
public class CountHackableTest {
  
  public void countHackableTest_simple() {
    Boolean[] hackable = {true, false, true, false};
    assertEquals(HackUtils.countHackable(hackable), 2);
  } 
  
  public void countHackableTest_single_true() {
    assertEquals(HackUtils.countHackable(new Boolean[] {true}), 1);
  }
  
  public void countHackableTest_single_false() {
    assertEquals(HackUtils.countHackable(new Boolean[] {false}), 0);
  } 
  
  public void countHackableTest_empty() {
    assertEquals(HackUtils.countHackable(new Boolean[] {}), 0);
  } 
  
  public void countHackableTest_random() {
    Random random = new Random();
    IntStream.range(0, 10)
             .mapToObj(i -> Stream.generate(() -> random.nextBoolean()).limit(i).toArray(Boolean[]::new))
             .forEach(a -> assertEquals(HackUtils.countHackable(a), Stream.of(a).filter(Boolean::booleanValue).count()));
  } 

}
