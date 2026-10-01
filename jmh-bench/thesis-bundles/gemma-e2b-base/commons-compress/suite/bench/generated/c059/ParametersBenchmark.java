package bench.generated.c059;

import org.apache.commons.compress.compressors.lz77support.Parameters;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class ParametersBenchmark {

    private Parameters parameters;

    @Setup
    public void setup() {
        // Define a standard window size (must be a power of two)
        final int windowSize = 1024;

        // 1. Standard configuration
        Parameters standardParams = Parameters.builder(windowSize).build();
        this.parameters = standardParams;

        // 2. Configuration tuned for compression ratio
        Parameters compressionParams = Parameters.builder(windowSize)
                .tunedForCompressionRatio()
                .build();

        // 3. Configuration tuned for speed
        Parameters speedParams = Parameters.builder(windowSize)
                .tunedForSpeed()
                .build();
        
        // We will use the standard parameters for most benchmarks, 
        // but store the others for specific tests.
    }

    @Benchmark
    public void buildStandardParameters(Blackhole bh) {
        Parameters p = Parameters.builder(1024).build();
        bh.consume(p);
    }

    @Benchmark
    public void buildCompressionTunedParameters(Blackhole bh) {
        Parameters p = Parameters.builder(1024)
                .tunedForCompressionRatio()
                .build();
        bh.consume(p);
    }

    @Benchmark
    public void buildSpeedTunedParameters(Blackhole bh) {
        Parameters p = Parameters.builder(1024)
                .tunedForSpeed()
                .build();
        bh.consume(p);
    }

    @Benchmark
    public void getWindowSize(Blackhole bh) {
        int size = parameters.getWindowSize();
        bh.consume(size);
    }

    @Benchmark
    public void getMaxBackReferenceLength(Blackhole bh) {
        int length = parameters.getMaxBackReferenceLength();
        bh.consume(length);
    }

    @Benchmark
    public void getMaxCandidates(Blackhole bh) {
        int candidates = parameters.getMaxCandidates();
        bh.consume(candidates);
    }

    @Benchmark
    public void getLazyMatching(Blackhole bh) {
        boolean lazy = parameters.getLazyMatching();
        bh.consume(lazy);
    }
}
