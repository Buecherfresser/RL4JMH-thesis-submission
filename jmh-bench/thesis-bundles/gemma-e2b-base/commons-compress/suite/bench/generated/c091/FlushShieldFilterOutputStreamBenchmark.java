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

    private OutputStream underlyingOutputStream;
    private FlushShieldFilterOutputStream filterOutputStream;

    @Setup
    public void setup() throws IOException {
        // Prepare a reusable in-memory output stream for benchmarking
        this.underlyingOutputStream = new ByteArrayOutputStream();
        // Initialize the filter stream using the prepared underlying stream
        this.filterOutputStream = new FlushShieldFilterOutputStream(this.underlyingOutputStream);
    }

    @Benchmark
    public void benchmarkFlush(Blackhole bh) throws IOException {
        // Execute the method under test
        filterOutputStream.flush();
        // Consume the result to prevent dead code elimination
        bh.consume(null);
    }
}
