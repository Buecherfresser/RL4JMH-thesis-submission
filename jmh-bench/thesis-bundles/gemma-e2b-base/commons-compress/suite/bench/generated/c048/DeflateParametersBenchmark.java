package bench.generated.c048;

import org.apache.commons.compress.compressors.deflate.DeflateParameters;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class DeflateParametersBenchmark {

    private DeflateParameters parameters;

    @Setup
    public void setup() {
        parameters = new DeflateParameters();
    }

    // --- Benchmarks for setting compression level ---

    @Benchmark
    public void setCompressionLevel_Max() {
        try {
            parameters.setCompressionLevel(9);
        } catch (IllegalArgumentException e) {
            // Should not happen with 9
        }
    }

    @Benchmark
    public void setCompressionLevel_Min() {
        try {
            parameters.setCompressionLevel(0);
        } catch (IllegalArgumentException e) {
            // Should not happen with 0
        }
    }

    @Benchmark
    public void setCompressionLevel_Mid() {
        try {
            parameters.setCompressionLevel(4);
        } catch (IllegalArgumentException e) {
            // Should not happen
        }
    }

    @Benchmark
    public void setCompressionLevel_InvalidHigh() {
        try {
            parameters.setCompressionLevel(10);
        } catch (IllegalArgumentException e) {
            // Expected exception
        }
    }

    @Benchmark
    public void setCompressionLevel_InvalidLow() {
        try {
            parameters.setCompressionLevel(-1);
        } catch (IllegalArgumentException e) {
            // Expected exception
        }
    }

    // --- Benchmarks for setting zlib header ---

    @Benchmark
    public void setWithZlibHeader_True() {
        parameters.setWithZlibHeader(true);
    }

    @Benchmark
    public void setWithZlibHeader_False() {
        parameters.setWithZlibHeader(false);
    }

    // --- Benchmarks for reading parameters ---

    @Benchmark
    public int getCompressionLevel() {
        return parameters.getCompressionLevel();
    }

    @Benchmark
    public boolean withZlibHeader() {
        return parameters.withZlibHeader();
    }
}
