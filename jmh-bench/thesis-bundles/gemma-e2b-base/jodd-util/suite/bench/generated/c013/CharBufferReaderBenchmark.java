package bench.generated.c013;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.nio.CharBuffer;
import java.util.concurrent.TimeUnit;

import jodd.io.CharBufferReader;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class CharBufferReaderBenchmark {

    // State fields for the subject and inputs
    private CharBuffer charBuffer;
    private CharBufferReader reader;
    private char[] readChars;
    private int offset;
    private int length;

    // Constants for input size
    private static final int BUFFER_SIZE = 1024 * 1024; // 1 MB buffer

    @Setup(Level.Trial)
    public void setup() {
        // 1. Create a large CharBuffer and populate it with data
        charBuffer = CharBuffer.allocate(BUFFER_SIZE);
        for (int i = 0; i < BUFFER_SIZE; i++) {
            charBuffer.put((char) ('A' + (i % 26)));
        }
        charBuffer.flip(); // Prepare for reading

        // 2. Create the reader instance
        reader = new CharBufferReader(charBuffer);

        // 3. Prepare the destination array for reading (must be large enough)
        readChars = new char[BUFFER_SIZE];
        offset = 0;
        length = BUFFER_SIZE;
    }

    @Benchmark
    public void benchmarkReadBulk(Blackhole bh) {
        // Call the read method: reader.read(char[] chars, int offset, int length)
        int result = reader.read(readChars, offset, length);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkReadSingleChar(Blackhole bh) {
        // Call the read method: reader.read()
        int result = reader.read();
        bh.consume(result);
    }
}
