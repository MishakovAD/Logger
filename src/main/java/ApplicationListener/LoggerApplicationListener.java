package ApplicationListener;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

public class LoggerApplicationListener implements ApplicationListener<ContextRefreshedEvent> {

  public void onApplicationEvent(ContextRefreshedEvent event) {
    System.out.println("start onApplicationEvent");
    ApplicationContext context = event.getApplicationContext();
    String[] beanDefinitionNames = context.getBeanDefinitionNames();
    for (String beanDefinitionName : beanDefinitionNames) {
      //System.out.println(beanDefinitionName);
    }
  }
}
