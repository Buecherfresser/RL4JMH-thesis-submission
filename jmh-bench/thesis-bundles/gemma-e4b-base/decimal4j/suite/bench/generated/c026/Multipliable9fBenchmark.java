package bench.generated.c026;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.exact.Multipliable9f;
import org.decimal4j.immutable.Decimal0f;
import org.decimal4j.immutable.Decimal1f;
import org.decimal4j.immutable.Decimal2f;
import org.decimal4j.immutable.Decimal3f;
import org.decimal4j.immutable.Decimal4f;
import org.decimal4j.immutable.Decimal5f;
import org.decimal4j.immutable.Decimal6f;
import org.decimal4j.immutable.Decimal7f;
import org.decimal4j.immutable.Decimal8f;
import org.decimal4j.immutable.Decimal9f;
import org.decimal4j.immutable.Decimal10f;
import org.decimal4j.immutable.Decimal11f;
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
import org.decimal4j.mutable.MutableDecimal7f;
import org.decimal4j.mutable.MutableDecimal8f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Multipliable9fBenchmark {

    private Multipliable9f multipliable9f;

    // Factors for by(Decimal<Scale9f> factor)
    private org.decimal4j.api.Decimal<org.decimal4j.scale.Scale9f> genericFactor;

    // Factors for by(DecimalNf factor)
    private Decimal0f factor0f;
    private org.decimal4j.mutable.MutableDecimal0f mutableFactor0f;
    private Decimal1f factor1f;
    private org.decimal4j.mutable.MutableDecimal1f mutableFactor1f;
    private Decimal2f factor2f;
    private org.decimal4j.mutable.MutableDecimal2f mutableFactor2f;
    private Decimal3f factor3f;
    private org.decimal4j.mutable.MutableDecimal3f mutableFactor3f;
    private Decimal4f factor4f;
    private org.decimal4j.mutable.MutableDecimal4f mutableFactor4f;
    private Decimal5f factor5f;
    private org.decimal4j.mutable.MutableDecimal5f mutableFactor5f;
    private Decimal6f factor6f;
    private org.decimal4j.mutable.MutableDecimal6f mutableFactor6f;
    private Decimal7f factor7f;
    private org.decimal4j.mutable.MutableDecimal7f mutableFactor7f;
    private Decimal8f factor8f;
    private org.decimal4j.mutable.MutableDecimal8f mutableFactor8f;

    @Setup(Level.Trial)
    public void setup() {
        // Base value (e.g., 1.23456789)
        org.decimal4j.api.Decimal<org.decimal4j.scale.Scale9f> baseValue = Decimal9f.valueOf(123456789L);
        multipliable9f = new Multipliable9f(baseValue);

        // Generic factor
        genericFactor = Decimal9f.valueOf(2L);

        // Initialize factors
        factor0f = Decimal0f.valueOf(1L);
        mutableFactor0f = new org.decimal4j.mutable.MutableDecimal0f(1L);

        factor1f = Decimal1f.valueOf(2L);
        mutableFactor1f = new org.decimal4j.mutable.MutableDecimal1f(2L);

        factor2f = Decimal2f.valueOf(3L);
        mutableFactor2f = new org.decimal4j.mutable.MutableDecimal2f(3L);

        factor3f = Decimal3f.valueOf(4L);
        mutableFactor3f = new org.decimal4j.mutable.MutableDecimal3f(4L);

        factor4f = Decimal4f.valueOf(5L);
        mutableFactor4f = new org.decimal4j.mutable.MutableDecimal4f(5L);

        factor5f = Decimal5f.valueOf(6L);
        mutableFactor5f = new org.decimal4j.mutable.MutableDecimal5f(6L);

        factor6f = Decimal6f.valueOf(7L);
        mutableFactor6f = new org.decimal4j.mutable.MutableDecimal6f(7L);

        factor7f = Decimal7f.valueOf(8L);
        mutableFactor7f = new org.decimal4j.mutable.MutableDecimal7f(8L);

        factor8f = Decimal8f.valueOf(9L);
        mutableFactor8f = new org.decimal4j.mutable.MutableDecimal8f(9L);
    }

    @Benchmark
    public org.decimal4j.api.Decimal<org.decimal4j.scale.Scale9f> testGetValue(Blackhole bh) {
        org.decimal4j.api.Decimal<org.decimal4j.scale.Scale9f> result = multipliable9f.getValue();
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal18f testSquare(Blackhole bh) {
        Decimal18f result = multipliable9f.square();
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal18f testByGenericDecimal(Blackhole bh) {
        Decimal18f result = multipliable9f.by(genericFactor);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal9f testByDecimal0f(Blackhole bh) {
        Decimal9f result = multipliable9f.by(factor0f);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal9f testByMutableDecimal0f(Blackhole bh) {
        Decimal9f result = multipliable9f.by(mutableFactor0f);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal10f testByDecimal1f(Blackhole bh) {
        Decimal10f result = multipliable9f.by(factor1f);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal10f testByMutableDecimal1f(Blackhole bh) {
        Decimal10f result = multipliable9f.by(mutableFactor1f);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal11f testByDecimal2f(Blackhole bh) {
        Decimal11f result = multipliable9f.by(factor2f);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal11f testByMutableDecimal2f(Blackhole bh) {
        Decimal11f result = multipliable9f.by(mutableFactor2f);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal12f testByDecimal3f(Blackhole bh) {
        Decimal12f result = multipliable9f.by(factor3f);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal12f testByMutableDecimal3f(Blackhole bh) {
        Decimal12f result = multipliable9f.by(mutableFactor3f);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal13f testByDecimal4f(Blackhole bh) {
        Decimal13f result = multipliable9f.by(factor4f);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal13f testByMutableDecimal4f(Blackhole bh) {
        Decimal13f result = multipliable9f.by(mutableFactor4f);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal14f testByDecimal5f(Blackhole bh) {
        Decimal14f result = multipliable9f.by(factor5f);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal14f testByMutableDecimal5f(Blackhole bh) {
        Decimal14f result = multipliable9f.by(mutableFactor5f);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal15f testByDecimal6f(Blackhole bh) {
        Decimal15f result = multipliable9f.by(factor6f);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal15f testByMutableDecimal6f(Blackhole bh) {
        Decimal15f result = multipliable9f.by(mutableFactor6f);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal16f testByDecimal7f(Blackhole bh) {
        Decimal16f result = multipliable9f.by(factor7f);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal16f testByMutableDecimal7f(Blackhole bh) {
        Decimal16f result = multipliable9f.by(mutableFactor7f);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal17f testByDecimal8f(Blackhole bh) {
        Decimal17f result = multipliable9f.by(factor8f);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal17f testByMutableDecimal8f(Blackhole bh) {
        Decimal17f result = multipliable9f.by(mutableFactor8f);
        bh.consume(result);
        return result;
    }
}
