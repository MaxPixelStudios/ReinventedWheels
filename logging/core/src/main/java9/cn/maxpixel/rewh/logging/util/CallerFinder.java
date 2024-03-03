package cn.maxpixel.rewh.logging.util;

import cn.maxpixel.rewh.logging.Logger;

import java.util.Iterator;

public class CallerFinder {
    private static final StackWalker WALKER = StackWalker.getInstance();

    public static StackTraceElement findCaller(String name) {
        return WALKER.walk(stacks -> {
            Iterator<StackWalker.StackFrame> it = stacks.dropWhile(sf -> !sf.getClassName().equals(name)).iterator();
            while (it.hasNext()) {
                StackWalker.StackFrame stack = it.next();
                if (!stack.getClassName().equals(name)) {
                    return stack.toStackTraceElement();
                }
            }
            return Logger.UNKNOWN;// Unable to find
        });
    }
}