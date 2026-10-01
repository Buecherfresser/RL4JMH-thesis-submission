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

    private TruncatedPart zero;
    private TruncatedPart lessThanHalf;
    private TruncatedPart equalToHalf;
    private TruncatedPart greaterThanHalf;

    private int digitZero;
    private int digitFive;
    private int digitSix;
    private int digitFour;
    private boolean zeroAfterTrue;
    private boolean zeroAfterFalse;

    @Setup(Level.Trial)
    public void setup() {
        zero = TruncatedPart.ZERO;
        lessThanHalf = TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO;
        equalToHalf = TruncatedPart.EQUAL_TO_HALF;
        greaterThanHalf = TruncatedPart.GREATER_THAN_HALF;

        digitZero = 0;
        digitFive = 5;
        digitSix = 6;
        digitFour = 4;
        zeroAfterTrue = true;
        zeroAfterFalse = false;
    }

    // isGreaterThanZero benchmarks
    @Benchmark
    public boolean zeroIsGreaterThanZero() {
        return zero.isGreaterThanZero();
    }

    @Benchmark
    public boolean lessThanHalfIsGreaterThanZero() {
        return lessThanHalf.isGreaterThanZero();
    }

    @Benchmark
    public boolean equalToHalfIsGreaterThanZero() {
        return equalToHalf.isGreaterThanZero();
    }

    @Benchmark
    public boolean greaterThanHalfIsGreaterThanZero() {
        return greaterThanHalf.isGreaterThanZero();
    }

    // isEqualToHalf benchmarks
    @Benchmark
    public boolean zeroIsEqualToHalf() {
        return zero.isEqualToHalf();
    }

    @Benchmark
    public boolean lessThanHalfIsEqualToHalf() {
        return lessThanHalf.isEqualToHalf();
    }

    @Benchmark
    public boolean equalToHalfIsEqualToHalf() {
        return equalToHalf.isEqualToHalf();
    }

    @Benchmark
    public boolean greaterThanHalfIsEqualToHalf() {
        return greaterThanHalf.isEqualToHalf();
    }

    // isGreaterEqualHalf benchmarks
    @Benchmark
    public boolean zeroIsGreaterEqualHalf() {
        return zero.isGreaterEqualHalf();
    }

    @Benchmark
    public boolean lessThanHalfIsGreaterEqualHalf() {
        return lessThanHalf.isGreaterEqualHalf();
    }

    @Benchmark
    public boolean equalToHalfIsGreaterEqualHalf() {
        return equalToHalf.isGreaterEqualHalf();
    }

    @Benchmark
    public boolean greaterThanHalfIsGreaterEqualHalf() {
        return greaterThanHalf.isGreaterEqualHalf();
    }

    // isGreaterThanHalf benchmarks
    @Benchmark
    public boolean zeroIsGreaterThanHalf() {
        return zero.isGreaterThanHalf();
    }

    @Benchmark
    public boolean lessThanHalfIsGreaterThanHalf() {
        return lessThanHalf.isGreaterThanHalf();
    }

    @Benchmark
    public boolean equalToHalfIsGreaterThanHalf() {
        return equalToHalf.isGreaterThanHalf();
    }

    @Benchmark
    public boolean greaterThanHalfIsGreaterThanHalf() {
        return greaterThanHalf.isGreaterThanHalf();
    }

    // valueOf benchmarks
    @Benchmark
    public TruncatedPart valueOfZeroZeroAfter() {
        return TruncatedPart.valueOf(digitZero, zeroAfterTrue);
    }

    @Benchmark
    public TruncatedPart valueOfFiveZeroAfter() {
        return TruncatedPart.valueOf(digitFive, zeroAfterTrue);
    }

    @Benchmark
    public TruncatedPart valueOfSixZeroAfter() {
        return TruncatedPart.valueOf(digitSix, zeroAfterTrue);
    }

    @Benchmark
    public TruncatedPart valueOfFiveNonZeroAfter() {
        return TruncatedPart.valueOf(digitFive, zeroAfterFalse);
    }

    @Benchmark
    public TruncatedPart valueOfFourNonZeroAfter() {
        return TruncatedPart.valueOf(digitFour, zeroAfterFalse);
    }
}
