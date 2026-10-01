package bench.generated.c041;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.compress.compressors.FileNameUtil;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class FileNameUtilBenchmark {

    private FileNameUtil fileNameUtil;

    @Setup
    public void setup() {
        // Initialize FileNameUtil. Using empty maps for simplicity, focusing on the string manipulation logic.
        // This setup runs once per benchmark configuration.
        Map<String, String> emptyMap = Collections.emptyMap();
        this.fileNameUtil = new FileNameUtil(emptyMap, ".gz");
    }

    @Benchmark
    public String benchmarkGetCompressedFileName(Blackhole bh) {
        // Test case 1: Simple name, should append default extension
        String fileName1 = "file.txt";
        String result1 = fileNameUtil.getCompressedFileName(fileName1);
        bh.consume(result1);

        // Test case 2: Name that might trigger internal logic (though maps are empty, this tests path traversal)
        String fileName2 = "package.tar";
        String result2 = fileNameUtil.getCompressedFileName(fileName2);
        bh.consume(result2);

        return null; // Must return something or consume the result
    }

    @Benchmark
    public String benchmarkGetUncompressedFileName(Blackhole bh) {
        // Test case 1: Simple name, should return original name
        String fileName1 = "file.txt";
        String result1 = fileNameUtil.getUncompressedFileName(fileName1);
        bh.consume(result1);

        // Test case 2: Name that might trigger internal logic
        String fileName2 = "package.tgz";
        String result2 = fileNameUtil.getUncompressedFileName(fileName2);
        bh.consume(result2);

        return null;
    }
}
