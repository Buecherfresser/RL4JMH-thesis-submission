package bench.generated.c041;

import org.apache.commons.compress.compressors.FileNameUtil;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
public class FileNameUtilBenchmark {

    private FileNameUtil fileNameUtil;
    private List<String> testFileNames;
    private int index;

    private Map<String, String> uncompressSuffixMap;
    private String defaultExtension;

    @Setup
    public void setup() {
        // 1. Define the uncompress suffix map
        Map<String, String> uncompressSuffix = new HashMap<>();
        uncompressSuffix.put("tgz", "tar");
        uncompressSuffix.put("svgz", "svg");
        uncompressSuffix.put("gz", ""); // Example of a generic suffix mapping to empty string

        this.uncompressSuffixMap = uncompressSuffix;
        this.defaultExtension = ".gz";

        // 2. Initialize the FileNameUtil instance
        this.fileNameUtil = new FileNameUtil(uncompressSuffix, this.defaultExtension);

        // 3. Define test file names
        this.testFileNames = List.of(
            "package.tar",      // Index 0
            "image.svgz",       // Index 1
            "document.txt",     // Index 2
            "archive.tgz",      // Index 3
            "file.gz"           // Index 4
        );
        this.index = 0;
    }

    @Benchmark
    public void getCompressedFileName_CustomMapping(Blackhole bh) {
        String input = testFileNames.get(index);
        String result = fileNameUtil.getCompressedFileName(input);
        bh.consume(result);
        index = (index + 1) % testFileNames.size();
    }

    @Benchmark
    public void getCompressedFileName_DefaultSuffix(Blackhole bh) {
        String input = testFileNames.get(index);
        String result = fileNameUtil.getCompressedFileName(input);
        bh.consume(result);
        index = (index + 1) % testFileNames.size();
    }

    @Benchmark
    public void getUncompressedFileName_CustomMapping(Blackhole bh) {
        String input = testFileNames.get(index);
        String result = fileNameUtil.getUncompressedFileName(input);
        bh.consume(result);
        index = (index + 1) % testFileNames.size();
    }

    @Benchmark
    public void getUncompressedFileName_NoMapping(Blackhole bh) {
        String input = testFileNames.get(index);
        String result = fileNameUtil.getUncompressedFileName(input);
        bh.consume(result);
        index = (index + 1) % testFileNames.size();
    }

    @Benchmark
    public void isCompressedFileName_TrueCase(Blackhole bh) {
        String input = testFileNames.get(index);
        boolean result = fileNameUtil.isCompressedFileName(input);
        bh.consume(result);
        index = (index + 1) % testFileNames.size();
    }

    @Benchmark
    public void isCompressedFileName_FalseCase(Blackhole bh) {
        String input = testFileNames.get(index);
        boolean result = fileNameUtil.isCompressedFileName(input);
        bh.consume(result);
        index = (index + 1) % testFileNames.size();
    }
}
