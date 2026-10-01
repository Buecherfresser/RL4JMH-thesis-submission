package bench.generated.c091;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.utils.FlushShieldFilterOutputStream;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
public class FlushShieldFilterOutputStreamBenchmark {

    /**
     * Benchmarks the execution of the flush() method on a FlushShieldFilterOutputStream.
     * This measures the overhead of creating the filter and calling its flush() method.
     */
    @Benchmark
    public void testFlushOperation(Blackhole bh) throws IOException {
        // 1. Setup the underlying stream (Input built in memory)
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // 2. Create the subject under test
        FlushShieldFilterOutputStream filter = new FlushShieldFilterOutputStream(baos);

        // 3. Execute the method being tested
        filter.flush();

        // 4. Consume the result
        bh.consume(null);
    }
}
