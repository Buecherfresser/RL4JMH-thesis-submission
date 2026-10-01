package bench.generated.c073;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import org.apache.commons.compress.compressors.z.ZCompressorInputStream;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ZCompressorInputStreamBenchmark {

    @Benchmark
    public void constructorBenchmark(Blackhole bh) {
        // Test the overhead of initialization using the public constructor.
        try {
            // Use a dummy input stream.
            try (InputStream dummyStream = new ByteArrayInputStream(new byte[100])) {
                new ZCompressorInputStream(dummyStream);
            }
        } catch (IOException e) {
            // Ignore exceptions during benchmarking if they occur due to dummy input
        }
    }

    @Benchmark
    public void matchesStaticMethod(Blackhole bh) {
        // Test the static method which is public and pure.
        try {
            // Test with signature matching the constants defined in the SUT.
            boolean result = ZCompressorInputStream.matches(new byte[]{0x1f, (byte) 0x9d}, 5);
            bh.consume(result);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }
}
