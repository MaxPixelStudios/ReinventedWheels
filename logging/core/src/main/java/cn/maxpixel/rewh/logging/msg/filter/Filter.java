package cn.maxpixel.rewh.logging.msg.filter;

import cn.maxpixel.rewh.logging.Config;
import cn.maxpixel.rewh.logging.Logger;
import cn.maxpixel.rewh.logging.msg.Message;

public interface Filter {
    boolean isLoggable(Logger logger, Config.Logger config, Message msg);
}