package bench.generated.c015;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.exact.Multipliable16f;
import org.decimal4j.immutable.Decimal0f;
import org.decimal4j.immutable.Decimal1f;
import org.decimal4j.immutable.Decimal2f;
import org.decimal4j.immutable.Decimal16f;
import org.decimal4j.immutable.Decimal17f;
import org.decimal4j.immutable.Decimal18f;
import org.decimal4j.mutable.MutableDecimal0f;
import org.decimal4j.mutable.MutableDecimal1f;
import org.decimal4j.mutable.MutableDecimal2f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Multipliable16fBenchmark {

    private Multipliable16f multipliable16f;

    // Factors
    private Decimal0f factorD0f;
    private MutableDecimal0f factorMD0f;
    private Decimal1f factorD1f;
    private MutableDecimal1f factorMD1f;
    private Decimal2f factorD2f;
    private MutableDecimal2f factorMD2f;

    // Base value for Multipliable16f
    private Decimal16f baseValue;

    @Setup(Level.Trial)
    public void setup() {
        // 1. Setup base value
        // Using a simple non-zero value for the base Decimal16f
        baseValue = Decimal16f.valueOf(123456789012345L);

        // 2. Setup Multipliable16f subject
        multipliable16f = new Multipliable16f(baseValue);

        // 3. Setup factors
        factorD0f = Decimal0f.valueOf(5L);
        factorMD0f = new MutableDecimal0f(5L);

        factorD1f = Decimal1f.valueOf(2L);
        factorMD1f = new MutableDecimal1f(2L);

        factorD2f = Decimal2f.valueOf(3L);
        factorMD2f = new MutableDecimal2f(3L);
    }

    @Benchmark
    public Decimal16f by_Decimal0f(Blackhole bh) {
        Decimal16f result = multipliable16f.by(factorD0f);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal16f by_MutableDecimal0f(Blackhole bh) {
        Decimal16f result = multipliable16f.by(factorMD0f);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal17f by_Decimal1f(Blackhole bh) {
        Decimal17f result = multipliable16f.by(factorD1f);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal17f by_MutableDecimal1f(Blackhole bh) {
        Decimal17f result = multipliable16f.by(factorMD1f);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal18f by_Decimal2f(Blackhole bh) {
        Decimal18f result = multipliable16f.by(factorD2f);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal18f by_MutableDecimal2f(Blackhole bh) {
        Decimal18f result = multipliable16f.by(factorMD2f);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public void getValue(Blackhole bh) {
        bh.consume(multipliable16f.getValue());
    }

    @Benchmark
    public int hashCode(Blackhole bh) {
        int hash = multipliable16f.hashCode();
        bh.consume(hash);
        return hash;
    }

    @Benchmark
    public String toString(Blackhole bh) {
        String s = multipliable16f.toString();
        bh.consume(s);
        return s;
    }
}
