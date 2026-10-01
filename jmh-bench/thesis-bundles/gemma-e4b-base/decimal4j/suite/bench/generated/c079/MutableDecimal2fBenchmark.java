package bench.generated.c079;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.mutable.MutableDecimal2f;
import org.decimal4j.immutable.Decimal2f;
import org.decimal4j.exact.Multipliable2f;
import java.math.BigDecimal;
import java.math.BigInteger;
import org.decimal4j.api.Decimal;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class MutableDecimal2fBenchmark {

    private MutableDecimal2f mutableDecimal;
    private long inputLong;
    private double inputDouble;
    private String inputString;
    private BigDecimal inputBigDecimal;
    private Decimal2f inputDecimal2f;

    @Setup(Level.Trial)
    public void setup() {
        // Initialize base mutable decimal
        mutableDecimal = MutableDecimal2f.zero();

        // Initialize inputs
        inputLong = 1234567890123L;
        inputDouble = 3.1415926535;
        inputString = "123.45";
        inputBigDecimal = new BigDecimal("987.65");
        inputDecimal2f = Decimal2f.valueOf(10.50);
    }

    // --- Initialization Benchmarks ---

    @Benchmark
    public MutableDecimal2f initFromLong(Blackhole bh) {
        // Create a fresh instance for measurement
        MutableDecimal2f result = new MutableDecimal2f(inputLong);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public MutableDecimal2f initFromDouble(Blackhole bh) {
        // Create a fresh instance for measurement
        MutableDecimal2f result = new MutableDecimal2f(inputDouble);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public MutableDecimal2f initFromString(Blackhole bh) {
        // Create a fresh instance for measurement
        MutableDecimal2f result = new MutableDecimal2f(inputString);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public MutableDecimal2f initFromBigDecimal(Blackhole bh) {
        // Create a fresh instance for measurement
        MutableDecimal2f result = new MutableDecimal2f(inputBigDecimal);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public MutableDecimal2f initFromDecimal2f(Blackhole bh) {
        // Create a fresh instance for measurement
        MutableDecimal2f result = new MutableDecimal2f(inputDecimal2f);
        bh.consume(result);
        return result;
    }

    // --- Arithmetic Benchmarks ---

    @Benchmark
    public MutableDecimal2f add(Blackhole bh) {
        // Use a fresh copy of the base instance to ensure mutability doesn't affect timing
        MutableDecimal2f a = mutableDecimal.clone();
        MutableDecimal2f b = MutableDecimal2f.two();
        
        // Perform addition (mutates 'a')
        a.add(b);
        bh.consume(a);
        return a;
    }

    @Benchmark
    public MutableDecimal2f multiply(Blackhole bh) {
        // Use a fresh copy
        MutableDecimal2f a = mutableDecimal.clone();
        MutableDecimal2f b = MutableDecimal2f.three();
        
        // Perform multiplication (mutates 'a')
        a.multiply(b);
        bh.consume(a);
        return a;
    }

    @Benchmark
    public MutableDecimal2f negate(Blackhole bh) {
        // Use a fresh copy
        MutableDecimal2f a = mutableDecimal.clone();
        
        // Perform negation (mutates 'a')
        a.negate();
        bh.consume(a);
        return a;
    }

    @Benchmark
    public MutableDecimal2f square(Blackhole bh) {
        // Use a fresh copy
        MutableDecimal2f a = mutableDecimal.clone();
        
        // Perform squaring (mutates 'a')
        a.square();
        bh.consume(a);
        return a;
    }

    // --- Utility and Conversion Benchmarks ---

    @Benchmark
    public MutableDecimal2f cloneOperation(Blackhole bh) {
        // Clone the base instance
        MutableDecimal2f cloned = mutableDecimal.clone();
        bh.consume(cloned);
        return cloned;
    }

    @Benchmark
    public Decimal2f toImmutableDecimal(Blackhole bh) {
        // Convert the base instance to immutable
        Decimal2f immutable = mutableDecimal.toImmutableDecimal();
        bh.consume(immutable);
        return immutable;
    }

    @Benchmark
    public MutableDecimal2f toMutableDecimal(Blackhole bh) {
        // Convert the base instance to mutable (should be identity)
        MutableDecimal2f mutable = mutableDecimal.toMutableDecimal();
        bh.consume(mutable);
        return mutable;
    }

    @Benchmark
    public Multipliable2f multiplyExact(Blackhole bh) {
        // Get the multipliable factor
        Multipliable2f multiplier = mutableDecimal.multiplyExact();
        bh.consume(multiplier);
        return multiplier;
    }
}
