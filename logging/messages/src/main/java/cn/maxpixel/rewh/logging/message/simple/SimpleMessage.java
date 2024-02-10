package cn.maxpixel.rewh.logging.message.simple;

import cn.maxpixel.rewh.logging.Config;
import cn.maxpixel.rewh.logging.Level;
import cn.maxpixel.rewh.logging.Marker;
import cn.maxpixel.rewh.logging.msg.Message;
import it.unimi.dsi.fastutil.objects.ObjectIterators;

import java.text.MessageFormat;
import java.util.Iterator;
import java.util.Objects;

public final class SimpleMessage implements Message {
    private final Marker marker;
    private final StackTraceElement caller;
    private final Level level;
    private final long timestamp;
    private final String message;
    private final Object[] args;
    private final Throwable throwable;

    private final StringBuilder formatted = new StringBuilder();
    private boolean didFormat;

    public SimpleMessage(Marker marker, StackTraceElement caller, Level level, long timestamp, String message, Object[] args, Throwable throwable) {
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
    public long getTimestamp() {
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
    public StringBuilder makeFormattedMessage(Config.Logger config) {
        if (!didFormat) {
            Message.replaceParams(message, args, args.length, formatted);
            if (args.length > 0 && (config.messageFormat || config.stringFormat)) {
                String replaced = formatted.toString();
                if (config.messageFormat) replaced = MessageFormat.format(replaced, args);
                if (config.stringFormat) replaced = String.format(replaced, args);
                this.formatted.setLength(0);
                this.formatted.append(replaced);
            }
            this.didFormat = true;
        }
        return formatted;
    }
}