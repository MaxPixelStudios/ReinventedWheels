package cn.maxpixel.rewh.logging.msg;

import cn.maxpixel.rewh.logging.Level;
import cn.maxpixel.rewh.logging.Marker;

import java.time.ZonedDateTime;

/**
 * A factory of ReusableMessage
 */
public class ReusableMessageFactory extends MessageFactory {
    private final ThreadLocal<ReusableMessage> MSG_HOLDER = ThreadLocal.withInitial(ReusableMessage::new);

    private ReusableMessage getMessage(Marker marker, StackTraceElement caller, Level level, ZonedDateTime timestamp, String message) {
        ReusableMessage msg = MSG_HOLDER.get();
        if(!msg.ready) msg = new ReusableMessage();// Why this isn't ready?
        return msg.init(marker, caller, level, timestamp, message);
    }

    @Override
    public Message create(Marker marker, StackTraceElement caller, Level level, ZonedDateTime timestamp, String message) {
        return getMessage(marker, caller, level, timestamp, message);
    }

    @Override
    public Message create(Marker marker, StackTraceElement caller, Level level, ZonedDateTime timestamp, String message, Object arg0) {
        return getMessage(marker, caller, level, timestamp, message).withParams(arg0);
    }

    @Override
    public Message create(Marker marker, StackTraceElement caller, Level level, ZonedDateTime timestamp, String message, Object arg0, Object arg1) {
        return getMessage(marker, caller, level, timestamp, message).withParams(arg0, arg1);
    }

    @Override
    public Message create(Marker marker, StackTraceElement caller, Level level, ZonedDateTime timestamp, String message, Object arg0, Object arg1, Object arg2) {
        return getMessage(marker, caller, level, timestamp, message).withParams(arg0, arg1, arg2);
    }

    @Override
    public Message create(Marker marker, StackTraceElement caller, Level level, ZonedDateTime timestamp, String message, Object arg0, Object arg1, Object arg2, Object arg3) {
        return getMessage(marker, caller, level, timestamp, message).withParams(arg0, arg1, arg2, arg3);
    }

    @Override
    public Message create(Marker marker, StackTraceElement caller, Level level, ZonedDateTime timestamp, String message, Object arg0, Object arg1, Object arg2, Object arg3, Object arg4) {
        return getMessage(marker, caller, level, timestamp, message).withParams(arg0, arg1, arg2, arg3, arg4);
    }

    @Override
    public Message create(Marker marker, StackTraceElement caller, Level level, ZonedDateTime timestamp, String message, Object arg0, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5) {
        return getMessage(marker, caller, level, timestamp, message).withParams(arg0, arg1, arg2, arg3, arg4, arg5);
    }

    @Override
    public Message create(Marker marker, StackTraceElement caller, Level level, ZonedDateTime timestamp, String message, Object[] args) {
        return getMessage(marker, caller, level, timestamp, message).withParams(args);
    }
}