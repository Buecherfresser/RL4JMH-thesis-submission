package bench.generated.c089;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.compress.utils.FileNameUtils;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class FileNameUtilsBenchmark {

    // State fields moved into the class annotated with @State
    private Path complexPath;
    private Path simplePath;
    private String complexFileName;
    private String simpleFileName;
    private String noExtensionFileName;

    @Setup(Level.Trial)
    public void setup() {
        // Complex path with multiple segments and extensions
        String complexPathStr = "a/b/c/archive.tar.gz";
        complexPath = Paths.get(complexPathStr);
        complexFileName = complexPathStr;

        // Simple file name with one extension
        String simplePathStr = "image.jpg";
        simplePath = Paths.get(simplePathStr);
        simpleFileName = simplePathStr;

        // File name without extension
        noExtensionFileName = "README";
    }

    @Benchmark
    public void getBaseName_Path_Complex(Blackhole bh) {
        String result = FileNameUtils.getBaseName(complexPath);
        bh.consume(result);
    }

    @Benchmark
    public void getBaseName_Path_Simple(Blackhole bh) {
        String result = FileNameUtils.getBaseName(simplePath);
        bh.consume(result);
    }

    @Benchmark
    public void getBaseName_String_Complex(Blackhole bh) {
        String result = FileNameUtils.getBaseName(complexFileName);
        bh.consume(result);
    }

    @Benchmark
    public void getBaseName_String_NoExtension(Blackhole bh) {
        String result = FileNameUtils.getBaseName(noExtensionFileName);
        bh.consume(result);
    }

    @Benchmark
    public void getExtension_Path_Complex(Blackhole bh) {
        String result = FileNameUtils.getExtension(complexPath);
        bh.consume(result);
    }

    @Benchmark
    public void getExtension_Path_Simple(Blackhole bh) {
        String result = FileNameUtils.getExtension(simplePath);
        bh.consume(result);
    }

    @Benchmark
    public void getExtension_String_Complex(Blackhole bh) {
        String result = FileNameUtils.getExtension(complexFileName);
        bh.consume(result);
    }

    @Benchmark
    public void getExtension_String_NoExtension(Blackhole bh) {
        String result = FileNameUtils.getExtension(noExtensionFileName);
        bh.consume(result);
    }
}
