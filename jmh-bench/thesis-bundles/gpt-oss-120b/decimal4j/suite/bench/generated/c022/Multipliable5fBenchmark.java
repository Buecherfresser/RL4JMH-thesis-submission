package bench.generated.c022;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.exact.Multipliable5f;
import org.decimal4j.immutable.Decimal0f;
import org.decimal4j.immutable.Decimal1f;
import org.decimal4j.immutable.Decimal2f;
import org.decimal4j.immutable.Decimal3f;
import org.decimal4j.immutable.Decimal4f;
import org.decimal4j.immutable.Decimal5f;
import org.decimal4j.immutable.Decimal6f;
import org.decimal4j.immutable.Decimal7f;
import org.decimal4j.immutable.Decimal8f;
import org.decimal4j.immutable.Decimal9f;
import org.decimal4j.immutable.Decimal10f;
import org.decimal4j.immutable.Decimal11f;
import org.decimal4j.immutable.Decimal12f;
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
import org.decimal4j.mutable.MutableDecimal6f;
import org.decimal4j.mutable.MutableDecimal7f;
import org.decimal4j.mutable.MutableDecimal8f;
import org.decimal4j.mutable.MutableDecimal9f;
import org.decimal4j.mutable.MutableDecimal10f;
import org.decimal4j.mutable.MutableDecimal11f;
import org.decimal4j.mutable.MutableDecimal12f;
import org.decimal4j.mutable.MutableDecimal13f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Multipliable5fBenchmark {

    @State(Scope.Benchmark)
    public static class BenchmarkState {
        Decimal5f baseValue;
        Multipliable5f multipliable;

        Decimal5f factorDecimal5f;
        Decimal0f factorDecimal0f;
        MutableDecimal0f factorMutableDecimal0f;
        Decimal1f factorDecimal1f;
        MutableDecimal1f factorMutableDecimal1f;
        Decimal2f factorDecimal2f;
        MutableDecimal2f factorMutableDecimal2f;
        Decimal3f factorDecimal3f;
        MutableDecimal3f factorMutableDecimal3f;
        Decimal4f factorDecimal4f;
        MutableDecimal4f factorMutableDecimal4f;
        Decimal6f factorDecimal6f;
        MutableDecimal6f factorMutableDecimal6f;
        Decimal7f factorDecimal7f;
        MutableDecimal7f factorMutableDecimal7f;
        Decimal8f factorDecimal8f;
        MutableDecimal8f factorMutableDecimal8f;
        Decimal9f factorDecimal9f;
        MutableDecimal9f factorMutableDecimal9f;
        Decimal10f factorDecimal10f;
        MutableDecimal10f factorMutableDecimal10f;
        Decimal11f factorDecimal11f;
        MutableDecimal11f factorMutableDecimal11f;
        Decimal12f factorDecimal12f;
        MutableDecimal12f factorMutableDecimal12f;
        Decimal13f factorDecimal13f;
        MutableDecimal13f factorMutableDecimal13f;

        @Setup(Level.Trial)
        public void setUp() {
            baseValue = Decimal5f.valueOf(12345L);
            multipliable = new Multipliable5f(baseValue);

            factorDecimal5f = Decimal5f.valueOf(2L);
            factorDecimal0f = Decimal0f.valueOf(2L);
            factorMutableDecimal0f = new MutableDecimal0f(2L);
            factorDecimal1f = Decimal1f.valueOf(2L);
            factorMutableDecimal1f = new MutableDecimal1f(2L);
            factorDecimal2f = Decimal2f.valueOf(2L);
            factorMutableDecimal2f = new MutableDecimal2f(2L);
            factorDecimal3f = Decimal3f.valueOf(2L);
            factorMutableDecimal3f = new MutableDecimal3f(2L);
            factorDecimal4f = Decimal4f.valueOf(2L);
            factorMutableDecimal4f = new MutableDecimal4f(2L);
            factorDecimal6f = Decimal6f.valueOf(2L);
            factorMutableDecimal6f = new MutableDecimal6f(2L);
            factorDecimal7f = Decimal7f.valueOf(2L);
            factorMutableDecimal7f = new MutableDecimal7f(2L);
            factorDecimal8f = Decimal8f.valueOf(2L);
            factorMutableDecimal8f = new MutableDecimal8f(2L);
            factorDecimal9f = Decimal9f.valueOf(2L);
            factorMutableDecimal9f = new MutableDecimal9f(2L);
            factorDecimal10f = Decimal10f.valueOf(2L);
            factorMutableDecimal10f = new MutableDecimal10f(2L);
            factorDecimal11f = Decimal11f.valueOf(2L);
            factorMutableDecimal11f = new MutableDecimal11f(2L);
            factorDecimal12f = Decimal12f.valueOf(2L);
            factorMutableDecimal12f = new MutableDecimal12f(2L);
            factorDecimal13f = Decimal13f.valueOf(2L);
            factorMutableDecimal13f = new MutableDecimal13f(2L);
        }
    }

    @Benchmark
    public Decimal10f square(BenchmarkState s) {
        return s.multipliable.square();
    }

    @Benchmark
    public Decimal10f byDecimal5f(BenchmarkState s) {
        return s.multipliable.by(s.factorDecimal5f);
    }

    @Benchmark
    public Decimal5f byDecimal0f(BenchmarkState s) {
        return s.multipliable.by(s.factorDecimal0f);
    }

    @Benchmark
    public Decimal5f byMutableDecimal0f(BenchmarkState s) {
        return s.multipliable.by(s.factorMutableDecimal0f);
    }

    @Benchmark
    public Decimal6f byDecimal1f(BenchmarkState s) {
        return s.multipliable.by(s.factorDecimal1f);
    }

    @Benchmark
    public Decimal6f byMutableDecimal1f(BenchmarkState s) {
        return s.multipliable.by(s.factorMutableDecimal1f);
    }

    @Benchmark
    public Decimal7f byDecimal2f(BenchmarkState s) {
        return s.multipliable.by(s.factorDecimal2f);
    }

    @Benchmark
    public Decimal7f byMutableDecimal2f(BenchmarkState s) {
        return s.multipliable.by(s.factorMutableDecimal2f);
    }

    @Benchmark
    public Decimal8f byDecimal3f(BenchmarkState s) {
        return s.multipliable.by(s.factorDecimal3f);
    }

    @Benchmark
    public Decimal8f byMutableDecimal3f(BenchmarkState s) {
        return s.multipliable.by(s.factorMutableDecimal3f);
    }

    @Benchmark
    public Decimal9f byDecimal4f(BenchmarkState s) {
        return s.multipliable.by(s.factorDecimal4f);
    }

    @Benchmark
    public Decimal9f byMutableDecimal4f(BenchmarkState s) {
        return s.multipliable.by(s.factorMutableDecimal4f);
    }

    @Benchmark
    public Decimal11f byDecimal6f(BenchmarkState s) {
        return s.multipliable.by(s.factorDecimal6f);
    }

    @Benchmark
    public Decimal11f byMutableDecimal6f(BenchmarkState s) {
        return s.multipliable.by(s.factorMutableDecimal6f);
    }

    @Benchmark
    public Decimal12f byDecimal7f(BenchmarkState s) {
        return s.multipliable.by(s.factorDecimal7f);
    }

    @Benchmark
    public Decimal12f byMutableDecimal7f(BenchmarkState s) {
        return s.multipliable.by(s.factorMutableDecimal7f);
    }

    @Benchmark
    public Decimal13f byDecimal8f(BenchmarkState s) {
        return s.multipliable.by(s.factorDecimal8f);
    }

    @Benchmark
    public Decimal13f byMutableDecimal8f(BenchmarkState s) {
        return s.multipliable.by(s.factorMutableDecimal8f);
    }

    @Benchmark
    public Decimal14f byDecimal9f(BenchmarkState s) {
        return s.multipliable.by(s.factorDecimal9f);
    }

    @Benchmark
    public Decimal14f byMutableDecimal9f(BenchmarkState s) {
        return s.multipliable.by(s.factorMutableDecimal9f);
    }

    @Benchmark
    public Decimal15f byDecimal10f(BenchmarkState s) {
        return s.multipliable.by(s.factorDecimal10f);
    }

    @Benchmark
    public Decimal15f byMutableDecimal10f(BenchmarkState s) {
        return s.multipliable.by(s.factorMutableDecimal10f);
    }

    @Benchmark
    public Decimal16f byDecimal11f(BenchmarkState s) {
        return s.multipliable.by(s.factorDecimal11f);
    }

    @Benchmark
    public Decimal16f byMutableDecimal11f(BenchmarkState s) {
        return s.multipliable.by(s.factorMutableDecimal11f);
    }

    @Benchmark
    public Decimal17f byDecimal12f(BenchmarkState s) {
        return s.multipliable.by(s.factorDecimal12f);
    }

    @Benchmark
    public Decimal17f byMutableDecimal12f(BenchmarkState s) {
        return s.multipliable.by(s.factorMutableDecimal12f);
    }

    @Benchmark
    public Decimal18f byDecimal13f(BenchmarkState s) {
        return s.multipliable.by(s.factorDecimal13f);
    }

    @Benchmark
    public Decimal18f byMutableDecimal13f(BenchmarkState s) {
        return s.multipliable.by(s.factorMutableDecimal13f);
    }
}
