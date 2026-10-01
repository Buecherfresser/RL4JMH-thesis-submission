package bench.generated.c089;

import org.apache.commons.compress.utils.FileNameUtils;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class FileNameUtilsBenchmark {

    // --- Setup State ---
    private Path setupPath;
    private String setupFileName;

    @Setup
    public void setup() {
        // Setup for Path operations
        setupPath = Path.of("/home/user/documents/report.v1.txt");

        // Setup for String operations
        setupFileName = "archive.tar.gz";
    }

    // --- Benchmarks for getBaseName(Path) ---

    @Benchmark
    public void getBaseName_Path(Blackhole bh) {
        String result = FileNameUtils.getBaseName(setupPath);
        bh.consume(result);
    }

    // --- Benchmarks for getBaseName(String) ---

    @Benchmark
    public void getBaseName_String(Blackhole bh) {
        String result = FileNameUtils.getBaseName(setupFileName);
        bh.consume(result);
    }

    // --- Benchmarks for getExtension(Path) ---

    @Benchmark
    public void getExtension_Path(Blackhole bh) {
        String result = FileNameUtils.getExtension(setupPath);
        bh.consume(result);
    }

    // --- Benchmarks for getExtension(String) ---

    @Benchmark
    public void getExtension_String(Blackhole bh) {
        String result = FileNameUtils.getExtension(setupFileName);
        bh.consume(result);
    }
}
