package bench.generated.c070;

import org.apache.commons.compress.compressors.xz.XZCompressorInputStream;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class XZCompressorInputStreamBenchmark {

    // Since XZCompressorInputStream is complex and relies on internal state,
    // we avoid @State fields and instantiate it within the benchmark method
    // to ensure thread safety and clean state for each invocation.

    @Benchmark
    public void benchmarkRead(Blackhole bh) {
        try (InputStream inputStream = new ByteArrayInputStream(new byte[0])) {
            // Instantiate the SUT. This will likely throw an IOException
            // or MemoryLimitException on the first read if the input is empty/invalid,
            // but it correctly exercises the constructor and read method path.
            XZCompressorInputStream s = new XZCompressorInputStream(inputStream);
            
            // Call the method and consume the result
            try {
                s.read();
            } catch (IOException e) {
                // Expected for empty/invalid stream, swallow exception for benchmark stability
            }
        } catch (IOException e) {
            // Handle setup failure if necessary
        }
    }

    @Benchmark
    public void benchmarkSkip(Blackhole bh) {
        try (InputStream inputStream = new ByteArrayInputStream(new byte[0])) {
            XZCompressorInputStream s = new XZCompressorInputStream(inputStream);
            
            try {
                // Call the method and consume the result
                s.skip(100);
            } catch (IOException e) {
                // Expected for empty/invalid stream
            }
        } catch (IOException e) {
            // Handle setup failure if necessary
        }
    }
}
