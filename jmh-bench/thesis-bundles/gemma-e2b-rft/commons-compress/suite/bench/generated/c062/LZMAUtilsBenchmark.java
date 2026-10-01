package bench.generated.c062;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.compressors.lzma.LZMAUtils;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class LZMAUtilsBenchmark {

    // State fields for inputs
    private String testFileName;
    private byte[] validHeaderSignature;
    private byte[] invalidSignature;

    @Setup(Level.Trial)
    public void setup() {
        // Setup input filenames for name mapping tests
        this.testFileName = "my_data.txt";

        // Setup byte signatures for header matching tests
        // LZMAUtils.HEADER_MAGIC is { (byte) 0x5D, 0, 0 }
        this.validHeaderSignature = new byte[]{(byte) 0x5D, 0, 0};
        this.invalidSignature = new byte[]{0x00, 0x00, 0x00};
    }

    @Benchmark
    public void benchmarkGetCompressedFileName(Blackhole bh) {
        String result = LZMAUtils.getCompressedFileName(testFileName);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkGetUncompressedFileName(Blackhole bh) {
        String result = LZMAUtils.getUncompressedFileName(testFileName);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkIsCompressedFileName(Blackhole bh) {
        boolean result = LZMAUtils.isCompressedFileName(testFileName);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkMatchesValidSignature(Blackhole bh) {
        boolean result = LZMAUtils.matches(validHeaderSignature, validHeaderSignature.length);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkMatchesInvalidSignature(Blackhole bh) {
        boolean result = LZMAUtils.matches(invalidSignature, validHeaderSignature.length);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkIsLZMACompressionAvailable(Blackhole bh) {
        boolean result = LZMAUtils.isLZMACompressionAvailable();
        bh.consume(result);
    }
}
