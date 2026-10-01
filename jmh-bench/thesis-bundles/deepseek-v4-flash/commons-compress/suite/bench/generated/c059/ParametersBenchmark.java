package bench.generated.c059;

import org.openjdk.jmh.annotations.*;
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

    @Setup(Level.Trial)
    public void setup() {
        windowSize = 32768;
        defaultParams = Parameters.builder(windowSize).build();
    }

    @Benchmark
    public Parameters buildDefault() {
        return Parameters.builder(windowSize).build();
    }

    @Benchmark
    public Parameters buildWithAllOptions() {
        return Parameters.builder(windowSize)
                .withMinBackReferenceLength(3)
                .withMaxBackReferenceLength(255)
                .withMaxOffset(32767)
                .withMaxLiteralLength(32768)
                .withMaxNumberOfCandidates(256)
                .withNiceBackReferenceLength(128)
                .withLazyMatching(true)
                .withLazyThreshold(128)
                .build();
    }

    @Benchmark
    public Parameters buildTunedForCompressionRatio() {
        return Parameters.builder(windowSize).tunedForCompressionRatio().build();
    }

    @Benchmark
    public Parameters buildTunedForSpeed() {
        return Parameters.builder(windowSize).tunedForSpeed().build();
    }

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
    public int getLazyMatchingThreshold() {
        return defaultParams.getLazyMatchingThreshold();
    }

    @Benchmark
    public boolean getLazyMatching() {
        return defaultParams.getLazyMatching();
    }
}
