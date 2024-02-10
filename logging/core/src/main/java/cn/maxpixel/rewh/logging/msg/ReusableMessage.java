package cn.maxpixel.rewh.logging.msg;

import cn.maxpixel.rewh.logging.Config;
import cn.maxpixel.rewh.logging.Level;
import cn.maxpixel.rewh.logging.Marker;
import cn.maxpixel.rewh.logging.util.Reusable;
import it.unimi.dsi.fastutil.objects.ObjectIterators;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Iterator;

public final class ReusableMessage implements Message, Reusable {
    private Marker marker;
    private StackTraceElement caller;
    private Level level;
    private long timestamp;
    private String message;
    private final Object[] args = new Object[5];
    private int argLength;
    private Object[] arguments;
    private Throwable throwable;

    boolean ready = true;
    private boolean didFormat;
    private final StringBuilder formatted = new StringBuilder();

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
        if (index >= argLength) throw new IndexOutOfBoundsException();
        if (arguments != null) return arguments[index];
        return args[index];
    }

    @Override
    public int getArgLength() {
        return Math.max(0, argLength);
    }

    @Override
    public Iterator<Object> getArgs() {
        return argLength <= 0 ? ObjectIterators.emptyIterator() : (arguments != null ?
                ObjectIterators.wrap(arguments, 0, argLength) :
                ObjectIterators.wrap(args, 0, argLength));
    }

    @Override
    public Throwable getThrowable() {
        return throwable;
    }

    @Override
    public StringBuilder makeFormattedMessage(Config.Logger config) {
        if (!didFormat) {
            formatted.setLength(0);
            Message.replaceParams(message, arguments == null ? args : arguments, argLength, formatted);
            if (argLength > 0 && (config.messageFormat || config.stringFormat)) {// FIXME: trash design
                Object[] objs = arguments == null ? Arrays.copyOf(args, argLength) : arguments;
                String replaced = formatted.toString();
                if (config.messageFormat) replaced = MessageFormat.format(replaced, objs);
                if (config.stringFormat) replaced = String.format(replaced, objs);
                formatted.setLength(0);
                formatted.append(replaced);
            }
            this.didFormat = true;
        }
        return formatted;
    }

    @Override
    public void ready() {
        this.argLength = -1;
        this.arguments = null;
        this.didFormat = false;
        this.ready = true;
    }

    ReusableMessage init(Marker marker, StackTraceElement caller, Level level, long timestamp, String message) {
        if (!ready) throw new IllegalStateException("Message is not ready");
        this.ready = false;
        this.marker = marker;
        this.caller = caller;
        this.level = level;
        this.timestamp = timestamp;
        this.message = message;
        return this;
    }

    ReusableMessage withParams(Object arg0) {
        if (ready) throw new IllegalStateException("Message not initialized yet");
        if (arg0 instanceof Throwable) {
            this.throwable = (Throwable) arg0;
        } else {
            this.args[0] = arg0;
            this.argLength = 1;
        }
        return this;
    }

    ReusableMessage withParams(Object arg0, Object arg1) {
        if (ready) throw new IllegalStateException("Message not initialized yet");
        this.args[0] = arg0;
        if (arg1 instanceof Throwable) {
            this.throwable = (Throwable) arg1;
            this.argLength = 1;
        } else {
            this.args[1] = arg1;
            this.argLength = 2;
        }
        return this;
    }

    ReusableMessage withParams(Object arg0, Object arg1, Object arg2) {
        if (ready) throw new IllegalStateException("Message not initialized yet");
        this.args[0] = arg0;
        this.args[1] = arg1;
        if (arg2 instanceof Throwable) {
            this.throwable = (Throwable) arg2;
            this.argLength = 2;
        } else {
            this.args[2] = arg2;
            this.argLength = 3;
        }
        return this;
    }

    ReusableMessage withParams(Object arg0, Object arg1, Object arg2, Object arg3) {
        if (ready) throw new IllegalStateException("Message not initialized yet");
        this.args[0] = arg0;
        this.args[1] = arg1;
        this.args[2] = arg2;
        if (arg3 instanceof Throwable) {
            this.throwable = (Throwable) arg3;
            this.argLength = 3;
        } else {
            this.args[3] = arg3;
            this.argLength = 4;
        }
        return this;
    }

    ReusableMessage withParams(Object arg0, Object arg1, Object arg2, Object arg3, Object arg4) {
        if (ready) throw new IllegalStateException("Message not initialized yet");
        this.args[0] = arg0;
        this.args[1] = arg1;
        this.args[2] = arg2;
        this.args[3] = arg3;
        if (arg4 instanceof Throwable) {
            this.throwable = (Throwable) arg4;
            this.argLength = 4;
        } else {
            this.args[4] = arg4;
            this.argLength = 5;
        }
        return this;
    }

    ReusableMessage withParams(Object arg0, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5) {
        if (ready) throw new IllegalStateException("Message not initialized yet");
        this.args[0] = arg0;
        this.args[1] = arg1;
        this.args[2] = arg2;
        this.args[3] = arg3;
        this.args[4] = arg4;
        if (arg5 instanceof Throwable) {
            this.throwable = (Throwable) arg5;
            this.argLength = 5;
        } else {
            this.args[5] = arg5;
            this.argLength = 6;
        }
        return this;
    }

    ReusableMessage withParams(Object[] arguments) {
        if (ready) throw new IllegalStateException("Message not initialized yet");
        this.arguments = arguments;
        if (arguments[arguments.length - 1] instanceof Throwable) {
            this.throwable = (Throwable) arguments[arguments.length - 1];
            this.argLength = arguments.length - 1;
        } else this.argLength = arguments.length;
        return this;
    }
}