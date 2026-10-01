package bench.generated.c062;

import org.apache.commons.compress.compressors.lzma.LZMAUtils;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class LZMAUtilsBenchmark {

    // --- State Fields for Inputs ---
    private String regularFileName;
    private String compressedFileName;
    private String uncompressedFileName;
    private byte[] validHeader;
    private byte[] invalidHeader;

    @Setup
    public void setup() {
        // 1. File Name Inputs
        regularFileName = "document.txt";
        compressedFileName = "document.txt.lzma";
        uncompressedFileName = "document.txt";

        // 2. Header Byte Array Inputs
        // LZMA Header Magic Bytes: { (byte) 0x5D, 0, 0 }
        validHeader = new byte[]{(byte) 0x5D, 0, 0};
        // Invalid header (e.g., starts with 0x00)
        invalidHeader = new byte[]{0x00, 0x00, 0x00};
    }

    // --- Benchmarks for Filename Mapping ---

    @Benchmark
    public void getCompressedFileName(Blackhole bh) {
        String result = LZMAUtils.getCompressedFileName(regularFileName);
        bh.consume(result);
    }

    @Benchmark
    public void getUncompressedFileName(Blackhole bh) {
        String result = LZMAUtils.getUncompressedFileName(compressedFileName);
        bh.consume(result);
    }

    // --- Benchmarks for Suffix Detection ---

    @Benchmark
    public void isCompressedFileName_True(Blackhole bh) {
        boolean result = LZMAUtils.isCompressedFileName(compressedFileName);
        bh.consume(result);
    }

    @Benchmark
    public void isCompressedFileName_False(Blackhole bh) {
        boolean result = LZMAUtils.isCompressedFileName(regularFileName);
        bh.consume(result);
    }

    // --- Benchmarks for Header Matching ---

    @Benchmark
    public void matches_True(Blackhole bh) {
        boolean result = LZMAUtils.matches(validHeader, validHeader.length);
        bh.consume(result);
    }

    @Benchmark
    public void matches_False(Blackhole bh) {
        boolean result = LZMAUtils.matches(invalidHeader, validHeader.length);
        bh.consume(result);
    }

    // --- Benchmarks for Availability Check ---

    @Benchmark
    public void isLZMACompressionAvailable(Blackhole bh) {
        boolean result = LZMAUtils.isLZMACompressionAvailable();
        bh.consume(result);
    }
}
