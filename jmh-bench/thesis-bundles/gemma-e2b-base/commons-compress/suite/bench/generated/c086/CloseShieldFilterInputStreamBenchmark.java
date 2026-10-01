package bench.generated.c086;

import org.apache.commons.compress.utils.CloseShieldFilterInputStream;
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
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
public class CloseShieldFilterInputStreamBenchmark {

    private InputStream inputStream;
    private CloseShieldFilterInputStream filterInputStream;

    @Setup(Level.Trial)
    public void setup() throws IOException {
        // Build a representative input stream in memory.
        // Using a small, fixed payload to ensure the operation is fast and measurable.
        byte[] data = "Test data for benchmarking".getBytes();
        this.inputStream = new ByteArrayInputStream(data);
        
        // Initialize the subject under test
        this.filterInputStream = new CloseShieldFilterInputStream(this.inputStream);
    }

    @Benchmark
    public void testConstructor(Blackhole bh) throws IOException {
        // Call the constructor once per benchmark iteration.
        CloseShieldFilterInputStream localFilter = new CloseShieldFilterInputStream(this.inputStream);
        bh.consume(localFilter);
    }

    @Benchmark
    public void testClose(Blackhole bh) throws IOException {
        // Call the close method once per benchmark iteration.
        filterInputStream.close();
    }
}
