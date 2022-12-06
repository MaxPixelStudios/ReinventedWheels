package cn.maxpixel.rewh.logging.msg.publisher;

import cn.maxpixel.rewh.logging.Logger;
import cn.maxpixel.rewh.logging.config.LoggerConfig;
import cn.maxpixel.rewh.logging.msg.Message;
import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.fusesource.jansi.Ansi;

import java.io.IOException;
import java.util.function.UnaryOperator;

public interface MessagePublisher {
    default boolean isLoggable(Logger logger, LoggerConfig config, Message message) {
        return true;
    }

    void publish(String message, UnaryOperator<Ansi> colorApplicator) throws IOException;

    class Adapter extends TypeAdapter<MessagePublisher> {
        @Override
        public void write(JsonWriter out, MessagePublisher value) throws IOException {
            if (value == OutputStreamMessagePublisher.STDOUT) out.value("STDOUT");
            else if (value == OutputStreamMessagePublisher.STDERR) out.value("STDERR");
            throw new UnsupportedOperationException("Currently unsupported");
        }

        @Override
        public MessagePublisher read(JsonReader in) throws IOException {
            switch (in.nextString()) {
                case "STDOUT":
                    return OutputStreamMessagePublisher.STDOUT;
                case "STDERR":
                    return OutputStreamMessagePublisher.STDERR;
                default:
                    throw new JsonParseException("Currently unsupported");
            }
        }
    }
}