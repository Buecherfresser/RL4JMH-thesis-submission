package bench.generated.c110;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.truncate.TruncatedPart;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class TruncatedPartBenchmark {

    // State field to hold a reference to the enum instance
    private TruncatedPart currentPart;

    @Setup
    public void setup() {
        // Initialize a representative state.
        this.currentPart = TruncatedPart.EQUAL_TO_HALF;
    }

    @Benchmark
    public void testIsGreaterThanZero(Blackhole bh) {
        // Test the method on the current state
        bh.consume(currentPart.isGreaterThanZero());
    }

    @Benchmark
    public void testIsEqualToHalf(Blackhole bh) {
        // Test the method on the current state
        bh.consume(currentPart.isEqualToHalf());
    }

    @Benchmark
    public void testIsGreaterEqualHalf(Blackhole bh) {
        // Test the method on the current state
        bh.consume(currentPart.isGreaterEqualHalf());
    }

    @Benchmark
    public void testIsGreaterThanHalf(Blackhole bh) {
        // Test the method on the current state
        bh.consume(currentPart.isGreaterThanHalf());
    }

    @Benchmark
    public void testValueOf(Blackhole bh) {
        // Test the static factory method
        bh.consume(TruncatedPart.valueOf(3, false));
    }

    @Benchmark
    public void testValueOfWithZeroAfter(Blackhole bh) {
        // Test the static factory method with zeroAfterFirstTruncatedDigit = true
        bh.consume(TruncatedPart.valueOf(5, true));
    }
}
