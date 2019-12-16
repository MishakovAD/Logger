package BeanPostProcessor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.SQLOutput;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import logger.Logger;
import org.springframework.beans.factory.config.BeanPostProcessor;

public class LoggerHandlerBeanPostProcessor implements BeanPostProcessor {
  Map<String, Class> annotatedBeans = new HashMap<String, Class>();

  public Object postProcessBeforeInitialization(Object bean, String beanName) {
    Class<?> beanClass = bean.getClass();
    if (beanClass.isAnnotationPresent(Logger.class)) {
      System.out.println("Setup logging for class: " + beanName);
      annotatedBeans.put(beanName, beanClass);
    }
    return bean;
  }

  public Object postProcessAfterInitialization(Object bean, String beanName) {
    final Class annotatedBean = annotatedBeans.get(beanName);
    if (annotatedBean != null) {
      return Proxy.newProxyInstance(annotatedBean.getClassLoader(), annotatedBean.getInterfaces(), (proxy, method, args) -> {
        String startLog = getStartLog(annotatedBean, method, args);
        System.out.println(startLog);
        long startTime = System.currentTimeMillis();
        Object invokeMethod = method.invoke(bean, args);
        long endTime = System.currentTimeMillis();
        String endLog = getEndLog(annotatedBean, method, args);
        StringBuilder report = new StringBuilder(endLog);
        report.append(" {TOTAL TIME} : " + (endTime - startTime) + "ms.");
        System.out.println(report.toString());
        return invokeMethod;
      });
    }
    return bean;
  }

  private String getStartLog(Class bean, Method method, Object[] args) {
    StringBuilder log = new StringBuilder();
    String methodName = method.getName();
    DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-mm-dd ^ HH:mm:ss");
    log.append("[DEBUG] {START " + methodName + "()} " + LocalDateTime.now().format(f) + " >>>> [" + bean.getName() + ".class] >> [Method: " + methodName + "(");
    if (args != null) {
      for (int i = 0; i < args.length; i++) {
        if (args[i] instanceof String) {
          log.append("\"" + args[i] + "\"");
        } else {
          log.append(args[i]);
        }
        if (i != args.length-1) {
          log.append(", ");
        }
      }
    }
    log.append(")]");
    return log.toString();
  }

  private String getEndLog(Class bean, Method method, Object[] args) {
    StringBuilder log = new StringBuilder();
    String methodName = method.getName();
    DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-mm-dd HH:mm:ss");
    log.append("[DEBUG] {END " + methodName + "()} " + LocalDateTime.now().format(f) + " >>>> [" + bean.getName() + ".class] >> [Method: " + methodName + "()]");
    return log.toString();
  }
}
