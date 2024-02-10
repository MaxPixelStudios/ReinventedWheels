package cn.maxpixel.rewh.logging.msg;

import cn.maxpixel.rewh.logging.Config;
import cn.maxpixel.rewh.logging.Level;
import cn.maxpixel.rewh.logging.LogManager;
import cn.maxpixel.rewh.logging.Marker;

public abstract class MessageFactory {
    private static MessageFactory current;

    public static MessageFactory get() {
        return current;
    }

    public static synchronized void reload() {
        try {
            current = (MessageFactory) Class.forName(Config.get().messageFactory).getConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            current = new ReusableMessageFactory();
            LogManager.getLogger().fatal("Error loading the message factory: {}. It must be a valid class and have a no-arg public constructor", Config.get().messageFactory, e);
        }
    }

    public abstract Message create(Marker marker, StackTraceElement caller, Level level, long timestamp, String message);
    public abstract Message create(Marker marker, StackTraceElement caller, Level level, long timestamp, String message, Object arg0);
    public abstract Message create(Marker marker, StackTraceElement caller, Level level, long timestamp, String message, Object arg0, Object arg1);
    public abstract Message create(Marker marker, StackTraceElement caller, Level level, long timestamp, String message, Object arg0, Object arg1, Object arg2);
    public abstract Message create(Marker marker, StackTraceElement caller, Level level, long timestamp, String message, Object arg0, Object arg1, Object arg2, Object arg3);
    public abstract Message create(Marker marker, StackTraceElement caller, Level level, long timestamp, String message, Object arg0, Object arg1, Object arg2, Object arg3, Object arg4);
    public abstract Message create(Marker marker, StackTraceElement caller, Level level, long timestamp, String message, Object arg0, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5);
    public abstract Message create(Marker marker, StackTraceElement caller, Level level, long timestamp, String message, Object[] args);
}