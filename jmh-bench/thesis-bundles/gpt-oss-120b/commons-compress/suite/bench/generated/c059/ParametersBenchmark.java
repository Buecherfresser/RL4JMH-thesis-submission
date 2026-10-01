package bench.generated.c059;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.compressors.lz77support.Parameters;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ParametersBenchmark {

    private int windowSize;
    private Parameters defaultParams;

    @Setup
    public void setUp() {
        // Choose a reasonable power‑of‑two window size
        this.windowSize = 1024;
        this.defaultParams = Parameters.builder(windowSize).build();
    }

    // -------------------------------------------------------------------------
    // Builder benchmarks
    // -------------------------------------------------------------------------

    @Benchmark
    public Parameters buildDefault() {
        return Parameters.builder(windowSize).build();
    }

    @Benchmark
    public Parameters buildTunedForCompressionRatio() {
        return Parameters.builder(windowSize)
                .tunedForCompressionRatio()
                .build();
    }

    @Benchmark
    public Parameters buildTunedForSpeed() {
        return Parameters.builder(windowSize)
                .tunedForSpeed()
                .build();
    }

    @Benchmark
    public Parameters buildWithMaxBackReferenceLength() {
        return Parameters.builder(windowSize)
                .withMaxBackReferenceLength(200)
                .build();
    }

    @Benchmark
    public Parameters buildWithMaxLiteralLength() {
        return Parameters.builder(windowSize)
                .withMaxLiteralLength(500)
                .build();
    }

    @Benchmark
    public Parameters buildWithMaxOffset() {
        return Parameters.builder(windowSize)
                .withMaxOffset(300)
                .build();
    }

    @Benchmark
    public Parameters buildWithLazyMatching() {
        return Parameters.builder(windowSize)
                .withLazyMatching(false)
                .build();
    }

    @Benchmark
    public Parameters buildWithLazyThreshold() {
        return Parameters.builder(windowSize)
                .withLazyThreshold(10)
                .build();
    }

    @Benchmark
    public Parameters buildWithNiceBackReferenceLength() {
        return Parameters.builder(windowSize)
                .withNiceBackReferenceLength(15)
                .build();
    }

    @Benchmark
    public Parameters buildWithMinBackReferenceLength() {
        return Parameters.builder(windowSize)
                .withMinBackReferenceLength(5)
                .build();
    }

    @Benchmark
    public Parameters buildWithMaxCandidates() {
        return Parameters.builder(windowSize)
                .withMaxNumberOfCandidates(50)
                .build();
    }

    // -------------------------------------------------------------------------
    // Getter benchmarks (read‑only, safe to reuse the same instance)
    // -------------------------------------------------------------------------

    @Benchmark
    public int getWindowSize() {
        return defaultParams.getWindowSize();
    }

    @Benchmark
    public int getMinBackReferenceLength() {
        return defaultParams.getMinBackReferenceLength();
    }

    @Benchmark
    public int getMaxBackReferenceLength() {
        return defaultParams.getMaxBackReferenceLength();
    }

    @Benchmark
    public int getMaxOffset() {
        return defaultParams.getMaxOffset();
    }

    @Benchmark
    public int getMaxLiteralLength() {
        return defaultParams.getMaxLiteralLength();
    }

    @Benchmark
    public int getNiceBackReferenceLength() {
        return defaultParams.getNiceBackReferenceLength();
    }

    @Benchmark
    public int getMaxCandidates() {
        return defaultParams.getMaxCandidates();
    }

    @Benchmark
    public boolean getLazyMatching() {
        return defaultParams.getLazyMatching();
    }

    @Benchmark
    public int getLazyMatchingThreshold() {
        return defaultParams.getLazyMatchingThreshold();
    }
}
