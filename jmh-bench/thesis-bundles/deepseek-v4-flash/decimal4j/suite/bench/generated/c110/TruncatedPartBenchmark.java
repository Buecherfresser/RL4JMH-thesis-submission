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

    private int digit0;
    private int digit1;
    private int digit2;
    private int digit3;
    private int digit4;
    private int digit5;
    private int digit6;
    private int digit7;
    private int digit8;
    private int digit9;

    private boolean zeroAfterTrue;
    private boolean zeroAfterFalse;

    @Setup(Level.Trial)
    public void setUp() {
        zero = TruncatedPart.ZERO;
        lessThanHalf = TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO;
        equalToHalf = TruncatedPart.EQUAL_TO_HALF;
        greaterThanHalf = TruncatedPart.GREATER_THAN_HALF;

        digit0 = 0;
        digit1 = 1;
        digit2 = 2;
        digit3 = 3;
        digit4 = 4;
        digit5 = 5;
        digit6 = 6;
        digit7 = 7;
        digit8 = 8;
        digit9 = 9;

        zeroAfterTrue = true;
        zeroAfterFalse = false;
    }

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

    @Benchmark
    public TruncatedPart valueOfDigit0ZeroAfterTrue() {
        return TruncatedPart.valueOf(digit0, zeroAfterTrue);
    }

    @Benchmark
    public TruncatedPart valueOfDigit0ZeroAfterFalse() {
        return TruncatedPart.valueOf(digit0, zeroAfterFalse);
    }

    @Benchmark
    public TruncatedPart valueOfDigit1ZeroAfterTrue() {
        return TruncatedPart.valueOf(digit1, zeroAfterTrue);
    }

    @Benchmark
    public TruncatedPart valueOfDigit1ZeroAfterFalse() {
        return TruncatedPart.valueOf(digit1, zeroAfterFalse);
    }

    @Benchmark
    public TruncatedPart valueOfDigit2ZeroAfterTrue() {
        return TruncatedPart.valueOf(digit2, zeroAfterTrue);
    }

    @Benchmark
    public TruncatedPart valueOfDigit2ZeroAfterFalse() {
        return TruncatedPart.valueOf(digit2, zeroAfterFalse);
    }

    @Benchmark
    public TruncatedPart valueOfDigit3ZeroAfterTrue() {
        return TruncatedPart.valueOf(digit3, zeroAfterTrue);
    }

    @Benchmark
    public TruncatedPart valueOfDigit3ZeroAfterFalse() {
        return TruncatedPart.valueOf(digit3, zeroAfterFalse);
    }

    @Benchmark
    public TruncatedPart valueOfDigit4ZeroAfterTrue() {
        return TruncatedPart.valueOf(digit4, zeroAfterTrue);
    }

    @Benchmark
    public TruncatedPart valueOfDigit4ZeroAfterFalse() {
        return TruncatedPart.valueOf(digit4, zeroAfterFalse);
    }

    @Benchmark
    public TruncatedPart valueOfDigit5ZeroAfterTrue() {
        return TruncatedPart.valueOf(digit5, zeroAfterTrue);
    }

    @Benchmark
    public TruncatedPart valueOfDigit5ZeroAfterFalse() {
        return TruncatedPart.valueOf(digit5, zeroAfterFalse);
    }

    @Benchmark
    public TruncatedPart valueOfDigit6ZeroAfterTrue() {
        return TruncatedPart.valueOf(digit6, zeroAfterTrue);
    }

    @Benchmark
    public TruncatedPart valueOfDigit6ZeroAfterFalse() {
        return TruncatedPart.valueOf(digit6, zeroAfterFalse);
    }

    @Benchmark
    public TruncatedPart valueOfDigit7ZeroAfterTrue() {
        return TruncatedPart.valueOf(digit7, zeroAfterTrue);
    }

    @Benchmark
    public TruncatedPart valueOfDigit7ZeroAfterFalse() {
        return TruncatedPart.valueOf(digit7, zeroAfterFalse);
    }

    @Benchmark
    public TruncatedPart valueOfDigit8ZeroAfterTrue() {
        return TruncatedPart.valueOf(digit8, zeroAfterTrue);
    }

    @Benchmark
    public TruncatedPart valueOfDigit8ZeroAfterFalse() {
        return TruncatedPart.valueOf(digit8, zeroAfterFalse);
    }

    @Benchmark
    public TruncatedPart valueOfDigit9ZeroAfterTrue() {
        return TruncatedPart.valueOf(digit9, zeroAfterTrue);
    }

    @Benchmark
    public TruncatedPart valueOfDigit9ZeroAfterFalse() {
        return TruncatedPart.valueOf(digit9, zeroAfterFalse);
    }
}
