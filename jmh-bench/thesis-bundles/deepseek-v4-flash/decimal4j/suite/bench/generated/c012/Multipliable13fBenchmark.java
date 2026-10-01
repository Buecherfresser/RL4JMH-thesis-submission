package bench.generated.c012;

import org.openjdk.jmh.annotations.*;
import java.util.concurrent.TimeUnit;
import org.decimal4j.exact.Multipliable13f;
import org.decimal4j.immutable.Decimal0f;
import org.decimal4j.immutable.Decimal1f;
import org.decimal4j.immutable.Decimal2f;
import org.decimal4j.immutable.Decimal3f;
import org.decimal4j.immutable.Decimal4f;
import org.decimal4j.immutable.Decimal5f;
import org.decimal4j.immutable.Decimal13f;
import org.decimal4j.immutable.Decimal14f;
import org.decimal4j.immutable.Decimal15f;
import org.decimal4j.immutable.Decimal16f;
import org.decimal4j.immutable.Decimal17f;
import org.decimal4j.immutable.Decimal18f;
import org.decimal4j.mutable.MutableDecimal0f;
import org.decimal4j.mutable.MutableDecimal1f;
import org.decimal4j.mutable.MutableDecimal2f;
import org.decimal4j.mutable.MutableDecimal3f;
import org.decimal4j.mutable.MutableDecimal4f;
import org.decimal4j.mutable.MutableDecimal5f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Multipliable13fBenchmark {

    @State(Scope.Benchmark)
    public static class BenchState {
        Multipliable13f multipliable;
        Decimal0f factor0;
        MutableDecimal0f mutableFactor0;
        Decimal1f factor1;
        MutableDecimal1f mutableFactor1;
        Decimal2f factor2;
        MutableDecimal2f mutableFactor2;
        Decimal3f factor3;
        MutableDecimal3f mutableFactor3;
        Decimal4f factor4;
        MutableDecimal4f mutableFactor4;
        Decimal5f factor5;
        MutableDecimal5f mutableFactor5;

        @Setup(Level.Trial)
        public void setup() {
            Decimal13f value = Decimal13f.valueOf("123.4567890123456");
            multipliable = new Multipliable13f(value);
            factor0 = Decimal0f.valueOf(2);
            mutableFactor0 = new MutableDecimal0f(2);
            factor1 = Decimal1f.valueOf(2.0);
            mutableFactor1 = new MutableDecimal1f(2.0);
            factor2 = Decimal2f.valueOf(2.0);
            mutableFactor2 = new MutableDecimal2f(2.0);
            factor3 = Decimal3f.valueOf(2.0);
            mutableFactor3 = new MutableDecimal3f(2.0);
            factor4 = Decimal4f.valueOf(2.0);
            mutableFactor4 = new MutableDecimal4f(2.0);
            factor5 = Decimal5f.valueOf(2.0);
            mutableFactor5 = new MutableDecimal5f(2.0);
        }
    }

    @Benchmark
    public Decimal13f byDecimal0f(BenchState state) {
        return state.multipliable.by(state.factor0);
    }

    @Benchmark
    public Decimal13f byMutableDecimal0f(BenchState state) {
        return state.multipliable.by(state.mutableFactor0);
    }

    @Benchmark
    public Decimal14f byDecimal1f(BenchState state) {
        return state.multipliable.by(state.factor1);
    }

    @Benchmark
    public Decimal14f byMutableDecimal1f(BenchState state) {
        return state.multipliable.by(state.mutableFactor1);
    }

    @Benchmark
    public Decimal15f byDecimal2f(BenchState state) {
        return state.multipliable.by(state.factor2);
    }

    @Benchmark
    public Decimal15f byMutableDecimal2f(BenchState state) {
        return state.multipliable.by(state.mutableFactor2);
    }

    @Benchmark
    public Decimal16f byDecimal3f(BenchState state) {
        return state.multipliable.by(state.factor3);
    }

    @Benchmark
    public Decimal16f byMutableDecimal3f(BenchState state) {
        return state.multipliable.by(state.mutableFactor3);
    }

    @Benchmark
    public Decimal17f byDecimal4f(BenchState state) {
        return state.multipliable.by(state.factor4);
    }

    @Benchmark
    public Decimal17f byMutableDecimal4f(BenchState state) {
        return state.multipliable.by(state.mutableFactor4);
    }

    @Benchmark
    public Decimal18f byDecimal5f(BenchState state) {
        return state.multipliable.by(state.factor5);
    }

    @Benchmark
    public Decimal18f byMutableDecimal5f(BenchState state) {
        return state.multipliable.by(state.mutableFactor5);
    }
}
