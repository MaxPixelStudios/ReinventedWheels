package cn.maxpixel.rewh.logging.msg.publisher;

import cn.maxpixel.rewh.logging.Config;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.function.UnaryOperator;

public class OutputStreamMessagePublisher implements MessagePublisher {
    public static final OutputStreamMessagePublisher STDOUT = new OutputStreamMessagePublisher(Config.ENABLE_JANSI ? AnsiConsole.out() : System.out, true);
    public static final OutputStreamMessagePublisher STDERR = new OutputStreamMessagePublisher(Config.ENABLE_JANSI ? AnsiConsole.err() : System.err, true);

    private final boolean supportColor;
    private final OutputStream outputStream;

    public OutputStreamMessagePublisher(OutputStream outputStream, boolean supportColor) {
        this(outputStream, supportColor, false);
    }

    public OutputStreamMessagePublisher(OutputStream outputStream, boolean supportColor, boolean buffered) {
        this.supportColor = supportColor;
        this.outputStream = buffered && !(Objects.requireNonNull(outputStream) instanceof BufferedOutputStream) ?
                new BufferedOutputStream(outputStream) : outputStream;
    }

    @Override
    public void publish(String message, Throwable throwable, UnaryOperator<Ansi> colorApplicator) throws IOException {
        if (supportColor) message = colorApplicator.apply(Ansi.ansi(message.length())).a(message).toString();
        outputStream.write(message.getBytes(StandardCharsets.UTF_8));
        if (throwable != null) {
            PrintStream ps = new PrintStream(outputStream);
            throwable.printStackTrace(ps);
            ps.flush();
        }
    }
}