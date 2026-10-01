package bench.generated.c016;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

import org.decimal4j.api.Decimal;
import org.decimal4j.immutable.Decimal0f;
import org.decimal4j.immutable.Decimal1f;
import org.decimal4j.immutable.Decimal17f;
import org.decimal4j.immutable.Decimal18f;
import org.decimal4j.mutable.MutableDecimal0f;
import org.decimal4j.mutable.MutableDecimal1f;
import org.decimal4j.scale.Scale17f;
import org.decimal4j.exact.Multipliable17f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Multipliable17fBenchmark {

    private Multipliable17f multipliable17f;

    // Inputs for by(Decimal0f factor)
    private Decimal0f decimal0fFactor;
    // Inputs for by(MutableDecimal0f factor)
    private MutableDecimal0f mutableDecimal0fFactor;
    // Inputs for by(Decimal1f factor)
    private Decimal1f decimal1fFactor;
    // Inputs for by(MutableDecimal1f factor)
    private MutableDecimal1f mutableDecimal1fFactor;

    @Setup
    public void setup() {
        // 1. Setup the base value for Multipliable17f.
        // We use Decimal17f as the base value, which is Decimal<Scale17f>.
        Decimal<Scale17f> baseValue = Decimal17f.valueOf(1000000000000000000L); // A large number
        multipliable17f = new Multipliable17f(baseValue);

        // 2. Setup factors for multiplication tests.
        // Decimal0f factor
        decimal0fFactor = Decimal0f.valueOf(5L);

        // MutableDecimal0f factor
        MutableDecimal0f mutableDecimal0fFactor = MutableDecimal0f.zero().set(10L);

        // Decimal1f factor
        decimal1fFactor = Decimal1f.valueOf(2.5);

        // MutableDecimal1f factor
        MutableDecimal1f mutableDecimal1fFactor = MutableDecimal1f.zero().set(1.5);
    }

    @Benchmark
    public void by_Decimal0f(Blackhole bh) {
        // Calls multipliable17f.by(Decimal0f factor)
        Decimal17f result = multipliable17f.by(decimal0fFactor);
        bh.consume(result);
    }

    @Benchmark
    public void by_MutableDecimal0f(Blackhole bh) {
        // Calls multipliable17f.by(MutableDecimal0f factor)
        Decimal17f result = multipliable17f.by(mutableDecimal0fFactor);
        bh.consume(result);
    }

    @Benchmark
    public void by_Decimal1f(Blackhole bh) {
        // Calls multipliable17f.by(Decimal1f factor)
        Decimal18f result = multipliable17f.by(decimal1fFactor);
        bh.consume(result);
    }

    @Benchmark
    public void by_MutableDecimal1f(Blackhole bh) {
        // Calls multipliable17f.by(MutableDecimal1f factor)
        Decimal18f result = multipliable17f.by(mutableDecimal1fFactor);
        bh.consume(result);
    }

    @Benchmark
    public void getValue_Read(Blackhole bh) {
        // Tests the getValue() method
        Decimal<Scale17f> value = multipliable17f.getValue();
        bh.consume(value);
    }

    @Benchmark
    public void hashCode_Read(Blackhole bh) {
        // Tests the hashCode() method
        int hash = multipliable17f.hashCode();
        bh.consume(hash);
    }
}
