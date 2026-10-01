package bench.generated.c016;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.exact.Multipliable17f;
import org.decimal4j.api.Decimal;
import org.decimal4j.immutable.Decimal0f;
import org.decimal4j.immutable.Decimal1f;
import org.decimal4j.immutable.Decimal17f;
import org.decimal4j.immutable.Decimal18f;
import org.decimal4j.mutable.MutableDecimal0f;
import org.decimal4j.mutable.MutableDecimal1f;
import org.decimal4j.scale.Scale17f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Multipliable17fBenchmark {

    private Multipliable17f multipliable;
    private Decimal0f factor0f;
    private MutableDecimal0f factorM0f;
    private Decimal1f factor1f;
    private MutableDecimal1f factorM1f;
    private Multipliable17f equalMultipliable;

    @Setup(Level.Trial)
    public void setup() {
        // 1. Setup base value (Decimal<Scale17f>)
        // Using Decimal0f.ONE as a representative base value
        Decimal<Scale17f> baseValue = Decimal17f.valueOf(Decimal0f.ONE);
        
        // 2. Create the subject instance
        multipliable = new Multipliable17f(baseValue);

        // 3. Setup factors
        factor0f = Decimal0f.valueOf(12345L);
        factorM0f = new MutableDecimal0f(12345L);
        
        factor1f = Decimal1f.valueOf(98765L);
        factorM1f = new MutableDecimal1f(98765L);
        
        // 4. Setup an equal object for equals() testing
        equalMultipliable = new Multipliable17f(baseValue);
    }

    @Benchmark
    public void testByDecimal0f(Blackhole bh) {
        Decimal17f result = multipliable.by(factor0f);
        bh.consume(result);
    }

    @Benchmark
    public void testByMutableDecimal0f(Blackhole bh) {
        Decimal17f result = multipliable.by(factorM0f);
        bh.consume(result);
    }

    @Benchmark
    public void testByDecimal1f(Blackhole bh) {
        Decimal18f result = multipliable.by(factor1f);
        bh.consume(result);
    }

    @Benchmark
    public void testByMutableDecimal1f(Blackhole bh) {
        Decimal18f result = multipliable.by(factorM1f);
        bh.consume(result);
    }

    @Benchmark
    public void testGetValue(Blackhole bh) {
        Decimal<Scale17f> result = multipliable.getValue();
        bh.consume(result);
    }

    @Benchmark
    public void testHashCode(Blackhole bh) {
        int result = multipliable.hashCode();
        bh.consume(result);
    }

    @Benchmark
    public void testEqualsTrue(Blackhole bh) {
        boolean result = multipliable.equals(equalMultipliable);
        bh.consume(result);
    }

    @Benchmark
    public void testEqualsFalse(Blackhole bh) {
        // Test against a different object type
        boolean result = multipliable.equals("not a multipliable");
        bh.consume(result);
    }

    @Benchmark
    public void testEqualsNull(Blackhole bh) {
        boolean result = multipliable.equals(null);
        bh.consume(result);
    }

    @Benchmark
    public void testToString(Blackhole bh) {
        String result = multipliable.toString();
        bh.consume(result);
    }
}
