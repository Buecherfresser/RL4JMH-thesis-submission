package bench.generated.c010;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.archivers.dump.DumpArchiveConstants;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class DumpArchiveConstantsBenchmark {

    // Since DumpArchiveConstants is a final class with only static members,
    // no instance state is required.

    /**
     * Benchmark exercising the static find method on COMPRESSION_TYPE enum.
     * This tests the iteration logic within the enum's static method.
     */
    @Benchmark
    public void findCompressionType(Blackhole bh) {
        // Test a known valid code (0 for ZLIB)
        DumpArchiveConstants.COMPRESSION_TYPE result = DumpArchiveConstants.COMPRESSION_TYPE.find(0);
        bh.consume(result);
    }

    /**
     * Benchmark exercising the static find method on SEGMENT_TYPE enum.
     * This tests the iteration logic within the enum's static method.
     */
    @Benchmark
    public void findSegmentType(Blackhole bh) {
        // Test a known valid code (1 for TAPE)
        DumpArchiveConstants.SEGMENT_TYPE result = DumpArchiveConstants.SEGMENT_TYPE.find(1);
        bh.consume(result);
    }

    /**
     * Benchmark accessing a static final constant.
     * This ensures the constant access path is measured.
     */
    @Benchmark
    public void accessConstant(Blackhole bh) {
        // Accessing a static final field
        int size = DumpArchiveConstants.TP_SIZE;
        bh.consume(size);
    }
}
