package bench.generated.c012;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.io.IOException;
import java.io.StringWriter;
import java.util.concurrent.TimeUnit;
import jodd.io.AppendableWriter;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class AppendableWriterBenchmark {

    private char[] charBuffer;
    private String testString;
    private String testCharSequence;

    @Setup(Level.Trial)
    public void setup() {
        // Input size: 100 characters
        charBuffer = new char[100];
        for (int i = 0; i < 100; i++) {
            charBuffer[i] = (char) ('a' + (i % 26));
        }
        testString = new String(charBuffer);
        testCharSequence = new String(charBuffer);
    }

    // Helper method to create a fresh, clean writer instance for each invocation
    // This simulates Level.Invocation setup for cumulative operations.
    private AppendableWriter createFreshWriter() {
        StringWriter sw = new StringWriter();
        return new AppendableWriter(sw);
    }

    @Benchmark
    public void writeCharBufferFull(Blackhole bh) throws IOException {
        AppendableWriter writer = createFreshWriter();
        writer.write(charBuffer);
        bh.consume(writer);
    }

    @Benchmark
    public void writeCharBufferPartial(Blackhole bh) throws IOException {
        AppendableWriter writer = createFreshWriter();
        // Write first 50 characters
        writer.write(charBuffer, 0, 50);
        bh.consume(writer);
    }

    @Benchmark
    public void writeIntChar(Blackhole bh) throws IOException {
        AppendableWriter writer = createFreshWriter();
        // Write a single character (ASCII 'z')
        writer.write('z');
        bh.consume(writer);
    }

    @Benchmark
    public void appendChar(Blackhole bh) throws IOException {
        AppendableWriter writer = createFreshWriter();
        // Append a single character
        writer.append('z');
        bh.consume(writer);
    }

    @Benchmark
    public void appendCharSequenceFull(Blackhole bh) throws IOException {
        AppendableWriter writer = createFreshWriter();
        // Append the full test string
        writer.append(testCharSequence);
        bh.consume(writer);
    }

    @Benchmark
    public void appendCharSequencePartial(Blackhole bh) throws IOException {
        AppendableWriter writer = createFreshWriter();
        // Append a partial segment of the test string
        writer.append(testCharSequence, 10, 60);
        bh.consume(writer);
    }

    @Benchmark
    public void writeStringFull(Blackhole bh) throws IOException {
        AppendableWriter writer = createFreshWriter();
        // Write the full test string
        writer.write(testString);
        bh.consume(writer);
    }

    @Benchmark
    public void writeStringPartial(Blackhole bh) throws IOException {
        AppendableWriter writer = createFreshWriter();
        // Write a partial segment of the test string
        writer.write(testString, 20, 70);
        bh.consume(writer);
    }

    @Benchmark
    public void writeCharBufferFull_Alternative(Blackhole bh) throws IOException {
        AppendableWriter writer = createFreshWriter();
        // Using the char[] overload
        writer.write(charBuffer);
        bh.consume(writer);
    }

    @Benchmark
    public void flushOperation(Blackhole bh) throws IOException {
        // We must ensure the writer is not closed and has content to flush
        AppendableWriter writer = createFreshWriter();
        writer.write('a');
        writer.write('b');
        writer.flush();
        bh.consume(writer);
    }

    @Benchmark
    public void closeOperation(Blackhole bh) throws IOException {
        // We must ensure the writer is not closed
        AppendableWriter writer = createFreshWriter();
        writer.write('a');
        writer.close();
        bh.consume(writer);
    }
}
