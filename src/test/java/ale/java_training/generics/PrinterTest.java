package ale.java_training.generics;

public class PrinterTest {

  public void test_simple() {
    Printer.Printer3D p3d = new Printer.Printer3D();
  	p3d.load(new Material.Plastic());
    
    Printer.PrinterPaper pp = new Printer.PrinterPaper();
  	pp.load(new Material.Paper());
  }
  
  public void test_generic() {
    Printer<Material.Plastic> p3d = new Printer.Printer3D();
  	p3d.load(new Material.Plastic());
    
    Printer<Material.Paper> pp = new Printer.PrinterPaper();
  	pp.load(new Material.Paper());
  }
  
}
