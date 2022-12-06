package cn.maxpixel.rewh.logging.simple;

import cn.maxpixel.rewh.logging.Level;
import cn.maxpixel.rewh.logging.Marker;
import cn.maxpixel.rewh.logging.config.LoggerConfig;
import cn.maxpixel.rewh.logging.msg.Message;
import it.unimi.dsi.fastutil.objects.ObjectIterators;

import java.text.MessageFormat;
import java.time.Instant;
import java.util.Iterator;
import java.util.Objects;

public final class SimpleMessage implements Message {
    private final Marker marker;
    private final StackTraceElement caller;
    private final Level level;
    private final Instant instant;
    private final String message;
    private final Object[] args;
    private final Throwable throwable;

    private String formatted;

    public SimpleMessage(Marker marker, StackTraceElement caller, Level level, Instant instant, String message, Object[] args, Throwable throwable) {
        this.marker = marker;
        this.caller = Objects.requireNonNull(caller);
        this.level = Objects.requireNonNull(level);
        this.instant = instant;
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
    public Instant getInstant() {
        return instant;
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
    public String makeFormattedMessage(LoggerConfig config) {
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