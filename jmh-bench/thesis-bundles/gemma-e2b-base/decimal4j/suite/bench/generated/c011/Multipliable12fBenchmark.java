package bench.generated.c011;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

import org.decimal4j.api.Decimal;
import org.decimal4j.immutable.Decimal0f;
import org.decimal4j.immutable.Decimal1f;
import org.decimal4j.immutable.Decimal2f;
import org.decimal4j.immutable.Decimal3f;
import org.decimal4j.immutable.Decimal4f;
import org.decimal4j.immutable.Decimal5f;
import org.decimal4j.immutable.Decimal6f;
import org.decimal4j.immutable.Decimal12f;
import org.decimal4j.immutable.Decimal13f;
import org.decimal4j.immutable.Decimal14f;
import org.decimal4j.immutable.Decimal15f;
import org.decimal4j.immutable.Decimal16f;
import org.decimal4j.immutable.Decimal17f;
import org.decimal4j.immutable.Decimal18f;
import org.decimal4j.mutable.MutableDecimal0f;
import org.decimal4j.mutable.MutableDecimal1f;
import org.decimal4j.mutable.MutableDecimal2f;
import org.decimal4j.mutable.MutableDecimal3f;
import org.decimal4j.mutable.MutableDecimal4f;
import org.decimal4j.mutable.MutableDecimal5f;
import org.decimal4j.mutable.MutableDecimal6f;
import org.decimal4j.scale.Scale12f;
import org.decimal4j.exact.Multipliable12f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Multipliable12fBenchmark {

    private Multipliable12f multipliable12f;

    // Setup phase: Build inputs once per trial
    @Setup(Level.Trial)
    public void setup() {
        // 1. Create the base Decimal<Scale12f> value
        // Using a simple, non-literal value for the base decimal.
        // We use Decimal12f.valueOf(1000000000000L) as a representative value.
        Decimal<Scale12f> baseValue = Decimal12f.valueOf(1000000000000L);
        this.multipliable12f = new Multipliable12f(baseValue);
    }

    // --- Benchmarks for Decimal0f factors ---

    @Benchmark
    public void by_Decimal0f(Blackhole bh) {
        Decimal12f result = multipliable12f.by(Decimal0f.ZERO);
        bh.consume(result);
    }

    @Benchmark
    public void by_MutableDecimal0f(Blackhole bh) {
        MutableDecimal0f factor = MutableDecimal0f.zero();
        Decimal12f result = multipliable12f.by(factor);
        bh.consume(result);
    }

    // --- Benchmarks for Decimal1f factors ---

    @Benchmark
    public void by_Decimal1f(Blackhole bh) {
        Decimal13f result = multipliable12f.by(Decimal1f.ONE);
        bh.consume(result);
    }

    @Benchmark
    public void by_MutableDecimal1f(Blackhole bh) {
        MutableDecimal1f factor = MutableDecimal1f.one();
        Decimal13f result = multipliable12f.by(factor);
        bh.consume(result);
    }

    // --- Benchmarks for Decimal2f factors ---

    @Benchmark
    public void by_Decimal2f(Blackhole bh) {
        Decimal14f result = multipliable12f.by(Decimal2f.FIVE);
        bh.consume(result);
    }

    @Benchmark
    public void by_MutableDecimal2f(Blackhole bh) {
        MutableDecimal2f factor = MutableDecimal2f.five();
        Decimal14f result = multipliable12f.by(factor);
        bh.consume(result);
    }

    // --- Benchmarks for Decimal3f factors ---

    @Benchmark
    public void by_Decimal3f(Blackhole bh) {
        Decimal15f result = multipliable12f.by(Decimal3f.TEN);
        bh.consume(result);
    }

    @Benchmark
    public void by_MutableDecimal3f(Blackhole bh) {
        MutableDecimal3f factor = MutableDecimal3f.ten();
        Decimal15f result = multipliable12f.by(factor);
        bh.consume(result);
    }

    // --- Benchmarks for Decimal4f factors ---

    @Benchmark
    public void by_Decimal4f(Blackhole bh) {
        Decimal16f result = multipliable12f.by(Decimal4f.ONE);
        bh.consume(result);
    }

    @Benchmark
    public void by_MutableDecimal4f(Blackhole bh) {
        MutableDecimal4f factor = MutableDecimal4f.one();
        Decimal16f result = multipliable12f.by(factor);
        bh.consume(result);
    }

    // --- Benchmarks for Decimal5f factors ---

    @Benchmark
    public void by_Decimal5f(Blackhole bh) {
        Decimal17f result = multipliable12f.by(Decimal5f.TWO);
        bh.consume(result);
    }

    @Benchmark
    public void by_MutableDecimal5f(Blackhole bh) {
        MutableDecimal5f factor = MutableDecimal5f.two();
        Decimal17f result = multipliable12f.by(factor);
        bh.consume(result);
    }

    // --- Benchmarks for Decimal6f factors ---

    @Benchmark
    public void by_Decimal6f(Blackhole bh) {
        Decimal18f result = multipliable12f.by(Decimal6f.TEN);
        bh.consume(result);
    }

    @Benchmark
    public void by_MutableDecimal6f(Blackhole bh) {
        MutableDecimal6f factor = MutableDecimal6f.ten();
        Decimal18f result = multipliable12f.by(factor);
        bh.consume(result);
    }
}
