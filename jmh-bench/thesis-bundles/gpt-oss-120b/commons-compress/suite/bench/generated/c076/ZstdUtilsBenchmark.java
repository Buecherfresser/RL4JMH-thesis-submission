package bench.generated.c076;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.compressors.zstandard.ZstdUtils;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ZstdUtilsBenchmark {

    private byte[] zstdSignature;
    private byte[] skippableSignature;
    private int length;

    @Setup(Level.Trial)
    public void setUp() {
        // Zstandard frame magic bytes
        zstdSignature = new byte[] {
                (byte) 0x28, (byte) 0xB5, (byte) 0x2F, (byte) 0xFD
        };
        // Skippable frame: first byte high nibble 0x5, followed by SKIPPABLE_FRAME_MAGIC
        skippableSignature = new byte[4];
        skippableSignature[0] = (byte) 0x50;
        skippableSignature[1] = (byte) 0x2A;
        skippableSignature[2] = (byte) 0x4D;
        skippableSignature[3] = (byte) 0x18;
        length = 4;
    }

    @Benchmark
    public boolean benchmarkIsZstdCompressionAvailable() {
        return ZstdUtils.isZstdCompressionAvailable();
    }

    @Benchmark
    public boolean benchmarkMatchesZstandard() {
        return ZstdUtils.matches(zstdSignature, length);
    }

    @Benchmark
    public boolean benchmarkMatchesSkippable() {
        return ZstdUtils.matches(skippableSignature, length);
    }

    @Benchmark
    public void benchmarkSetCacheZstdAvailablityTrue(Blackhole bh) {
        ZstdUtils.setCacheZstdAvailablity(true);
        bh.consume(0);
    }

    @Benchmark
    public void benchmarkSetCacheZstdAvailablityFalse(Blackhole bh) {
        ZstdUtils.setCacheZstdAvailablity(false);
        bh.consume(0);
    }
}
