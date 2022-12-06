package cn.maxpixel.rewh.logging.msg.filter;

import cn.maxpixel.rewh.logging.msg.Message;

public interface Filter {
    boolean isLoggable(Message msg);
}