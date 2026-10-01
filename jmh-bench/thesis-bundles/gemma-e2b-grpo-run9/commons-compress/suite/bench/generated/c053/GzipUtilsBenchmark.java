package bench.generated.c053;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import org.apache.commons.compress.compressors.gzip.GzipUtils;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class GzipUtilsBenchmark {

    // Since GzipUtils methods are static and do not mutate state, no instance fields are required.

    @Benchmark
    public void testGetCompressedFileName(Blackhole bh) {
        // Test a common filename pattern
        String fileName = "archive.tar.gz";
        bh.consume(GzipUtils.getCompressedFileName(fileName));
    }

    @Benchmark
    public void testGetUncompressedFileName(Blackhole bh) {
        // Test a compressed filename pattern
        String fileName = "data.txt.gz";
        bh.consume(GzipUtils.getUncompressedFileName(fileName));
    }

    @Benchmark
    public void testIsCompressedFileName(Blackhole bh) {
        // Test a compressed filename
        bh.consume(GzipUtils.isCompressedFileName("file.gz"));
    }
}
