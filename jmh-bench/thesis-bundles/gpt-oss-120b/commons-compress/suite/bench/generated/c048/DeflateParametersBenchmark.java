package bench.generated.c048;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.compressors.deflate.DeflateParameters;

@State(org.openjdk.jmh.annotations.Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class DeflateParametersBenchmark {

    private DeflateParameters params;
    private int levelToSet;
    private boolean headerFlag;

    @Setup
    public void setup() {
        params = new DeflateParameters();
        // Use a mid‑range compression level for the setter benchmark
        levelToSet = 5;
        // Toggle the header flag for the setter benchmark
        headerFlag = false;
    }

    @Benchmark
    public int benchmarkGetCompressionLevel() {
        return params.getCompressionLevel();
    }

    @Benchmark
    public void benchmarkSetCompressionLevel(Blackhole bh) {
        params.setCompressionLevel(levelToSet);
        bh.consume(params);
    }

    @Benchmark
    public boolean benchmarkWithZlibHeader() {
        return params.withZlibHeader();
    }

    @Benchmark
    public void benchmarkSetWithZlibHeader(Blackhole bh) {
        params.setWithZlibHeader(headerFlag);
        bh.consume(params);
    }
}
