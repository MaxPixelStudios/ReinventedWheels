package cn.maxpixel.rewh.logging.reuse;

import cn.maxpixel.rewh.logging.Level;
import cn.maxpixel.rewh.logging.Marker;
import cn.maxpixel.rewh.logging.msg.Message;
import cn.maxpixel.rewh.logging.msg.MessageFactory;

import java.time.Instant;

/**
 * A factory of ReusableMessage
 */
public class ReusableMessageFactory extends MessageFactory {
    private static final ThreadLocal<ReusableMessage> MSG_HOLDER = ThreadLocal.withInitial(ReusableMessage::new);

    public static ReusableMessage getMessage(Marker marker, StackTraceElement caller, Level level, Instant instant, String message) {
        ReusableMessage msg = MSG_HOLDER.get();
        if(!msg.ready) msg = new ReusableMessage();// Why this isn't ready?
        return msg.init(marker, caller, level, instant, message);
    }

    @Override
    public Message create(Marker marker, StackTraceElement caller, Level level, Instant instant, String message) {
        return getMessage(marker, caller, level, instant, message);
    }

    @Override
    public Message create(Marker marker, StackTraceElement caller, Level level, Instant instant, String message, Object arg0) {
        return getMessage(marker, caller, level, instant, message).withParams(arg0);
    }

    @Override
    public Message create(Marker marker, StackTraceElement caller, Level level, Instant instant, String message, Object arg0, Object arg1) {
        return getMessage(marker, caller, level, instant, message).withParams(arg0, arg1);
    }

    @Override
    public Message create(Marker marker, StackTraceElement caller, Level level, Instant instant, String message, Object arg0, Object arg1, Object arg2) {
        return getMessage(marker, caller, level, instant, message).withParams(arg0, arg1, arg2);
    }

    @Override
    public Message create(Marker marker, StackTraceElement caller, Level level, Instant instant, String message, Object arg0, Object arg1, Object arg2, Object arg3) {
        return getMessage(marker, caller, level, instant, message).withParams(arg0, arg1, arg2, arg3);
    }

    @Override
    public Message create(Marker marker, StackTraceElement caller, Level level, Instant instant, String message, Object arg0, Object arg1, Object arg2, Object arg3, Object arg4) {
        return getMessage(marker, caller, level, instant, message).withParams(arg0, arg1, arg2, arg3, arg4);
    }

    @Override
    public Message create(Marker marker, StackTraceElement caller, Level level, Instant instant, String message, Object arg0, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5) {
        return getMessage(marker, caller, level, instant, message).withParams(arg0, arg1, arg2, arg3, arg4, arg5);
    }

    @Override
    public Message create(Marker marker, StackTraceElement caller, Level level, Instant instant, String message, Object[] args) {
        return getMessage(marker, caller, level, instant, message).withParams(args);
    }
}