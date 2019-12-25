package BeanPostProcessor;

import Controller.LogLevel;
import Controller.LogType;
import Controller.LoggerController;
import logger.Logger;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Component
public class LoggerHandlerBeanPostProcessor implements BeanPostProcessor {
  Map<String, Class> annotatedBeans = new HashMap<String, Class>();
  LoggerController loggerController;

  public LoggerHandlerBeanPostProcessor() {
    MBeanServer platformMBeanServer = ManagementFactory.getPlatformMBeanServer();
    loggerController = new LoggerController();
    try {
      platformMBeanServer.registerMBean(loggerController, new ObjectName("logger", "name" ,"loggerController"));
    } catch (InstanceAlreadyExistsException e) {
      e.printStackTrace();
    } catch (MBeanRegistrationException e) {
      e.printStackTrace();
    } catch (NotCompliantMBeanException e) {
      e.printStackTrace();
    } catch (MalformedObjectNameException e) {
      e.printStackTrace();
    }
  }

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
        long startTime = System.currentTimeMillis();
        Object invokeMethod = method.invoke(bean, args);
        long endTime = System.currentTimeMillis();
        String endLog = getEndLog(annotatedBean, method, args);
        StringBuilder report = new StringBuilder(endLog);
        report.append(" {TOTAL TIME} : " + (endTime - startTime) + "ms.");
        String log;
        if ("void".equals(method.getReturnType().getName())) {
          report.append(" RESULT = void method");
          log = report.toString();
        } else {
          if (invokeMethod != null) {
            report.append(" RESULT = " + invokeMethod);
            log = report.toString();
          } else {
            report.append(" RESULT = NULL");
            log = report.toString().replace("[DEBUG]  ", "[WARNING]");
          }
        }
        log(bean, startLog, log);
        return invokeMethod;
      });
    }
    return bean;
  }

  private void log(Object bean, String startLog, String log) {
    LogLevel level = bean.getClass().getAnnotation(Logger.class).level(); //TODO: придумать что нибудь с урвонем логирования.
    LogType type = bean.getClass().getAnnotation(Logger.class).type();
    String filePath = bean.getClass().getAnnotation(Logger.class).filePath();
    if (loggerController.isEnabled()) {
      switch (type) {
        case CONSOLE:
          System.out.println(startLog);
          System.out.println(log);
          break;
        case FILE:
          writeLogToFile(filePath, startLog, log);
          break;
        case ALL:
          System.out.println(startLog);
          System.out.println(log);
          writeLogToFile(filePath, startLog, log);
          break;
      }
    }
  }

  private void writeLogToFile(String filePath, String startLog, String endLog) {
    try(FileWriter writer = new FileWriter(filePath + "log.txt", true))
    {
      writer.write(startLog + "\n");
      writer.write(endLog + "\n");
      writer.flush();
    }
    catch(IOException ex){
      System.out.println(ex.getMessage());
    }
  }

  private String getStartLog(Class bean, Method method, Object[] args) {
    if (bean == null || method == null) {
      return "getStartLog() - Arguments is null.";
    }
    StringBuilder log = new StringBuilder();
    String methodName = method.getName();
    String className = bean.getName();
    DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-mm-dd HH:mm:ss");
    log.append("[DEBUG]   {START} " + LocalDateTime.now().format(f) + " " + className + "." + methodName + "() >>>> [" + methodName + "(");
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
    if (bean == null || method == null) {
      return "getEndLog() - Arguments is null.";
    }
    StringBuilder log = new StringBuilder();
    String methodName = method.getName();
    String className = bean.getName();
    DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-mm-dd HH:mm:ss");
    log.append("[DEBUG]   {END}   " + LocalDateTime.now().format(f) + " " + className + "." + methodName + "() >>>>");
    return log.toString();
  }
}
