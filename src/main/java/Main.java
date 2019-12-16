import logger.Log;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main {

  public static void main(String[] args) {
    ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("config.xml");
    context.getBean(Testing.class).test("hello");
    context.getBean(Testing.class).test2(1.54, 2, "test2");
  }
}
