package bench.generated.c017;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.exact.Multipliable18f;
import org.decimal4j.immutable.Decimal18f;
import org.decimal4j.immutable.Decimal0f;
import org.decimal4j.mutable.MutableDecimal0f;
import org.decimal4j.scale.Scale18f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Multipliable18fBenchmark {

    private Multipliable18f multipliable;
    private Multipliable18f multipliableEqual;
    private Multipliable18f multipliableDifferent;

    private Decimal0f factorDecimal0f;
    private MutableDecimal0f factorMutableDecimal0f;

    @Setup
    public void setup() {
        // Base value: 1.0 (represented as Decimal18f)
        Decimal18f baseValue = Decimal18f.valueOf(1L);
        multipliable = new Multipliable18f(baseValue);

        // Setup for equality checks
        Decimal18f equalValue = Decimal18f.valueOf(1L);
        multipliableEqual = new Multipliable18f(equalValue);

        Decimal18f differentValue = Decimal18f.valueOf(2L);
        multipliableDifferent = new Multipliable18f(differentValue);

        // Setup factors
        factorDecimal0f = Decimal0f.FIVE;
        factorMutableDecimal0f = new MutableDecimal0f(0.5);
    }

    @Benchmark
    public void benchmarkGetValue(Blackhole bh) {
        bh.consume(multipliable.getValue());
    }

    @Benchmark
    public void benchmarkByDecimal0f(Blackhole bh) {
        // Operation: this * factor (Decimal0f)
        bh.consume(multipliable.by(factorDecimal0f));
    }

    @Benchmark
    public void benchmarkByMutableDecimal0f(Blackhole bh) {
        // Operation: this * factor (MutableDecimal0f)
        bh.consume(multipliable.by(factorMutableDecimal0f));
    }

    @Benchmark
    public int benchmarkHashCode() {
        return multipliable.hashCode();
    }

    @Benchmark
    public boolean benchmarkEquals_Equal(Blackhole bh) {
        bh.consume(multipliable.equals(multipliableEqual));
        return true;
    }

    @Benchmark
    public boolean benchmarkEquals_Different(Blackhole bh) {
        bh.consume(multipliable.equals(multipliableDifferent));
        return true;
    }

    @Benchmark
    public boolean benchmarkEquals_Null(Blackhole bh) {
        bh.consume(multipliable.equals(null));
        return true;
    }

    @Benchmark
    public String benchmarkToString(Blackhole bh) {
        bh.consume(multipliable.toString());
        return null;
    }
}
