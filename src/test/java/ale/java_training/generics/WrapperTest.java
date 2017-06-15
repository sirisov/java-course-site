package ale.java_training.generics;

import static org.testng.Assert.assertEquals;

public class WrapperTest {

  public void test_string() {
    Wrapper.Wrap<String> stringWrap = Wrapper.wrap("hello");
    String hello = stringWrap.get();
    assertEquals(hello, "hello", "Wrap should returns wrapped object");
  }
  
  public void test_object() {
    Candy candy = new Candy();
    Wrapper.Wrap<Candy> candyWrap = Wrapper.wrap(candy);
    assertEquals(candyWrap.get(), candy, "Wrap should returns original object");
  }

  public static class Candy {
    
  }
  
}
