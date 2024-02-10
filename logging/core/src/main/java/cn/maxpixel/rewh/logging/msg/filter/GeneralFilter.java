package cn.maxpixel.rewh.logging.msg.filter;

import cn.maxpixel.rewh.logging.Config;
import cn.maxpixel.rewh.logging.Logger;
import cn.maxpixel.rewh.logging.msg.Message;

public class GeneralFilter implements Filter {// TODO
    @Override
    public boolean isLoggable(Logger logger, Config.Logger config, Message msg) {
        return false;
    }
}