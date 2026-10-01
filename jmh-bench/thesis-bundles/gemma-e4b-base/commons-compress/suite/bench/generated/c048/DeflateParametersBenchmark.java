package bench.generated.c048;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.compressors.deflate.DeflateParameters;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class DeflateParametersBenchmark {

    private DeflateParameters parameters;

    @Setup(Level.Trial)
    public void setup() {
        // Initialize a fresh instance for benchmark scope
        parameters = new DeflateParameters();
    }

    /**
     * Benchmarks reading the current compression level.
     */
    @Benchmark
    public int benchmarkGetCompressionLevel(Blackhole bh) {
        int level = parameters.getCompressionLevel();
        bh.consume(level);
        return level;
    }

    /**
     * Benchmarks setting the compression level to maximum (9).
     */
    @Benchmark
    public void benchmarkSetCompressionLevelMax(Blackhole bh) {
        // Using the known constant value 9 instead of the non-public class field
        parameters.setCompressionLevel(9);
        bh.consume(parameters.getCompressionLevel());
    }

    /**
     * Benchmarks setting the compression level to minimum (0).
     */
    @Benchmark
    public void benchmarkSetCompressionLevelMin(Blackhole bh) {
        // Using the known constant value 0 instead of the non-public class field
        parameters.setCompressionLevel(0);
        bh.consume(parameters.getCompressionLevel());
    }

    /**
     * Benchmarks reading the zlib header presence.
     */
    @Benchmark
    public boolean benchmarkWithZlibHeader(Blackhole bh) {
        boolean hasHeader = parameters.withZlibHeader();
        bh.consume(hasHeader);
        return hasHeader;
    }

    /**
     * Benchmarks setting the zlib header presence to true.
     */
    @Benchmark
    public void benchmarkSetWithZlibHeaderTrue(Blackhole bh) {
        parameters.setWithZlibHeader(true);
        bh.consume(parameters.withZlibHeader());
    }

    /**
     * Benchmarks setting the zlib header presence to false.
     */
    @Benchmark
    public void benchmarkSetWithZlibHeaderFalse(Blackhole bh) {
        parameters.setWithZlibHeader(false);
        bh.consume(parameters.withZlibHeader());
    }
}
