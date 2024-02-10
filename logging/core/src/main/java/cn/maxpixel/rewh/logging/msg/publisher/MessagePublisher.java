package cn.maxpixel.rewh.logging.msg.publisher;

import cn.maxpixel.rewh.logging.Config;
import cn.maxpixel.rewh.logging.Logger;
import cn.maxpixel.rewh.logging.msg.Message;
import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public interface MessagePublisher {
    MessagePublisher NOP = (message, throwable, colorApplicator) -> {};

    default boolean isLoggable(Logger logger, Config.Logger config, Message message) {
        return true;
    }

    void publish(StringBuilder message, Throwable throwable, String colorPrefix) throws IOException;

    class Adapter extends TypeAdapter<MessagePublisher> {
        @Override
        public void write(JsonWriter out, MessagePublisher value) throws IOException {
            if (value == OutputStreamMessagePublisher.STDOUT) out.value("STDOUT");
            else if (value == OutputStreamMessagePublisher.STDERR) out.value("STDERR");
            else if (value == NOP) out.value("NOP");
            throw new UnsupportedOperationException("Currently unsupported");
        }

        @Override
        public MessagePublisher read(JsonReader in) throws IOException {
            switch (in.nextString()) {
                case "STDOUT":
                    return OutputStreamMessagePublisher.STDOUT;
                case "STDERR":
                    return OutputStreamMessagePublisher.STDERR;
                case "NOP":
                    return NOP;
                default:
                    throw new JsonParseException("Currently unsupported");
            }
        }
    }
}