package bench.generated.c084;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.BigInteger;

import org.decimal4j.mutable.MutableDecimal7f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class MutableDecimal7fBenchmark {

    // State fields for read-only/reusable inputs
    private MutableDecimal7f zero;
    private MutableDecimal7f one;
    private MutableDecimal7f ten;
    private MutableDecimal7f hundredth;
    private MutableDecimal7f millionth;
    private MutableDecimal7f bigIntValue;

    @Setup
    public void setup() {
        // Initialize reusable objects
        this.zero = MutableDecimal7f.zero();
        this.one = MutableDecimal7f.one();
        this.ten = MutableDecimal7f.ten();
        this.hundredth = MutableDecimal7f.hundredth();
        this.millionth = MutableDecimal7f.millionth();
        
        // Create a large BigInteger for testing
        try {
            // Using a large number to test instantiation limits/performance
            this.bigIntValue = new MutableDecimal7f(new BigInteger("12345678901234567890123456789012345678901234567890"));
        } catch (Exception e) {
            // Ignore setup failure for benchmark purposes if it's just a large number
        }
    }

    @Benchmark
    public void benchmarkZero(Blackhole bh) {
        // Test a simple instance creation/retrieval
        bh.consume(MutableDecimal7f.zero());
    }

    @Benchmark
    public void benchmarkOne(Blackhole bh) {
        // Test a simple instance creation/retrieval
        bh.consume(MutableDecimal7f.one());
    }

    @Benchmark
    public void benchmarkTen(Blackhole bh) {
        // Test a simple instance creation/retrieval
        bh.consume(MutableDecimal7f.ten());
    }

    @Benchmark
    public void benchmarkHundredth(Blackhole bh) {
        // Test an instance created via static method
        bh.consume(MutableDecimal7f.hundredth());
    }

    @Benchmark
    public void benchmarkMillionth(Blackhole bh) {
        // Test an instance created via static method
        bh.consume(MutableDecimal7f.millionth());
    }

    @Benchmark
    public void benchmarkBigIntegerConversion(Blackhole bh) {
        // Test conversion from BigInteger using the constructor, as static valueOf was not found.
        try {
            MutableDecimal7f result = new MutableDecimal7f(new BigInteger("12345678901234567890123456789012345678901234567890"));
            bh.consume(result);
        } catch (IllegalArgumentException e) {
            // Ignore if the BigInteger is too large for MutableDecimal7f
        }
    }

    @Benchmark
    public void benchmarkClone(Blackhole bh) {
        // Test the clone method
        MutableDecimal7f original = MutableDecimal7f.one();
        bh.consume(original.clone());
    }

    @Benchmark
    public void benchmarkArithmetic(Blackhole bh) {
        // Test a simple arithmetic operation (e.g., addition)
        // Local variables are fine as they are not static final literals
        MutableDecimal7f a = MutableDecimal7f.one();
        MutableDecimal7f b = MutableDecimal7f.two();
        
        // The result is consumed to prevent dead code elimination
        bh.consume(a.add(b));
    }

    @Benchmark
    public void benchmarkToImmutable(Blackhole bh) {
        // Test conversion to immutable type
        MutableDecimal7f mutableVal = MutableDecimal7f.five();
        bh.consume(mutableVal.toImmutableDecimal());
    }

    @Benchmark
    public void benchmarkToMutable(Blackhole bh) {
        // Test conversion to mutable type (should return self)
        MutableDecimal7f mutableVal = MutableDecimal7f.zero();
        bh.consume(mutableVal.toMutableDecimal());
    }
    
}
