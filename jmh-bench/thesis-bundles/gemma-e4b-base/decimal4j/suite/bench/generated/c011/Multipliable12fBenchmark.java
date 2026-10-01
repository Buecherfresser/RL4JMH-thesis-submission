package bench.generated.c011;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.exact.Multipliable12f;
import org.decimal4j.immutable.Decimal0f;
import org.decimal4j.immutable.Decimal1f;
import org.decimal4j.immutable.Decimal2f;
import org.decimal4j.immutable.Decimal3f;
import org.decimal4j.immutable.Decimal4f;
import org.decimal4j.immutable.Decimal5f;
import org.decimal4j.immutable.Decimal6f;
import org.decimal4j.mutable.MutableDecimal0f;
import org.decimal4j.mutable.MutableDecimal1f;
import org.decimal4j.mutable.MutableDecimal2f;
import org.decimal4j.mutable.MutableDecimal3f;
import org.decimal4j.mutable.MutableDecimal4f;
import org.decimal4j.mutable.MutableDecimal5f;
import org.decimal4j.mutable.MutableDecimal6f;
import org.decimal4j.scale.Scale12f;
import org.decimal4j.api.Decimal;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Multipliable12fBenchmark {

    private Multipliable12f multipliable;

    // Factors for multiplication tests
    private Decimal0f factor0f;
    private MutableDecimal0f factorM0f;
    private Decimal1f factor1f;
    private MutableDecimal1f factorM1f;
    private Decimal2f factor2f;
    private MutableDecimal2f factorM2f;
    private Decimal3f factor3f;
    private MutableDecimal3f factorM3f;
    private Decimal4f factor4f;
    private MutableDecimal4f factorM4f;
    private Decimal5f factor5f;
    private MutableDecimal5f factorM5f;
    private Decimal6f factor6f;
    private MutableDecimal6f factorM6f;

    @Setup(Level.Trial)
    public void setup() {
        // Base value (Decimal12f)
        Decimal<Scale12f> baseValue = org.decimal4j.immutable.Decimal12f.valueOf(123456789012L);
        multipliable = new Multipliable12f(baseValue);

        // Factors
        factor0f = Decimal0f.valueOf(5L);
        factorM0f = new MutableDecimal0f(5L);

        factor1f = Decimal1f.valueOf(2L);
        factorM1f = new MutableDecimal1f(2L);

        factor2f = Decimal2f.valueOf(3L);
        factorM2f = new MutableDecimal2f(3L);

        factor3f = Decimal3f.valueOf(4L);
        factorM3f = new MutableDecimal3f(4L);

        factor4f = Decimal4f.valueOf(5L);
        factorM4f = new MutableDecimal4f(5L);

        factor5f = Decimal5f.valueOf(6L);
        factorM5f = new MutableDecimal5f(6L);

        factor6f = Decimal6f.valueOf(7L);
        factorM6f = new MutableDecimal6f(7L);
    }

    @Benchmark
    public void benchmarkGetValue(Blackhole bh) {
        bh.consume(multipliable.getValue());
    }

    @Benchmark
    public void benchmarkToString(Blackhole bh) {
        bh.consume(multipliable.toString());
    }

    @Benchmark
    public void benchmarkHashCode(Blackhole bh) {
        bh.consume(multipliable.hashCode());
    }

    @Benchmark
    public void benchmarkEquals(Blackhole bh) {
        // Test against self
        bh.consume(multipliable.equals(multipliable));
        // Test against null
        bh.consume(multipliable.equals(null));
        // Test against different type
        bh.consume(multipliable.equals("not_a_multipliable"));
    }

    // --- Multiplication Benchmarks ---

    @Benchmark
    public void benchmarkByDecimal0f(Blackhole bh) {
        bh.consume(multipliable.by(factor0f));
    }

    @Benchmark
    public void benchmarkByMutableDecimal0f(Blackhole bh) {
        bh.consume(multipliable.by(factorM0f));
    }

    @Benchmark
    public void benchmarkByDecimal1f(Blackhole bh) {
        bh.consume(multipliable.by(factor1f));
    }

    @Benchmark
    public void benchmarkByMutableDecimal1f(Blackhole bh) {
        bh.consume(multipliable.by(factorM1f));
    }

    @Benchmark
    public void benchmarkByDecimal2f(Blackhole bh) {
        bh.consume(multipliable.by(factor2f));
    }

    @Benchmark
    public void benchmarkByMutableDecimal2f(Blackhole bh) {
        bh.consume(multipliable.by(factorM2f));
    }

    @Benchmark
    public void benchmarkByDecimal3f(Blackhole bh) {
        bh.consume(multipliable.by(factor3f));
    }

    @Benchmark
    public void benchmarkByMutableDecimal3f(Blackhole bh) {
        bh.consume(multipliable.by(factorM3f));
    }

    @Benchmark
    public void benchmarkByDecimal4f(Blackhole bh) {
        bh.consume(multipliable.by(factor4f));
    }

    @Benchmark
    public void benchmarkByMutableDecimal4f(Blackhole bh) {
        bh.consume(multipliable.by(factorM4f));
    }

    @Benchmark
    public void benchmarkByDecimal5f(Blackhole bh) {
        bh.consume(multipliable.by(factor5f));
    }

    @Benchmark
    public void benchmarkByMutableDecimal5f(Blackhole bh) {
        bh.consume(multipliable.by(factorM5f));
    }

    @Benchmark
    public void benchmarkByDecimal6f(Blackhole bh) {
        bh.consume(multipliable.by(factor6f));
    }

    @Benchmark
    public void benchmarkByMutableDecimal6f(Blackhole bh) {
        bh.consume(multipliable.by(factorM6f));
    }
}
