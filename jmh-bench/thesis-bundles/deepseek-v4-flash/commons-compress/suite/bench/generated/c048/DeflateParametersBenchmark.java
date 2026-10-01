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

    private DeflateParameters params;

    @Setup(Level.Trial)
    public void setup() {
        params = new DeflateParameters();
    }

    @Benchmark
    public int getCompressionLevel() {
        return params.getCompressionLevel();
    }

    @Benchmark
    public void setCompressionLevelValid(Blackhole bh) {
        params.setCompressionLevel(5);
        bh.consume(params);
    }

    @Benchmark
    public boolean setCompressionLevelInvalid() {
        try {
            params.setCompressionLevel(10);
            return false;
        } catch (IllegalArgumentException e) {
            return true;
        }
    }

    @Benchmark
    public void setWithZlibHeader(Blackhole bh) {
        params.setWithZlibHeader(true);
        bh.consume(params);
    }

    @Benchmark
    public boolean withZlibHeader() {
        return params.withZlibHeader();
    }
}
