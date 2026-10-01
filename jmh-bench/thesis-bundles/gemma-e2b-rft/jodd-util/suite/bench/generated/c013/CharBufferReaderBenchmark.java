package bench.generated.c013;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.io.Reader;
import java.nio.CharBuffer;
import java.util.concurrent.TimeUnit;

import jodd.io.CharBufferReader;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CharBufferReaderBenchmark {

    // State fields for the subject under test and inputs
    private CharBuffer charBuffer;
    private CharBufferReader reader;
    private char[] charArray;
    private final int bufferSize = 1024 * 1024; // 1 MB

    @Setup
    public void setup() {
        // 1. Create a large payload string
        String payloadString = "A".repeat(bufferSize);
        char[] payloadChars = payloadString.toCharArray();

        // 2. Create the CharBuffer
        this.charBuffer = CharBuffer.wrap(payloadChars);

        // 3. Create the CharBufferReader instance
        this.reader = new CharBufferReader(this.charBuffer);

        // 4. Prepare a reusable char array for read operations
        this.charArray = new char[bufferSize];
    }

    @Benchmark
    public void read_full_chunk(Blackhole bh) {
        // Test read(char[] chars, int offset, int length)
        int lengthToRead = 1024;
        int offset = 0;

        int result = reader.read(charArray, offset, lengthToRead);
        bh.consume(result);
    }

    @Benchmark
    public void read_single_char(Blackhole bh) {
        // Test read()
        int result = reader.read();
        bh.consume(result);
    }
}
