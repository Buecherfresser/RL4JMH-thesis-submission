package bench.generated.c043;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.BigDecimal;
import java.math.BigInteger;
import org.decimal4j.factory.Factory7f;
import org.decimal4j.scale.Scale7f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Factory7fBenchmark {

    // Since Factory7f is a singleton enum, we don't need a @State field
    // unless we were benchmarking a mutable instance, which we are not.

    @Benchmark
    public void benchmarkValueOfLong(Blackhole bh) {
        // Call a method that returns a value, consuming it via Blackhole
        Factory7f.INSTANCE.valueOf(123456789L);
        bh.consume(null);
    }

    @Benchmark
    public void benchmarkValueOfDouble(Blackhole bh) {
        // Call a method that returns a value, consuming it via Blackhole
        Factory7f.INSTANCE.valueOf(3.14159);
        bh.consume(null);
    }

    @Benchmark
    public void benchmarkValueOfBigDecimal(Blackhole bh) {
        // Call a method that returns a value, consuming it via Blackhole
        Factory7f.INSTANCE.valueOf(new BigDecimal("123.45"));
        bh.consume(null);
    }

    @Benchmark
    public void benchmarkParseString(Blackhole bh) {
        // Call a method that returns a value, consuming it via Blackhole
        Factory7f.INSTANCE.parse("123456789012345");
        bh.consume(null);
    }

    @Benchmark
    public void benchmarkValueOfUnscaledLong(Blackhole bh) {
        // Call a method that returns a value, consuming it via Blackhole
        Factory7f.INSTANCE.valueOfUnscaled(987654321L);
        bh.consume(null);
    }

    @Benchmark
    public void benchmarkNewMutable(Blackhole bh) {
        // Call a method that returns a value, consuming it via Blackhole
        Factory7f.INSTANCE.newMutable();
        bh.consume(null);
    }

    @Benchmark
    public void benchmarkNewArray(Blackhole bh) {
        // Call a method that returns a value, consuming it via Blackhole
        Factory7f.INSTANCE.newArray(10);
        bh.consume(null);
    }
}
