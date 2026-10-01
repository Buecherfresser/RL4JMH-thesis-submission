package bench.generated.c009;

import org.decimal4j.api.Decimal;
import org.decimal4j.exact.Multipliable10f;
import org.decimal4j.immutable.Decimal0f;
import org.decimal4j.immutable.Decimal10f;
import org.decimal4j.immutable.Decimal15f;
import org.decimal4j.immutable.Decimal18f;
import org.decimal4j.immutable.Decimal5f;
import org.decimal4j.immutable.Decimal8f;
import org.decimal4j.mutable.MutableDecimal5f;
import org.decimal4j.scale.Scale10f;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Multipliable10fBenchmark {

    private Multipliable10f multipliable;
    private Multipliable10f otherMultipliable;
    private Decimal0f factor0;
    private Decimal5f factor5;
    private Decimal8f factor8;
    private MutableDecimal5f mutableFactor5;

    @Setup
    public void setup() {
        multipliable = new Multipliable10f(Decimal10f.valueOf(12345));
        otherMultipliable = new Multipliable10f(Decimal10f.valueOf(12345));
        factor0 = Decimal0f.valueOf(2);
        factor5 = Decimal5f.valueOf(3.5);
        factor8 = Decimal8f.valueOf(1.23456789);
        mutableFactor5 = new MutableDecimal5f("4.5");
    }

    @Benchmark
    public Decimal10f byDecimal0f() {
        return multipliable.by(factor0);
    }

    @Benchmark
    public Decimal15f byDecimal5f() {
        return multipliable.by(factor5);
    }

    @Benchmark
    public Decimal18f byDecimal8f() {
        return multipliable.by(factor8);
    }

    @Benchmark
    public Decimal15f byMutableDecimal5f() {
        return multipliable.by(mutableFactor5);
    }

    @Benchmark
    public Decimal<Scale10f> getValue() {
        return multipliable.getValue();
    }

    @Benchmark
    public String toStringBenchmark() {
        return multipliable.toString();
    }

    @Benchmark
    public boolean equalsBenchmark() {
        return multipliable.equals(otherMultipliable);
    }

    @Benchmark
    public int hashCodeBenchmark() {
        return multipliable.hashCode();
    }
}
