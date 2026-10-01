package bench.generated.c072;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.compressors.xz.XZUtils;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class XZUtilsBenchmark {

    private String compressedFileName;
    private String uncompressedFileName;
    private String nonCompressedFileName;
    private byte[] xzMagic;

    @Setup(Level.Trial)
    public void setup() {
        compressedFileName = "package.txz";
        uncompressedFileName = "package.tar";
        nonCompressedFileName = "package.txt";
        xzMagic = new byte[] { (byte) 0xFD, '7', 'z', 'X', 'Z', 0 };
    }

    @Benchmark
    public String getCompressedFileName() {
        return XZUtils.getCompressedFileName(compressedFileName);
    }

    @Benchmark
    public String getCompressedFilename() {
        return XZUtils.getCompressedFilename(compressedFileName);
    }

    @Benchmark
    public String getUncompressedFileName() {
        return XZUtils.getUncompressedFileName(uncompressedFileName);
    }

    @Benchmark
    public String getUncompressedFilename() {
        return XZUtils.getUncompressedFilename(uncompressedFileName);
    }

    @Benchmark
    public boolean isCompressedFileName() {
        return XZUtils.isCompressedFileName(compressedFileName);
    }

    @Benchmark
    public boolean isCompressedFilename() {
        return XZUtils.isCompressedFilename(compressedFileName);
    }

    @Benchmark
    public boolean isXZCompressionAvailable() {
        return XZUtils.isXZCompressionAvailable();
    }

    @Benchmark
    public boolean matches() {
        return XZUtils.matches(xzMagic, xzMagic.length);
    }
}
