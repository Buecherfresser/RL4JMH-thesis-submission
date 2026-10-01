package bench.generated.c077;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.mutable.MutableDecimal18f;
import org.decimal4j.immutable.Decimal18f;
import org.decimal4j.factory.Factory18f;
import org.decimal4j.scale.Scale18f;
import org.decimal4j.api.Decimal;
import org.decimal4j.exact.Multipliable18f;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class MutableDecimal18fBenchmark {

    private MutableDecimal18f baseDecimal;
    private long testLong;
    private double testDouble;
    private BigDecimal testBigDecimal;
    private Decimal18f testDecimal18f;
    private String testString;
    private Decimal<?> testGenericDecimal;

    @Setup(Level.Trial)
    public void setup() {
        // Initialize base instance for operation benchmarks
        baseDecimal = MutableDecimal18f.one();

        // Initialize inputs for construction benchmarks
        testLong = 123456789012345L;
        testDouble = 123.4567890123456789;
        testBigDecimal = new BigDecimal("987654321098765432.123456789");
        testDecimal18f = Decimal18f.valueOf(123.4567890123456789);
        testString = "123456789012345.6789";
        
        // Create a generic decimal instance for testing conversion from Decimal<?>
        testGenericDecimal = Decimal18f.FACTORY.valueOf(testLong);
    }

    // --- Construction Benchmarks ---

    @Benchmark
    public void benchmark_construction_fromLong(Blackhole bh) {
        MutableDecimal18f result = new MutableDecimal18f(testLong);
        bh.consume(result);
    }

    @Benchmark
    public void benchmark_construction_fromDouble(Blackhole bh) {
        MutableDecimal18f result = new MutableDecimal18f(testDouble);
        bh.consume(result);
    }

    @Benchmark
    public void benchmark_construction_fromBigDecimal(Blackhole bh) {
        MutableDecimal18f result = new MutableDecimal18f(testBigDecimal);
        bh.consume(result);
    }

    @Benchmark
    public void benchmark_construction_fromDecimal18f(Blackhole bh) {
        MutableDecimal18f result = new MutableDecimal18f(testDecimal18f);
        bh.consume(result);
    }

    @Benchmark
    public void benchmark_construction_fromString(Blackhole bh) {
        MutableDecimal18f result = new MutableDecimal18f(testString);
        bh.consume(result);
    }

    @Benchmark
    public void benchmark_construction_fromGenericDecimal(Blackhole bh) {
        MutableDecimal18f result = new MutableDecimal18f(testGenericDecimal);
        bh.consume(result);
    }

    // --- Operation Benchmarks ---

    @Benchmark
    public void benchmark_clone(Blackhole bh) {
        // Clone operation
        MutableDecimal18f cloned = baseDecimal.clone();
        bh.consume(cloned);
    }

    @Benchmark
    public void benchmark_toImmutableDecimal(Blackhole bh) {
        // Conversion to immutable type
        Decimal18f immutable = baseDecimal.toImmutableDecimal();
        bh.consume(immutable);
    }

    @Benchmark
    public void benchmark_multiplyExact(Blackhole bh) {
        // Start exact multiplication chain
        Multipliable18f multiplier = baseDecimal.multiplyExact();
        // Consume the multiplier object itself, representing the start of the operation
        bh.consume(multiplier);
    }
}
