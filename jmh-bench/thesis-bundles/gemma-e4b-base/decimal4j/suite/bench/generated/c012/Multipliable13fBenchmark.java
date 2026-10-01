package bench.generated.c012;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.exact.Multipliable13f;
import org.decimal4j.immutable.Decimal0f;
import org.decimal4j.immutable.Decimal1f;
import org.decimal4j.immutable.Decimal2f;
import org.decimal4j.immutable.Decimal3f;
import org.decimal4j.immutable.Decimal4f;
import org.decimal4j.immutable.Decimal5f;
import org.decimal4j.immutable.Decimal13f;
import org.decimal4j.mutable.MutableDecimal0f;
import org.decimal4j.mutable.MutableDecimal1f;
import org.decimal4j.mutable.MutableDecimal2f;
import org.decimal4j.mutable.MutableDecimal3f;
import org.decimal4j.mutable.MutableDecimal4f;
import org.decimal4j.mutable.MutableDecimal5f;
import org.decimal4j.scale.Scale13f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Multipliable13fBenchmark {

    private Multipliable13f multipliable;
    private Multipliable13f comparisonMultipliable;

    @Setup(Level.Trial)
    public void setup() {
        // Base value: 1.0 in scale 13
        Decimal13f baseValue = Decimal13f.valueOf(1L);
        multipliable = new Multipliable13f(baseValue);

        // Comparison value: 2.0 in scale 13
        Decimal13f comparisonValue = Decimal13f.valueOf(2L);
        comparisonMultipliable = new Multipliable13f(comparisonValue);
    }

    @Benchmark
    public void testGetValue(Blackhole bh) {
        bh.consume(multipliable.getValue());
    }

    @Benchmark
    public void testHashCode(Blackhole bh) {
        bh.consume(multipliable.hashCode());
    }

    @Benchmark
    public void testEquals(Blackhole bh) {
        // Test equality against self
        bh.consume(multipliable.equals(multipliable));
        // Test inequality against different value
        bh.consume(multipliable.equals(comparisonMultipliable));
        // Test inequality against null
        bh.consume(multipliable.equals(null));
    }

    @Benchmark
    public void testToString(Blackhole bh) {
        bh.consume(multipliable.toString());
    }

    // --- Multiplication Benchmarks (by(DecimalXf factor)) ---

    @Benchmark
    public void testByDecimal0f(Blackhole bh) {
        Decimal0f factor = Decimal0f.ONE;
        bh.consume(multipliable.by(factor));
    }

    @Benchmark
    public void testByDecimal1f(Blackhole bh) {
        Decimal1f factor = Decimal1f.ONE;
        bh.consume(multipliable.by(factor));
    }

    @Benchmark
    public void testByDecimal2f(Blackhole bh) {
        Decimal2f factor = Decimal2f.ONE;
        bh.consume(multipliable.by(factor));
    }

    @Benchmark
    public void testByDecimal3f(Blackhole bh) {
        Decimal3f factor = Decimal3f.ONE;
        bh.consume(multipliable.by(factor));
    }

    @Benchmark
    public void testByDecimal4f(Blackhole bh) {
        Decimal4f factor = Decimal4f.ONE;
        bh.consume(multipliable.by(factor));
    }

    @Benchmark
    public void testByDecimal5f(Blackhole bh) {
        Decimal5f factor = Decimal5f.ONE;
        bh.consume(multipliable.by(factor));
    }

    // --- Multiplication Benchmarks (by(MutableDecimalXf factor)) ---

    @Benchmark
    public void testByMutableDecimal0f(Blackhole bh) {
        MutableDecimal0f factor = MutableDecimal0f.one();
        bh.consume(multipliable.by(factor));
    }

    @Benchmark
    public void testByMutableDecimal1f(Blackhole bh) {
        MutableDecimal1f factor = MutableDecimal1f.one();
        bh.consume(multipliable.by(factor));
    }

    @Benchmark
    public void testByMutableDecimal2f(Blackhole bh) {
        MutableDecimal2f factor = MutableDecimal2f.one();
        bh.consume(multipliable.by(factor));
    }

    @Benchmark
    public void testByMutableDecimal3f(Blackhole bh) {
        MutableDecimal3f factor = MutableDecimal3f.one();
        bh.consume(multipliable.by(factor));
    }

    @Benchmark
    public void testByMutableDecimal4f(Blackhole bh) {
        MutableDecimal4f factor = MutableDecimal4f.one();
        bh.consume(multipliable.by(factor));
    }

    @Benchmark
    public void testByMutableDecimal5f(Blackhole bh) {
        MutableDecimal5f factor = MutableDecimal5f.one();
        bh.consume(multipliable.by(factor));
    }
}
