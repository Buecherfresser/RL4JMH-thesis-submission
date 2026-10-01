package bench.generated.c013;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.api.Decimal;
import org.decimal4j.immutable.Decimal0f;
import org.decimal4j.immutable.Decimal1f;
import org.decimal4j.immutable.Decimal2f;
import org.decimal4j.immutable.Decimal3f;
import org.decimal4j.immutable.Decimal4f;
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
import org.decimal4j.scale.Scale14f;
import org.decimal4j.exact.Multipliable14f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Multipliable14fBenchmark {

    // --- State Fields ---
    private Multipliable14f multipliable14f;
    private Decimal<Scale14f> baseDecimal14f;

    // Factors for multiplication tests
    private Decimal0f decimal0f;
    private MutableDecimal0f mutableDecimal0f;
    private Decimal1f decimal1f;
    private MutableDecimal1f mutableDecimal1f;
    private Decimal2f decimal2f;
    private MutableDecimal2f mutableDecimal2f;
    private Decimal3f decimal3f;
    private MutableDecimal3f mutableDecimal3f;
    private Decimal4f decimal4f;
    private MutableDecimal4f mutableDecimal4f;

    // --- Setup ---
    @Setup
    public void setup() {
        // 1. Create a base Decimal<Scale14f> value.
        // Using a simple, non-trivial value for testing arithmetic.
        // Example: 123.45678901234567 (14 decimal places)
        java.math.BigDecimal bd = new java.math.BigDecimal("123.45678901234567");
        this.baseDecimal14f = Decimal14f.valueOf(bd);
        this.multipliable14f = new Multipliable14f(this.baseDecimal14f);

        // 2. Create factor instances (using simple values for testing)
        this.decimal0f = Decimal0f.valueOf(1L);
        this.mutableDecimal0f = MutableDecimal0f.zero();

        this.decimal1f = Decimal1f.valueOf(1.0);
        this.mutableDecimal1f = MutableDecimal1f.one();

        this.decimal2f = Decimal2f.valueOf(2.0);
        this.mutableDecimal2f = MutableDecimal2f.two();

        this.decimal3f = Decimal3f.valueOf(3.0);
        this.mutableDecimal3f = MutableDecimal3f.three();

        this.decimal4f = Decimal4f.valueOf(4.0);
        this.mutableDecimal4f = MutableDecimal4f.four();
    }

    // --- Benchmarks ---

    @Benchmark
    public Decimal14f benchmarkByDecimal0f(Blackhole bh) {
        Decimal14f result = multipliable14f.by(decimal0f);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal14f benchmarkByMutableDecimal0f(Blackhole bh) {
        Decimal14f result = multipliable14f.by(mutableDecimal0f);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal15f benchmarkByDecimal1f(Blackhole bh) {
        Decimal15f result = multipliable14f.by(decimal1f);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal15f benchmarkByMutableDecimal1f(Blackhole bh) {
        Decimal15f result = multipliable14f.by(mutableDecimal1f);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal16f benchmarkByDecimal2f(Blackhole bh) {
        Decimal16f result = multipliable14f.by(decimal2f);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal16f benchmarkByMutableDecimal2f(Blackhole bh) {
        Decimal16f result = multipliable14f.by(mutableDecimal2f);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal17f benchmarkByDecimal3f(Blackhole bh) {
        Decimal17f result = multipliable14f.by(decimal3f);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal17f benchmarkByMutableDecimal3f(Blackhole bh) {
        Decimal17f result = multipliable14f.by(mutableDecimal3f);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal18f benchmarkByDecimal4f(Blackhole bh) {
        Decimal18f result = multipliable14f.by(decimal4f);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal18f benchmarkByMutableDecimal4f(Blackhole bh) {
        Decimal18f result = multipliable14f.by(mutableDecimal4f);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Integer benchmarkHashCode(Blackhole bh) {
        int hash = multipliable14f.hashCode();
        bh.consume(hash);
        return hash;
    }
}
