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

    // Inputs for getCompressedFileName
    private String inputFileNameCustom; // e.g., "package.tar" -> "package.tgz"
    private String inputFileNameDefault; // e.g., "file" -> "file.gz"
    private String inputFileNameNoSuffix; // e.g., "data" -> "data.gz"

    // Inputs for getUncompressedFileName
    private String inputCompressedCustom; // e.g., "package.tgz" -> "package.tar"
    private String inputCompressedGeneric; // e.g., "file.gz" -> "file"
    private String inputUncompressedNoSuffix; // e.g., "data" -> "data"

    // Inputs for isCompressedFileName
    private String inputIsCompressed; // e.g., "package.tgz"
    private String inputIsNotCompressed; // e.g., "data"

    @Setup(Level.Trial)
    public void setup() {
        // 1. Setup Suffix Maps
        Map<String, String> uncompressSuffix = new HashMap<>();
        // Compressed -> Uncompressed mappings
        uncompressSuffix.put(".tgz", ".tar");
        uncompressSuffix.put(".zip", ""); // Generic removal
        uncompressSuffix.put(".gz", "");  // Generic removal
        uncompressSuffix.put(".xz", "");  // Generic removal

        String defaultExtension = ".gz";

        // 2. Initialize SUT
        fileNameUtil = new FileNameUtil(uncompressSuffix, defaultExtension);

        // 3. Setup Inputs
        // getCompressedFileName inputs
        inputFileNameCustom = "archive.tar";
        inputFileNameDefault = "document";
        inputFileNameNoSuffix = "image";

        // getUncompressedFileName inputs
        inputCompressedCustom = "archive.tgz";
        inputCompressedGeneric = "log.gz";
        inputUncompressedNoSuffix = "datafile";

        // isCompressedFileName inputs
        inputIsCompressed = "archive.tgz";
        inputIsNotCompressed = "datafile";
    }

    @Benchmark
    public String testGetCompressedFileName_CustomMapping() {
        return fileNameUtil.getCompressedFileName(inputFileNameCustom);
    }

    @Benchmark
    public String testGetCompressedFileName_DefaultExtension() {
        return fileNameUtil.getCompressedFileName(inputFileNameDefault);
    }

    @Benchmark
    public String testGetCompressedFileName_NoSuffixFound() {
        return fileNameUtil.getCompressedFileName(inputFileNameNoSuffix);
    }

    @Benchmark
    public String testGetUncompressedFileName_CustomMapping() {
        return fileNameUtil.getUncompressedFileName(inputCompressedCustom);
    }

    @Benchmark
    public String testGetUncompressedFileName_GenericSuffix() {
        return fileNameUtil.getUncompressedFileName(inputCompressedGeneric);
    }

    @Benchmark
    public String testGetUncompressedFileName_NoSuffixFound() {
        return fileNameUtil.getUncompressedFileName(inputUncompressedNoSuffix);
    }

    @Benchmark
    public boolean testIsCompressedFileName_True() {
        return fileNameUtil.isCompressedFileName(inputIsCompressed);
    }

    @Benchmark
    public boolean testIsCompressedFileName_False() {
        return fileNameUtil.isCompressedFileName(inputIsNotCompressed);
    }
}
