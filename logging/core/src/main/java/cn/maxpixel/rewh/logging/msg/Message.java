package cn.maxpixel.rewh.logging.msg;

import cn.maxpixel.rewh.logging.Level;
import cn.maxpixel.rewh.logging.Marker;
import cn.maxpixel.rewh.logging.config.LoggerConfig;

import java.time.Instant;
import java.util.Iterator;

public interface Message {
    Marker getMarker();

    StackTraceElement getCaller();

    Level getLevel();

    Instant getInstant();

    String getMessage();

    Object getArg(int index);

    int getArgLength();

    Iterator<Object> getArgs();

    Throwable getThrowable();

    String makeFormattedMessage(LoggerConfig config);

    static String replaceParams(String msg, Object[] params, int paramCount) {
        if (!msg.contains("{}") || paramCount <= 0) return msg;
        if (msg.equals("{}")) return params[0].toString();
        if (msg.equals("\\{}")) return "{}";
        StringBuilder sb = new StringBuilder();
        for (int pointer = msg.indexOf("{}"), paramIndex = 0, previousPointer = 0;
             pointer < msg.length() && pointer != -1;
             pointer = msg.indexOf("{}", previousPointer)) {
            if (paramIndex == paramCount) return sb.append(msg, previousPointer, msg.length()).toString();
            if (msg.charAt(pointer - 1) == '\\') {
                sb.append(msg, previousPointer, pointer - 1);
                if (msg.charAt(pointer - 2) == '\\') sb.append(params[paramIndex++]);
                else sb.append('{').append('}');
            } else {
                sb.append(msg, previousPointer, pointer);
                sb.append(params[paramIndex++]);
            }
            previousPointer = pointer + 2;
        }
        return sb.toString();
    }
}