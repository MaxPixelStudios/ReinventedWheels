package cn.maxpixel.rewh.logging.msg.publisher;

import cn.maxpixel.rewh.logging.Config;
import org.fusesource.jansi.AnsiConsole;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class OutputStreamMessagePublisher implements MessagePublisher, Closeable {
    public static final boolean ENABLE_JANSI_STREAM = Config.JANSI_INSTALLED && System.console() != null && Boolean.parseBoolean(System.getProperty("rewh.logging.jansi_stream", "true"));
    public static final OutputStreamMessagePublisher STDOUT = new OutputStreamMessagePublisher(ENABLE_JANSI_STREAM ? AnsiConsole.out() : System.out, true);
    public static final OutputStreamMessagePublisher STDERR = new OutputStreamMessagePublisher(ENABLE_JANSI_STREAM ? AnsiConsole.err() : System.err, true);

    private final boolean shouldClose;
    private final boolean supportColor;
    private final OutputStream outputStream;
    private final PrintStream ps;
    private final ThreadLocal<EncoderKit> kits = ThreadLocal.withInitial(EncoderKit::new);

    private boolean closed;

    public OutputStreamMessagePublisher(OutputStream outputStream, boolean supportColor) {
        this(outputStream, supportColor, false);
    }

    public OutputStreamMessagePublisher(OutputStream outputStream, boolean supportColor, boolean shouldClose) {
        this(outputStream, supportColor, shouldClose, false);
    }

    public OutputStreamMessagePublisher(OutputStream outputStream, boolean supportColor, boolean shouldClose, boolean buffered) {
        this.shouldClose = shouldClose;
        this.supportColor = Config.JANSI_INSTALLED && supportColor;
        this.outputStream = buffered && !(Objects.requireNonNull(outputStream) instanceof BufferedOutputStream) ?
                new BufferedOutputStream(outputStream) : outputStream;
        this.ps = new PrintStream(this.outputStream);
    }

    @Override
    public void publish(StringBuilder message, Throwable throwable, String colorPrefix) throws IOException {
        EncoderKit kit = kits.get();
        if (supportColor) kit.encodeColorString(colorPrefix, outputStream);
        kit.encode(message, outputStream);
        if (throwable != null) {
            throwable.printStackTrace(ps);
            ps.flush();
        }
    }

    @Override
    public void close() throws IOException {
        if (!closed && shouldClose) {
            outputStream.close();
            ps.close();
        }
        closed = true;
    }

    private static final class EncoderKit {
        private static final int BUFFER_SIZE = 8192;
        private final CharsetEncoder encoder = StandardCharsets.UTF_8.newEncoder();
        private final CharBuffer inputBuffer = CharBuffer.allocate(BUFFER_SIZE);
        private final ByteBuffer outputBuffer = ByteBuffer.allocate(BUFFER_SIZE * 2);

        private void reset() {
            encoder.reset();
            inputBuffer.clear();
            outputBuffer.clear();
        }

        private void encodeColorString(String colorString, OutputStream to) throws IOException {
            reset();
            colorString.getChars(0, colorString.length(), inputBuffer.array(), 0);
            inputBuffer.position(colorString.length()).flip();
            encoder.encode(inputBuffer, outputBuffer, true);
            to.write(outputBuffer.array(), outputBuffer.arrayOffset(), outputBuffer.position());
        }

        public void encode(StringBuilder from, OutputStream to) throws IOException {
            reset();
            int len = from.length();
            int encoded = 0;
            do {
                int remaining = len - encoded;
                boolean endOfInput = remaining <= (BUFFER_SIZE - inputBuffer.position());
                int toEncode = endOfInput ? remaining : (BUFFER_SIZE - inputBuffer.position());
                from.getChars(encoded, encoded += toEncode, inputBuffer.array(), inputBuffer.position());
                inputBuffer.position(inputBuffer.position() + toEncode).flip();
                CoderResult cr = encoder.encode(inputBuffer, outputBuffer, endOfInput);
                to.write(outputBuffer.array(), outputBuffer.arrayOffset(), outputBuffer.position());
                outputBuffer.clear();
                if (cr.isOverflow()) {
                    if (endOfInput) {
                        encoder.encode(inputBuffer, outputBuffer, true);// calls at most twice, encoder.maxBytesPerChar() == 3
                        to.write(outputBuffer.array(), outputBuffer.arrayOffset(), outputBuffer.position());
                        return;
                    } inputBuffer.compact();
                } else inputBuffer.clear();
            } while (encoded < len);
        }
    }
}