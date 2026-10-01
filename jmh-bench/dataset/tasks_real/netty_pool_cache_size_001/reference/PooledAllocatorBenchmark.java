package bench;

import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class PooledAllocatorBenchmark {

    @Param({"1024"})
    public int bufSize;

    @Param({"32"})
    public int iterationsPerOp;

    private PooledAllocator allocator;

    @Setup
    public void setup() {
        allocator = new PooledAllocator();
    }

    @Benchmark
    public void allocateAndFree(Blackhole bh) {
        for (int i = 0; i < iterationsPerOp; i++) {
            byte[] buf = allocator.allocate(bufSize);
            bh.consume(buf);
            allocator.release(buf);
        }
    }
}
