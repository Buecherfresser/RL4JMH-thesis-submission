package bench.generated.c012;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.io.AppendableWriter;
import java.io.StringWriter;
import java.io.IOException;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class AppendableWriterBenchmark {

    // Shared payloads
    private char[] charArray;
    private String sampleString;

    // Writer that writes into a StringWriter (Appendable, Flushable, Closeable)
    private StringWriter stringWriter;
    private AppendableWriter writer;

    @Setup(Level.Trial)
    public void setUp() {
        // Build a moderate size payload (~1KB) to keep operations fast
        int size = 1024;
        charArray = new char[size];
        for (int i = 0; i < size; i++) {
            charArray[i] = (char) ('a' + (i % 26));
        }
        sampleString = new String(charArray);

        stringWriter = new StringWriter();
        writer = new AppendableWriter(stringWriter);
    }

    // Helper to clear the underlying buffer before each benchmark invocation
    private void resetBuffer() {
        stringWriter.getBuffer().setLength(0);
    }

    @Benchmark
    public int writeCharArrayRange() throws IOException {
        resetBuffer();
        writer.write(charArray, 0, charArray.length);
        return stringWriter.getBuffer().length();
    }

    @Benchmark
    public int writeInt() throws IOException {
        resetBuffer();
        writer.write('x');
        return stringWriter.getBuffer().length();
    }

    @Benchmark
    public int appendChar() throws IOException {
        resetBuffer();
        writer.append('y');
        return stringWriter.getBuffer().length();
    }

    @Benchmark
    public int appendCharSequenceRange() throws IOException {
        resetBuffer();
        writer.append(sampleString, 0, sampleString.length());
        return stringWriter.getBuffer().length();
    }

    @Benchmark
    public int appendCharSequence() throws IOException {
        resetBuffer();
        writer.append(sampleString);
        return stringWriter.getBuffer().length();
    }

    @Benchmark
    public int writeStringRange() throws IOException {
        resetBuffer();
        writer.write(sampleString, 0, sampleString.length());
        return stringWriter.getBuffer().length();
    }

    @Benchmark
    public int writeString() throws IOException {
        resetBuffer();
        writer.write(sampleString);
        return stringWriter.getBuffer().length();
    }

    @Benchmark
    public int writeCharArray() throws IOException {
        resetBuffer();
        writer.write(charArray);
        return stringWriter.getBuffer().length();
    }

    @Benchmark
    public int flush() throws IOException {
        resetBuffer();
        writer.write(sampleString);
        writer.flush();
        return stringWriter.getBuffer().length();
    }

    @Benchmark
    public int close(Blackhole bh) throws IOException {
        // Create a fresh writer for each close benchmark to avoid reusing a closed instance
        StringWriter sw = new StringWriter();
        AppendableWriter w = new AppendableWriter(sw);
        w.write(sampleString);
        w.close();
        // Consume the length to prevent dead-code elimination
        int len = sw.getBuffer().length();
        bh.consume(len);
        return len;
    }
}
