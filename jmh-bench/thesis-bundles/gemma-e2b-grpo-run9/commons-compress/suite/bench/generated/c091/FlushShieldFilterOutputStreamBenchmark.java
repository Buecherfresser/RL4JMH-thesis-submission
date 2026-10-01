package bench.generated.c091;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.commons.compress.utils.FlushShieldFilterOutputStream;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(java.util.concurrent.TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class FlushShieldFilterOutputStreamBenchmark {

    // No state needed as all inputs are created locally within the benchmark method
    // to ensure isolation and measure the overhead of the filter instantiation and flush call.

    @Benchmark
    public void benchmarkFlush(Blackhole bh) throws IOException {
        // 1. Create an underlying stream (in-memory)
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        // 2. Create the filter instance
        FlushShieldFilterOutputStream filter = new FlushShieldFilterOutputStream(baos);
        
        try {
            // 3. Call the method being tested (which is a no-op)
            filter.flush();
        } finally {
            // Ensure the stream is closed if necessary, though for this test it might not matter much
            // since we are measuring the filter's behavior, not resource cleanup.
            try {
                filter.close();
            } catch (IOException e) {
                // Ignore
            }
        }
        
        // 4. Consume the result (the filter instance)
        bh.consume(filter);
    }
}
