import javax.annotation.PostConstruct;
import logger.Logger;

@Logger
public class Test implements Testing {

  @PostConstruct
  public void init() {

    System.out.println("Init method");
  }

  public Test() {
    System.out.println("Constructor");
  }

  @Override
  public void test(String message) {
    //System.out.println("Testing 1" + message);
  }

  @Override
  public void test2(double a1, int a2, String mes) {

    //System.out.println("testing 2");
  }

}
