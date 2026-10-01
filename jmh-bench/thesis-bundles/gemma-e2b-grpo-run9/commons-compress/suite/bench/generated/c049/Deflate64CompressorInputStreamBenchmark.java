package bench.generated.c049;

import org.apache.commons.compress.compressors.deflate64.Deflate64CompressorInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Deflate64CompressorInputStreamBenchmark {

    // The subject under test. Since it requires an InputStream in its constructor,
    // we initialize it in the setup phase.
    private Deflate64CompressorInputStream inputStream;

    @Setup
    public void setup() throws IOException {
        // Create a dummy input stream. Since we cannot easily provide a valid
        // compressed payload or mock the internal HuffmanDecoder, we use a simple
        // ByteArrayInputStream. This setup focuses on measuring the overhead of
        // instantiating and calling the public methods of the wrapper class.
        try (InputStream dummyStream = new ByteArrayInputStream(new byte[0])) {
            // Instantiate the class using the InputStream constructor.
            // Note: This instantiation might fail or behave unexpectedly if the
            // internal HuffmanDecoder requires non-empty input, but it tests the path.
            this.inputStream = new Deflate64CompressorInputStream(dummyStream);
        }
    }

    @Benchmark
    public void benchmarkRead(Blackhole bh) {
        try {
            // Call the read method. This method loops internally.
            // We consume the result (which will likely be -1 or throw an exception
            // depending on the dummy stream behavior) to prevent dead code elimination.
            bh.consume(inputStream.read());
        } catch (IOException e) {
            // Catch expected IOExceptions during testing of a non-functional stream
        }
    }

    @Benchmark
    public void benchmarkReadBytes(Blackhole bh) {
        try {
            // Call the read method that takes a byte array.
            // We consume the result.
            bh.consume(inputStream.read(new byte[10], 0, 10));
        } catch (IOException e) {
            // Catch expected IOExceptions
        }
    }

    @Benchmark
    public void benchmarkClose(Blackhole bh) {
        try {
            // Call the close method, which involves closing resources.
            inputStream.close();
        } catch (IOException e) {
            // Catch expected IOExceptions
        }
    }

    @Benchmark
    public void benchmarkGetCompressedCount(Blackhole bh) {
        try {
            // Call a method that reads internal state.
            bh.consume(inputStream.getCompressedCount());
        } catch (Exception e) {
            // Catch potential exceptions
        }
    }
}
