package cn.maxpixel.rewh.logging.msg;

import cn.maxpixel.rewh.logging.Config;
import cn.maxpixel.rewh.logging.Level;
import cn.maxpixel.rewh.logging.Marker;

import java.util.Iterator;

public interface Message {
    Marker getMarker();

    StackTraceElement getCaller();

    Level getLevel();

    long getTimestamp();

    String getMessage();

    Object getArg(int index);

    int getArgLength();

    Iterator<Object> getArgs();

    Throwable getThrowable();

    StringBuilder makeFormattedMessage(Config.Logger config);

    static void replaceParams(String msg, Object[] params, int paramCount, StringBuilder dest) {
        if (!msg.contains("{}") || paramCount <= 0) dest.append(msg);
        if (msg.equals("{}")) dest.append(params[0]);
        if (msg.equals("\\{}")) dest.append("{}");
        int previousPointer = 0;
        for (int pointer = msg.indexOf("{}"), paramIndex = 0;
             pointer < msg.length() && pointer != -1;
             pointer = msg.indexOf("{}", previousPointer)) {
            if (paramIndex == paramCount) dest.append(msg, previousPointer, msg.length());
            if (msg.charAt(pointer - 1) == '\\') {
                dest.append(msg, previousPointer, pointer - 1);
                if (msg.charAt(pointer - 2) == '\\') dest.append(params[paramIndex++]);
                else dest.append("{}");
            } else {
                dest.append(msg, previousPointer, pointer).append(params[paramIndex++]);
            }
            previousPointer = pointer + 2;
        }
        dest.append(msg, previousPointer, msg.length());
    }
}