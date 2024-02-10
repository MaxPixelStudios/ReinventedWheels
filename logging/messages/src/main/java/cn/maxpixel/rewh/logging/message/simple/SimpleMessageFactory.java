package cn.maxpixel.rewh.logging.message.simple;

import cn.maxpixel.rewh.logging.Level;
import cn.maxpixel.rewh.logging.Marker;
import cn.maxpixel.rewh.logging.msg.Message;
import cn.maxpixel.rewh.logging.msg.MessageFactory;

import java.util.Arrays;

public final class SimpleMessageFactory extends MessageFactory {
    @Override
    public Message create(Marker marker, StackTraceElement caller, Level level, long timestamp, String message) {
        return new SimpleMessage(marker, caller, level, timestamp, message, null, null);
    }

    @Override
    public Message create(Marker marker, StackTraceElement caller, Level level, long timestamp, String message, Object arg0) {
        boolean b = arg0 instanceof Throwable;
        return new SimpleMessage(marker, caller, level, timestamp, message, b ? null : new Object[] {arg0}, b ? (Throwable) arg0 : null);
    }

    @Override
    public Message create(Marker marker, StackTraceElement caller, Level level, long timestamp, String message, Object arg0, Object arg1) {
        boolean b = arg1 instanceof Throwable;
        return new SimpleMessage(marker, caller, level, timestamp, message, b ? new Object[] {arg0} : new Object[] {arg0, arg1}, b ? (Throwable) arg1 : null);
    }

    @Override
    public Message create(Marker marker, StackTraceElement caller, Level level, long timestamp, String message, Object arg0, Object arg1, Object arg2) {
        boolean b = arg2 instanceof Throwable;
        return new SimpleMessage(marker, caller, level, timestamp, message, b ? new Object[] {arg0, arg1} : new Object[] {arg0, arg1, arg2}, b ? (Throwable) arg2 : null);
    }

    @Override
    public Message create(Marker marker, StackTraceElement caller, Level level, long timestamp, String message, Object arg0, Object arg1, Object arg2, Object arg3) {
        boolean b = arg3 instanceof Throwable;
        return new SimpleMessage(marker, caller, level, timestamp, message, b ? new Object[] {arg0, arg1, arg2} : new Object[] {arg0, arg1, arg2, arg3}, b ? (Throwable) arg3 : null);
    }

    @Override
    public Message create(Marker marker, StackTraceElement caller, Level level, long timestamp, String message, Object arg0, Object arg1, Object arg2, Object arg3, Object arg4) {
        boolean b = arg4 instanceof Throwable;
        return new SimpleMessage(marker, caller, level, timestamp, message, b ? new Object[] {arg0, arg1, arg2, arg3} : new Object[] {arg0, arg1, arg2, arg3, arg4}, b ? (Throwable) arg4 : null);
    }

    @Override
    public Message create(Marker marker, StackTraceElement caller, Level level, long timestamp, String message, Object arg0, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5) {
        boolean b = arg5 instanceof Throwable;
        return new SimpleMessage(marker, caller, level, timestamp, message, b ? new Object[] {arg0, arg1, arg2, arg3, arg4} : new Object[] {arg0, arg1, arg2, arg3, arg4, arg5}, b ? (Throwable) arg5 : null);
    }

    @Override
    public Message create(Marker marker, StackTraceElement caller, Level level, long timestamp, String message, Object[] args) {
        int i = args.length - 1;
        boolean b = args[i] instanceof Throwable;
        return new SimpleMessage(marker, caller, level, timestamp, message, b ? Arrays.copyOf(args, i) : args, b ? (Throwable) args[i] : null);
    }
}