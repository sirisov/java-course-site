package ale.java_training.generics;

public class EnumTest {

  public void test_simple() {
    GenericEnum.getSomeEnum(Some.SomeEnum.FIRST);
    GenericEnum.getSomeEnum(Some.OtherEnum.THIRD);
  }
  
}
