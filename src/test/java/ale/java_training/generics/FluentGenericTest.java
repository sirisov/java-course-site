package ale.java_training.generics;

import static org.mockito.Mockito.when;
import static org.testng.Assert.assertFalse;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

import org.mockito.Mockito;
import org.testng.annotations.Test;

import ale.java_training.generics.BaseFluent.*;

public class FluentGenericTest {

  public void test_simple() {
    ChildFluent cf = Mockito.mock(ChildFluent.class);
    when(cf.first()).thenReturn(cf);
    when(cf.second()).thenReturn(cf);
    cf.first().second();
    cf.first().second().first().first().second().second();
  }
  
  @Test(dependsOnMethods = "test_simple")
  public void test_inconsistency() {
    Class<?> cl = BaseFluent.class;
    TypeVariable<? extends Class<?>> tp = cl.getTypeParameters()[0];
    Type bound = tp.getBounds()[0];
    assertFalse(bound.getTypeName().equals(Object.class.getTypeName()), "Generic type of BaseFluent should be restricted to BaseFluent and it's successor:");
    assertFalse(!(bound instanceof ParameterizedType), "Parameterized class shouldn't be used as raw type:");
  }
  
}
