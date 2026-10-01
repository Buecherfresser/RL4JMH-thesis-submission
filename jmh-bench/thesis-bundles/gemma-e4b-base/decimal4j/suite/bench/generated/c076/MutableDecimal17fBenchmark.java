package bench.generated.c076;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import org.decimal4j.mutable.MutableDecimal17f;
import org.decimal4j.immutable.Decimal17f;
import org.decimal4j.scale.Scale17f;
import org.decimal4j.factory.Factory17f;
import org.decimal4j.api.Decimal;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class MutableDecimal17fBenchmark {

    private MutableDecimal17f mdfLong;
    private MutableDecimal17f mdfDouble;
    private MutableDecimal17f mdfString;
    private MutableDecimal17f mdfBigDecimal;
    private MutableDecimal17f mdfDecimal17f;
    private MutableDecimal17f mdfZero;
    private MutableDecimal17f mdfOne;
    private MutableDecimal17f mdfInputA;
    private MutableDecimal17f mdfInputB;

    @Setup(Level.Trial)
    public void setup() {
        // 1. Setup basic constants
        mdfZero = MutableDecimal17f.zero();
        mdfOne = MutableDecimal17f.one();

        // 2. Setup inputs for construction tests
        long longValue = 1234567890123L;
        double doubleValue = 3.141592653589793;
        String stringValue = "123.4567890123456789";
        BigDecimal bigDecimalValue = new BigDecimal("987.6543210987654321");
        Decimal17f decimal17fValue = Decimal17f.valueOf(123.456789012345678);

        // 3. Setup construction instances
        mdfLong = new MutableDecimal17f(longValue);
        mdfDouble = new MutableDecimal17f(doubleValue);
        mdfString = new MutableDecimal17f(stringValue);
        mdfBigDecimal = new MutableDecimal17f(bigDecimalValue);
        mdfDecimal17f = new MutableDecimal17f(decimal17fValue);

        // 4. Setup inputs for arithmetic tests
        mdfInputA = new MutableDecimal17f(100L);
        mdfInputB = new MutableDecimal17f(50L);
    }

    @Benchmark
    public void testConstructionFromLong(Blackhole bh) {
        MutableDecimal17f result = new MutableDecimal17f(1234567890123L);
        bh.consume(result);
    }

    @Benchmark
    public void testConstructionFromDouble(Blackhole bh) {
        MutableDecimal17f result = new MutableDecimal17f(3.141592653589793);
        bh.consume(result);
    }

    @Benchmark
    public void testConstructionFromString(Blackhole bh) {
        MutableDecimal17f result = new MutableDecimal17f("123.4567890123456789");
        bh.consume(result);
    }

    @Benchmark
    public void testConstructionFromBigDecimal(Blackhole bh) {
        MutableDecimal17f result = new MutableDecimal17f(new BigDecimal("987.6543210987654321"));
        bh.consume(result);
    }

    @Benchmark
    public void testConstructionFromDecimal17f(Blackhole bh) {
        MutableDecimal17f result = new MutableDecimal17f(Decimal17f.valueOf(123.456789012345678));
        bh.consume(result);
    }

    @Benchmark
    public void testClone(Blackhole bh) {
        MutableDecimal17f cloned = mdfInputA.clone();
        bh.consume(cloned);
    }

    @Benchmark
    public void testGetScale(Blackhole bh) {
        int scale = mdfInputA.getScale();
        bh.consume(scale);
    }

    @Benchmark
    public void testGetScaleMetrics(Blackhole bh) {
        Scale17f metrics = mdfInputA.getScaleMetrics();
        bh.consume(metrics);
    }

    @Benchmark
    public void testGetFactory(Blackhole bh) {
        Factory17f factory = mdfInputA.getFactory();
        bh.consume(factory);
    }

    @Benchmark
    public void testUnscaledValue(Blackhole bh) {
        long unscaled = mdfInputA.unscaledValue();
        bh.consume(unscaled);
    }

    @Benchmark
    public void testNegate(Blackhole bh) {
        MutableDecimal17f result = mdfInputA.negate();
        bh.consume(result);
    }

    @Benchmark
    public void testAdd(Blackhole bh) {
        MutableDecimal17f result = mdfInputA.add(mdfInputB);
        bh.consume(result);
    }

    @Benchmark
    public void testMultiply(Blackhole bh) {
        // Standard multiplication of two MutableDecimal17f instances
        MutableDecimal17f result = mdfInputA.multiply(mdfInputB);
        bh.consume(result);
    }

    @Benchmark
    public void testToImmutableDecimal(Blackhole bh) {
        Decimal17f result = mdfInputA.toImmutableDecimal();
        bh.consume(result);
    }
}
