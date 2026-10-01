package bench.generated.c040;

import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.apache.commons.compress.compressors.CompressorException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 3, time = 1)
public class CompressorStreamFactoryBenchmark {

    private CompressorStreamFactory factory;

    @Setup
    public void setup() {
        this.factory = CompressorStreamFactory.getSingleton();
    }

    @Benchmark
    public void createGzipInputStream(Blackhole bh) {
        try {
            // Use an empty stream as input, testing the factory's lookup logic
            InputStream dummyIn = new ByteArrayInputStream(new byte[0]);
            factory.createCompressorInputStream("gz", dummyIn);
        } catch (CompressorException e) {
            // Expected if the underlying implementation fails, but we consume the exception path
        }
        bh.consume(factory);
    }

    @Benchmark
    public void createBzip2OutputStream(Blackhole bh) {
        try {
            // Use an empty stream as output
            OutputStream dummyOut = new ByteArrayOutputStream();
            factory.createCompressorOutputStream("bzip2", dummyOut);
        } catch (Exception e) {
            // Consume exception if it occurs
        }
        bh.consume(factory);
    }

    @Benchmark
    public void createNonExistentInputStream(Blackhole bh) {
        try {
            // Test a compressor name that is highly unlikely to exist
            InputStream dummyIn = new ByteArrayInputStream(new byte[0]);
            factory.createCompressorInputStream("nonexistent_format", dummyIn);
        } catch (CompressorException e) {
            // Expected behavior for unknown compressor
        }
        bh.consume(factory);
    }

    @Benchmark
    public void createNullInputStream(Blackhole bh) {
        try {
            // Test null input stream
            factory.createCompressorInputStream("gz", null);
        } catch (IllegalArgumentException e) {
            // Expected behavior for null input
        } catch (CompressorException e) {
            // Catch other potential exceptions
        }
        bh.consume(factory);
    }

    @Benchmark
    public void createNullOutputStream(Blackhole bh) {
        try {
            // Test null output stream
            factory.createCompressorOutputStream("gz", null);
        } catch (IllegalArgumentException e) {
            // Expected behavior for null output
        } catch (Exception e) {
            // Catch other potential exceptions
        }
        bh.consume(factory);
    }
}
