package bench.generated.c031;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.archivers.zip.ExtraFieldUtils;
import java.io.IOException;
import java.util.zip.ZipException;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class ExtraFieldUtilsBenchmark {

    // Since ExtraFieldUtils methods are static and take byte arrays,
    // we don't need instance state for this benchmark.

    /**
     * Benchmark for the simple parsing method.
     * This method tests the static parsing logic with a fixed, small byte array.
     */
    @Benchmark
    public void benchmarkParseSimple(Blackhole bh) {
        try {
            // Use a small, fixed byte array. The actual content doesn't matter
            // as much as exercising the method call path.
            byte[] dummyData = new byte[1024];
            ExtraFieldUtils.parse(dummyData);
        } catch (ZipException e) {
            // Catch expected exceptions during parsing if the dummy data is invalid,
            // allowing the benchmark to continue.
            bh.consume(null);
        }
    }

    /**
     * Benchmark for parsing with the 'local' flag set to true.
     */
    @Benchmark
    public void benchmarkParseLocal(Blackhole bh) {
        try {
            byte[] dummyData = new byte[2048];
            ExtraFieldUtils.parse(dummyData, true);
        } catch (ZipException e) {
            bh.consume(null);
        }
    }

    /**
     * Benchmark for parsing with the 'local' flag set to false.
     */
    @Benchmark
    public void benchmarkParseCentralDirectory(Blackhole bh) {
        try {
            byte[] dummyData = new byte[4096];
            ExtraFieldUtils.parse(dummyData, false);
        } catch (ZipException e) {
            bh.consume(null);
        }
    }

    /**
     * Benchmark for parsing with a custom error handling behavior (THROW).
     * This tests the dynamic behavior switching within the parse method.
     */
    @Benchmark
    public void benchmarkParseWithThrowBehavior(Blackhole bh) {
        try {
            byte[] dummyData = new byte[1024];
            // We rely on the default behavior being THROW, but explicitly calling it
            // ensures we hit the path that uses the custom behavior implementation.
            ExtraFieldUtils.parse(dummyData, true, ExtraFieldUtils.UnparseableExtraField.THROW);
        } catch (ZipException e) {
            // Expected if data is corrupt, but we consume the result.
        }
    }
}
