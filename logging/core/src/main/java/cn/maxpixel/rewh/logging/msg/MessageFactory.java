package cn.maxpixel.rewh.logging.msg;

import cn.maxpixel.rewh.logging.Level;
import cn.maxpixel.rewh.logging.Marker;

import java.time.Instant;
import java.util.ServiceLoader;

public abstract class MessageFactory {
    private static final ServiceLoader<MessageFactory> serviceLoader = ServiceLoader.load(MessageFactory.class);
    private static MessageFactory current = serviceLoader.iterator().next();

    public static MessageFactory get() {
        return current;
    }

    public static synchronized void reload() {
        serviceLoader.reload();
        current = serviceLoader.iterator().next();
    }

    public abstract Message create(Marker marker, StackTraceElement caller, Level level, Instant instant, String message);
    public abstract Message create(Marker marker, StackTraceElement caller, Level level, Instant instant, String message, Object arg0);
    public abstract Message create(Marker marker, StackTraceElement caller, Level level, Instant instant, String message, Object arg0, Object arg1);
    public abstract Message create(Marker marker, StackTraceElement caller, Level level, Instant instant, String message, Object arg0, Object arg1, Object arg2);
    public abstract Message create(Marker marker, StackTraceElement caller, Level level, Instant instant, String message, Object arg0, Object arg1, Object arg2, Object arg3);
    public abstract Message create(Marker marker, StackTraceElement caller, Level level, Instant instant, String message, Object arg0, Object arg1, Object arg2, Object arg3, Object arg4);
    public abstract Message create(Marker marker, StackTraceElement caller, Level level, Instant instant, String message, Object arg0, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5);
    public abstract Message create(Marker marker, StackTraceElement caller, Level level, Instant instant, String message, Object[] args);
}