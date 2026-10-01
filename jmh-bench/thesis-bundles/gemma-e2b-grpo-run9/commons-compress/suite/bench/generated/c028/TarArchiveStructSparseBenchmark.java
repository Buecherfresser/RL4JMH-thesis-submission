package bench.generated.c028;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.archivers.tar.TarArchiveStructSparse;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class TarArchiveStructSparseBenchmark {

    // State field to hold an instance of the SUT.
    // Since TarArchiveStructSparse is immutable, we can reuse it safely across benchmarks
    // if we don't modify it, but creating a new one in @Setup is safer practice
    // if we were testing mutation. For simplicity and adherence to the immutable nature,
    // we will create a new instance inside the benchmark method or rely on the
    // JMH harness to manage the state if we were using a mutable object.
    // Since we are benchmarking a simple read operation, we instantiate inside the benchmark
    // to ensure we are measuring the cost of the operation itself, not state reuse.

    @Setup
    public void setup() {
        // No complex setup needed for this simple immutable class.
    }

    @Benchmark
    public void benchmarkGetNumbytes(Blackhole bh) {
        // Create a fresh instance for each benchmark run to measure construction + access time
        TarArchiveStructSparse sparse = new TarArchiveStructSparse(100L, 500L);
        long numBytes = sparse.getNumbytes();
        bh.consume(numBytes);
    }

    @Benchmark
    public void benchmarkGetOffset(Blackhole bh) {
        TarArchiveStructSparse sparse = new TarArchiveStructSparse(100L, 500L);
        long offset = sparse.getOffset();
        bh.consume(offset);
    }

    @Benchmark
    public void benchmarkEquals(Blackhole bh) {
        // Test equals method. Since we cannot easily compare against a null or
        // a complex object without violating the zero-parameter rule or introducing
        // complex state, we test against itself (which should be fast).
        TarArchiveStructSparse sparse1 = new TarArchiveStructSparse(100L, 500L);
        boolean result = sparse1.equals(sparse1);
        bh.consume(result);
    }
}
