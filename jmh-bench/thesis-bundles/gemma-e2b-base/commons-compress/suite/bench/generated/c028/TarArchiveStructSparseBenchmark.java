package bench.generated.c028;

import org.apache.commons.compress.archivers.tar.TarArchiveStructSparse;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
public class TarArchiveStructSparseBenchmark {

    // State fields for input data
    private TarArchiveStructSparse sparseInstance1;
    private TarArchiveStructSparse sparseInstance2;
    private final long OFFSET_VALUE = 1234567890123L;
    private final long NUMBYTES_VALUE = 9876543210987L;

    @Setup
    public void setup() {
        // Build instances once for reuse across benchmarks
        sparseInstance1 = new TarArchiveStructSparse(OFFSET_VALUE, NUMBYTES_VALUE);
        sparseInstance2 = new TarArchiveStructSparse(OFFSET_VALUE, NUMBYTES_VALUE);
    }

    @Benchmark
    public void testConstructor(Blackhole bh) {
        // Test construction time
        TarArchiveStructSparse instance = new TarArchiveStructSparse(OFFSET_VALUE, NUMBYTES_VALUE);
        bh.consume(instance);
    }

    @Benchmark
    public void testGetNumbytes(Blackhole bh) {
        // Test getter for numbytes
        long result = sparseInstance1.getNumbytes();
        bh.consume(result);
    }

    @Benchmark
    public void testGetOffset(Blackhole bh) {
        // Test getter for offset
        long result = sparseInstance1.getOffset();
        bh.consume(result);
    }

    @Benchmark
    public void testEqualsTrue(Blackhole bh) {
        // Test equals when objects are identical
        boolean result = sparseInstance1.equals(sparseInstance2);
        bh.consume(result);
    }

    @Benchmark
    public void testEqualsFalse(Blackhole bh) {
        // Test equals when objects are different (different offset)
        TarArchiveStructSparse differentInstance = new TarArchiveStructSparse(1L, NUMBYTES_VALUE);
        boolean result = sparseInstance1.equals(differentInstance);
        bh.consume(result);
    }

    @Benchmark
    public void testHashCode(Blackhole bh) {
        // Test hashCode calculation
        int hashCode = sparseInstance1.hashCode();
        bh.consume(hashCode);
    }
}
