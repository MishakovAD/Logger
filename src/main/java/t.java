import logger.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@Logger
@Lazy
public class t implements tt {

    @PostConstruct
    public void init() {
        System.out.println("Init-method t");
    }

    public t() {
        System.out.println("Constructor t");
    }

    public t(String message) {
        System.out.println("Constructor t with param " + message);
    }

    @Override
    public void doSomething() {
        System.out.println("method doSomething");
    }

    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("config.xml");
        tt t = context.getBean(tt.class);
        t.doSomething();

    }
}
