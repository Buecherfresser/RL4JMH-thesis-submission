package bench.generated.c089;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.io.ByteArrayInputStream;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import org.apache.commons.compress.utils.FileNameUtils;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class FileNameUtilsBenchmark {

    // Since FileNameUtils methods are static and operate on local inputs,
    // we don't strictly need @State fields, but we ensure we don't use
    // static final literals for inputs.

    @Benchmark
    public void getBaseNameString(Blackhole bh) {
        // Test case 1: File with extension
        String fileName1 = "document.pdf";
        String result1 = FileNameUtils.getBaseName(fileName1);
        bh.consume(result1);

        // Test case 2: File with no extension
        String fileName2 = "configfile";
        String result2 = FileNameUtils.getBaseName(fileName2);
        bh.consume(result2);
    }

    @Benchmark
    public void getExtensionString(Blackhole bh) {
        // Test case 1: File with extension
        String fileName1 = "image.jpg";
        String result1 = FileNameUtils.getExtension(fileName1);
        bh.consume(result1);

        // Test case 2: File with no extension
        String fileName2 = "readme";
        String result2 = FileNameUtils.getExtension(fileName2);
        bh.consume(result2);
    }

    @Benchmark
    public void getBaseNamePath(Blackhole bh) {
        // Test case 1: Simple path
        Path path1 = Path.of("/home/user/data/report.txt");
        String result1 = FileNameUtils.getBaseName(path1);
        bh.consume(result1);

        // Test case 2: Path with no extension
        Path path2 = Path.of("/etc/config");
        String result2 = FileNameUtils.getBaseName(path2);
        bh.consume(result2);
    }

    @Benchmark
    public void getExtensionPath(Blackhole bh) {
        // Test case 1: Simple path
        Path path1 = Path.of("/home/user/data/report.txt");
        String result1 = FileNameUtils.getExtension(path1);
        bh.consume(result1);

        // Test case 2: Path with no extension
        Path path2 = Path.of("/etc/config");
        String result2 = FileNameUtils.getExtension(path2);
        bh.consume(result2);
    }
}
