package bench.generated.c028;

import org.apache.commons.compress.archivers.tar.TarArchiveStructSparse;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
public class TarArchiveStructSparseBenchmark {

    // State fields to hold the objects we will benchmark against
    private List<TarArchiveStructSparse> sparseStructs;

    @Setup
    public void setup() {
        // Build a pool of representative instances.
        // Since the class is a simple struct, we use fixed, large values.
        sparseStructs = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            long offset = i * 1024L;
            long numBytes = i * 512L;
            sparseStructs.add(new TarArchiveStructSparse(offset, numBytes));
        }
    }

    @Benchmark
    public void getNumbytes(Blackhole bh) {
        // Call the method and consume the result
        long result = sparseStructs.get(0).getNumbytes();
        bh.consume(result);
    }

    @Benchmark
    public void getOffset(Blackhole bh) {
        // Call the method and consume the result
        long result = sparseStructs.get(0).getOffset();
        bh.consume(result);
    }

    @Benchmark
    public void equals(Blackhole bh) {
        // Test equality against itself (should always be true)
        TarArchiveStructSparse instance = sparseStructs.get(0);
        boolean result = instance.equals(instance);
        bh.consume(result);
    }

    @Benchmark
    public void hashCode(Blackhole bh) {
        // Test hashCode and consume the result
        int result = sparseStructs.get(0).hashCode();
        bh.consume(result);
    }
}
