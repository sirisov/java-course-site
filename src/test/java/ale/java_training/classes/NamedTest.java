package ale.java_training.classes;


import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.testng.annotations.Test;

public class NamedTest {

  @Test
  public void test_simple() {
    Named impl = Named.create("test");
    assertEquals(impl.getName(), "test", "Simple creation should work:");
  }
  
  @Test
  public void check_class() {
    Named impl = Named.create("test");
    assertEquals(impl.getClass().getDeclaredFields().length, 1, "SimpleNamed shouldn't have more than one field");
    Field field = impl.getClass().getDeclaredFields()[0];
    assertTrue((field.getModifiers() & Modifier.PRIVATE) == Modifier.PRIVATE, "Field " + field.getName() + " should be private");
    assertTrue((field.getModifiers() & Modifier.FINAL) == Modifier.FINAL, "Field " + field.getName() + " should be final");
  }
  
  @Test
  public void test_advanced() {
    assertEquals(Named.create("").getName(), "", "Should work for empty string:");
    assertEquals(Named.create(null).getName(), null, "Should work for null:");
  }
  
}
