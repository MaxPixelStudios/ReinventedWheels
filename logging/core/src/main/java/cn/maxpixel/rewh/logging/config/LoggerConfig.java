package cn.maxpixel.rewh.logging.config;

import cn.maxpixel.rewh.logging.Level;
import cn.maxpixel.rewh.logging.msg.filter.Filter;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.annotations.Since;

public final class LoggerConfig {
    @Since(0.1)
    @Expose
    public Formatter formatter = Formatter.DEFAULT;

    @Since(0.1)
    @Expose
    public Level level = Level.INFO;

    @Since(0.1)
    @SerializedName("enable_string_format")
    @Expose
    public boolean stringFormat = false; // String.format

    @Since(0.1)
    @SerializedName("enable_message_format")
    @Expose
    public boolean messageFormat = false; // MessageFormat.format

    @Since(0.1)
    @SerializedName("fetch_caller")
    @Expose
    public boolean fetchCaller = true;

    @Since(0.1)
    @Expose
    public Filter[] filters = new Filter[0];

    public LoggerConfig() {
    }

    public LoggerConfig(LoggerConfig config) {
        this.formatter = config.formatter;
        this.level = config.level;
        this.stringFormat = config.stringFormat;
        this.messageFormat = config.messageFormat;
        this.fetchCaller = config.fetchCaller;
        this.filters = config.filters.clone();
    }
}