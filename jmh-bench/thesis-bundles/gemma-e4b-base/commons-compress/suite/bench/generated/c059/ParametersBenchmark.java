package bench.generated.c059;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.compressors.lz77support.Parameters;
import org.apache.commons.compress.compressors.lz77support.Parameters.Builder;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ParametersBenchmark {

    private static final int WINDOW_SIZE = 4096; // Must be a power of two

    private Builder baseBuilder;

    @Setup
    public void setup() {
        // Initialize a base builder for repeated use
        baseBuilder = Parameters.builder(WINDOW_SIZE);
    }

    /**
     * Benchmarks the default construction of Parameters using the builder.
     */
    @Benchmark
    public Parameters benchmarkDefaultConstruction(Blackhole bh) {
        Parameters p = baseBuilder.build();
        bh.consume(p);
        return p;
    }

    /**
     * Benchmarks construction when tuned for compression ratio.
     */
    @Benchmark
    public Parameters benchmarkTunedForCompressionRatio(Blackhole bh) {
        Parameters p = baseBuilder.tunedForCompressionRatio().build();
        bh.consume(p);
        return p;
    }

    /**
     * Benchmarks construction when tuned for speed.
     */
    @Benchmark
    public Parameters benchmarkTunedForSpeed(Blackhole bh) {
        Parameters p = baseBuilder.tunedForSpeed().build();
        bh.consume(p);
        return p;
    }

    /**
     * Benchmarks setting a single parameter (Lazy Matching).
     */
    @Benchmark
    public Parameters benchmarkSetLazyMatching(Blackhole bh) {
        Parameters p = baseBuilder.withLazyMatching(true).build();
        bh.consume(p);
        return p;
    }

    /**
     * Benchmarks setting a single parameter (Max Back Reference Length).
     */
    @Benchmark
    public Parameters benchmarkSetMaxBackReferenceLength(Blackhole bh) {
        Parameters p = baseBuilder.withMaxBackReferenceLength(2048).build();
        bh.consume(p);
        return p;
    }

    /**
     * Benchmarks setting a single parameter (Max Candidates).
     */
    @Benchmark
    public Parameters benchmarkSetMaxCandidates(Blackhole bh) {
        Parameters p = baseBuilder.withMaxNumberOfCandidates(512).build();
        bh.consume(p);
        return p;
    }

    /**
     * Benchmarks setting a single parameter (Nice Back Reference Length).
     */
    @Benchmark
    public Parameters benchmarkSetNiceBackReferenceLength(Blackhole bh) {
        Parameters p = baseBuilder.withNiceBackReferenceLength(128).build();
        bh.consume(p);
        return p;
    }

    /**
     * Benchmarks setting multiple parameters (Max Back Ref Length and Lazy Matching).
     */
    @Benchmark
    public Parameters benchmarkSetMultipleParameters(Blackhole bh) {
        Parameters p = baseBuilder
                .withMaxBackReferenceLength(1024)
                .withLazyMatching(false)
                .build();
        bh.consume(p);
        return p;
    }

    /**
     * Benchmarks setting multiple parameters (Max Candidates and Lazy Threshold).
     */
    @Benchmark
    public Parameters benchmarkSetComplexParameters(Blackhole bh) {
        Parameters p = baseBuilder
                .withMaxNumberOfCandidates(100)
                .withLazyThreshold(64)
                .build();
        bh.consume(p);
        return p;
    }
}
