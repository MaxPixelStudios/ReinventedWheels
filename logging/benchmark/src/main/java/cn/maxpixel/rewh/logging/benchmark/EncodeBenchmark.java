package cn.maxpixel.rewh.logging.benchmark;

import org.openjdk.jmh.annotations.*;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Fork(1)
@State(Scope.Thread)
@Warmup(iterations = 5)
@Measurement(iterations = 5)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class EncodeBenchmark {
    private static final OutputStream NULL_OUT = new OutputStream() {
        @Override
        public void write(byte[] b, int off, int len) throws IOException {
        }

        @Override
        public void write(byte[] b) throws IOException {
        }

        @Override
        public void write(int b) throws IOException {
        }
    };

    private final StringBuilder builder = new StringBuilder();
    private final Random r = new Random();
    private static final char[] chars = new char[] {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o'};

    @Setup(Level.Iteration)
    public void prepare() {
        builder.setLength(0);
        char c = chars[r.nextInt(chars.length)];
        for (int i = 0; i < 8000; i++) {
            builder.append(c);
        }
    }

    @Benchmark
    public void encodeWithSmallLengthSpecialTreated(EncoderKit1 kit) throws IOException {
        kit.encode(builder, NULL_OUT);
    }

    @Benchmark
    public void encodeWithoutSmallLengthSpecialTreated(EncoderKit2 kit) throws IOException {
        kit.encode(builder, NULL_OUT);
    }

    @State(Scope.Thread)
    public static class EncoderKit1 {
        private static final int BUFFER_SIZE = 8192;
        private final CharsetEncoder encoder = StandardCharsets.UTF_8.newEncoder();
        private final CharBuffer inputBuffer = CharBuffer.allocate(BUFFER_SIZE);
        private final ByteBuffer outputBuffer = ByteBuffer.allocate(BUFFER_SIZE * 2);

        public final void encode(StringBuilder from, OutputStream to) throws IOException {
            encoder.reset();
            inputBuffer.clear();
            outputBuffer.clear();
            int len = from.length();
            if (len <= BUFFER_SIZE) {
                from.getChars(0, len, inputBuffer.array(), 0);
                inputBuffer.position(len).flip();
                CoderResult cr = encoder
                        .encode(inputBuffer, outputBuffer, true);
                to.write(outputBuffer.array(), outputBuffer.arrayOffset(), outputBuffer.position());
                if (cr.isOverflow()) {
                    outputBuffer.clear();
                    encoder.encode(inputBuffer, outputBuffer, true);// calls at most twice, encoder.maxBytesPerChar() == 3
                    to.write(outputBuffer.array(), outputBuffer.arrayOffset(), outputBuffer.position());
                }
            } else {
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

    @State(Scope.Thread)
    public static class EncoderKit2 {
        private static final int BUFFER_SIZE = 8192;
        private final CharsetEncoder encoder = StandardCharsets.UTF_8.newEncoder();
        private final CharBuffer inputBuffer = CharBuffer.allocate(BUFFER_SIZE);
        private final ByteBuffer outputBuffer = ByteBuffer.allocate(BUFFER_SIZE * 2);

        public final void encode(StringBuilder from, OutputStream to) throws IOException {
            encoder.reset();
            inputBuffer.clear();
            outputBuffer.clear();
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