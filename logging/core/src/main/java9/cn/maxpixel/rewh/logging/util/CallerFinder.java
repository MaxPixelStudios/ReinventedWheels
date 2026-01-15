package cn.maxpixel.rewh.logging.util;

import cn.maxpixel.rewh.logging.Logger;

public class CallerFinder {
    private static final StackWalker WALKER = StackWalker.getInstance();

    public static StackTraceElement findCaller(String name) {
        return WALKER.walk(stacks -> {
            return stacks.dropWhile(sf -> !sf.getClassName().equals(name))
                    .dropWhile(sf -> sf.getClassName().equals(name))
                    .findFirst()
                    .map(StackWalker.StackFrame::toStackTraceElement)
                    .orElse(Logger.UNKNOWN);// Unable to find
        });
    }
}