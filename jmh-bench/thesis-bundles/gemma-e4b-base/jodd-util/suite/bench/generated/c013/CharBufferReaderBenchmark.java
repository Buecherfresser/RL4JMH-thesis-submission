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
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CharBufferReaderBenchmark {

    private CharBufferReader reader;
    private char[] readBuffer;
    private static final int BUFFER_SIZE = 1024;

    @Setup(Level.Trial)
    public void setup() {
        // 1. Prepare input data
        char[] data = new char[BUFFER_SIZE];
        for (int i = 0; i < BUFFER_SIZE; i++) {
            data[i] = (char) ('a' + (i % 26));
        }
        CharBuffer charBuffer = CharBuffer.wrap(data);

        // 2. Initialize the subject under test
        reader = new CharBufferReader(charBuffer);

        // 3. Prepare the output buffer for read(char[], int, int)
        readBuffer = new char[BUFFER_SIZE];
    }

    @Benchmark
    public int readChunk() {
        // Read a chunk of data (e.g., 100 characters)
        int lengthToRead = 100;
        int result = reader.read(readBuffer, 0, lengthToRead);
        return result;
    }

    @Benchmark
    public int readSingleChar() {
        // Read a single character
        int result = reader.read();
        return result;
    }

    @Benchmark
    public void closeReader(Blackhole bh) {
        // Test the close method (though usually trivial)
        reader.close();
        bh.consume(null);
    }
}
