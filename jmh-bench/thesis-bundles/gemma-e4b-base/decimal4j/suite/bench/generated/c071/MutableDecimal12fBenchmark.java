package bench.generated.c071;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.mutable.MutableDecimal12f;
import org.decimal4j.immutable.Decimal12f;
import org.decimal4j.exact.Multipliable12f;
import org.decimal4j.scale.Scale12f;
import java.math.BigDecimal;
import java.math.BigInteger;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class MutableDecimal12fBenchmark {

    private MutableDecimal12f subject;

    @Setup
    public void setup() {
        // Initialize a representative instance
        subject = MutableDecimal12f.one();
    }

    @Benchmark
    public void cloneBenchmark(Blackhole bh) {
        MutableDecimal12f cloned = subject.clone();
        bh.consume(cloned);
    }

    @Benchmark
    public void toImmutableDecimalBenchmark(Blackhole bh) {
        Decimal12f immutable = subject.toImmutableDecimal();
        bh.consume(immutable);
    }

    @Benchmark
    public void toMutableDecimalBenchmark(Blackhole bh) {
        MutableDecimal12f selfRef = subject.toMutableDecimal();
        bh.consume(selfRef);
    }

    @Benchmark
    public void multiplyExactInitiationBenchmark(Blackhole bh) {
        Multipliable12f multiplier = subject.multiplyExact();
        bh.consume(multiplier);
    }
}
