package cn.maxpixel.rewh.logging.msg;

import cn.maxpixel.rewh.logging.Config;
import cn.maxpixel.rewh.logging.Level;
import cn.maxpixel.rewh.logging.Marker;
import it.unimi.dsi.fastutil.objects.ObjectIterators;

import java.text.MessageFormat;
import java.time.ZonedDateTime;
import java.util.Iterator;
import java.util.Objects;

public final class SimpleMessage implements Message {
    private final Marker marker;
    private final StackTraceElement caller;
    private final Level level;
    private final ZonedDateTime timestamp;
    private final String message;
    private final Object[] args;
    private final Throwable throwable;

    private String formatted;

    public SimpleMessage(Marker marker, StackTraceElement caller, Level level, ZonedDateTime timestamp, String message, Object[] args, Throwable throwable) {
        this.marker = marker;
        this.caller = Objects.requireNonNull(caller);
        this.level = Objects.requireNonNull(level);
        this.timestamp = timestamp;
        this.message = Objects.requireNonNull(message);
        this.args = args;
        this.throwable = throwable;
    }

    @Override
    public Marker getMarker() {
        return marker;
    }

    @Override
    public StackTraceElement getCaller() {
        return caller;
    }

    @Override
    public Level getLevel() {
        return level;
    }

    @Override
    public ZonedDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public Object getArg(int index) {
        return args[index];
    }

    @Override
    public int getArgLength() {
        return args.length;
    }

    @Override
    public Iterator<Object> getArgs() {
        return ObjectIterators.wrap(args);
    }

    @Override
    public Throwable getThrowable() {
        return throwable;
    }

    @Override
    public String makeFormattedMessage(Config.Logger config) {
        if (formatted == null) {
            if (args.length > 0) {
                String replaced = Message.replaceParams(message, args, args.length);
                if (config.messageFormat) replaced = MessageFormat.format(replaced, args);
                if (config.stringFormat) replaced = String.format(replaced, args);
                this.formatted = replaced;
            } else this.formatted = message;
        }
        return formatted;
    }
}