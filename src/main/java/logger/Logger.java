package logger;

import Controller.LogLevel;
import Controller.LogType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Logger {
    LogLevel level() default LogLevel.DEBUG;
    LogType type() default LogType.CONSOLE;
    String filePath() default "";
}
