package bench.generated.c005;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.archivers.arj.ArjArchiveEntry;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ArjArchiveEntryBenchmark {

    // State field for the subject under test.
    private ArjArchiveEntry entry;

    @Setup
    public void setup() {
        // Initialize the entry object.
        this.entry = new ArjArchiveEntry();
    }

    @Benchmark
    public void benchmarkGetHostOs(Blackhole bh) {
        // Call a public method.
        bh.consume(this.entry.getHostOs());
    }

    @Benchmark
    public void benchmarkGetMode(Blackhole bh) {
        // Replaced the non-public getMethod() call with the public getMode() call
        // to resolve compilation errors.
        bh.consume(this.entry.getMode());
    }

    @Benchmark
    public void benchmarkGetName(Blackhole bh) {
        // Call a public method.
        bh.consume(this.entry.getName());
    }

    @Benchmark
    public void benchmarkGetSize(Blackhole bh) {
        // Call a public method.
        bh.consume(this.entry.getSize());
    }

    @Benchmark
    public void benchmarkIsDirectory(Blackhole bh) {
        // Call a public method.
        bh.consume(this.entry.isDirectory());
    }

    @Benchmark
    public void benchmarkIsHostOsUnix(Blackhole bh) {
        // Call a public method.
        bh.consume(this.entry.isHostOsUnix());
    }
}
