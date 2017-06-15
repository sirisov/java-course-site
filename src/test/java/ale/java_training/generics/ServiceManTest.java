package ale.java_training.generics;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.fail;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;


public class ServiceManTest {

  public void test_simple() {
    ServiceMan sm = new ServiceMan();
    sm.repair(new Printer.HPLaserJet());
    sm.repair(new Printer.Epson());
    sm.repair(new Printer.Plotter());
  }
  
  public void test_inconsistency() {
    ServiceMan sm = new ServiceMan();
    try {
      Method rep = ServiceMan.class.getDeclaredMethod("repair", Printer.class);
      Type argType = rep.getGenericParameterTypes()[0];
      if (argType instanceof ParameterizedType) {
        String typeName = ((ParameterizedType) argType).getActualTypeArguments()[0].getTypeName();
        assertFalse(!typeName.matches("\\? extends .*Paper"), "Method repair() should accept only printers with paper material");
      } else {
        fail("Method repair() shoud accept argument with generic type only");
      }
    } catch (NoSuchMethodException ex) {
      fail("No repair() method found");
    }
  }
  
}
