package ale.java_training.generics;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

import ale.java_training.generics.Printer.*;

public class AccountantTest {

  public void test_simple() {
    Accountant acc = new Accountant();
    HPLaserJet hp = acc.buy(HPLaserJet.class);
    Kyocera k = acc.buy(Kyocera.class);
    Brother br = acc.buy(Brother.class);
  }
  
  public void test_inconsistency() {
    Accountant acc = new Accountant();
    try {
      Method buy = Accountant.class.getDeclaredMethod("buy", Class.class);
      TypeVariable<Method> argType = buy.getTypeParameters()[0];
      assertFalse(argType.getBounds()[0].getTypeName().equals(Object.class.getName()), "Method buy() should not accept any class:");
      Type bound = argType.getBounds()[0];
      assertFalse(!(bound instanceof ParameterizedType), "Method buy() shoudn't accept any printer types but only paper one:");
      String typeName = ((ParameterizedType) bound).getActualTypeArguments()[0].getTypeName();
      assertTrue(typeName.matches(".*Paper.*"), "Method buy() should accept only paper printer types");
      assertTrue(typeName.matches("\\? extends .*Paper\\$A4"), "Method buy() should accept only office printer types (with A4 paper)");
    } catch (NoSuchMethodException ex) {
      fail("No buy() method found");
    }
  }
  
}
