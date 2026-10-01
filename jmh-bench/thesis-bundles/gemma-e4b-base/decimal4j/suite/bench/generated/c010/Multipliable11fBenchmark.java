package bench.generated.c010;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.exact.Multipliable11f;
import org.decimal4j.immutable.Decimal0f;
import org.decimal4j.immutable.Decimal1f;
import org.decimal4j.immutable.Decimal2f;
import org.decimal4j.immutable.Decimal3f;
import org.decimal4j.immutable.Decimal4f;
import org.decimal4j.immutable.Decimal5f;
import org.decimal4j.immutable.Decimal6f;
import org.decimal4j.immutable.Decimal7f;
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
import org.decimal4j.mutable.MutableDecimal5f;
import org.decimal4j.mutable.MutableDecimal6f;
import org.decimal4j.mutable.MutableDecimal7f;
import org.decimal4j.scale.Scale11f;
import org.decimal4j.api.Decimal;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Multipliable11fBenchmark {

    private Multipliable11f multipliable;
    private Multipliable11f equalInstance;

    // Base value for Multipliable11f (Decimal<Scale11f>)
    private Decimal11f baseDecimal;

    // Factors
    private Decimal0f factorD0;
    private MutableDecimal0f factorMD0;
    private Decimal1f factorD1;
    private MutableDecimal1f factorMD1;
    private Decimal2f factorD2;
    private MutableDecimal2f factorMD2;
    private Decimal3f factorD3;
    private MutableDecimal3f factorMD3;
    private Decimal4f factorD4;
    private MutableDecimal4f factorMD4;
    private Decimal5f factorD5;
    private MutableDecimal5f factorMD5;
    private Decimal6f factorD6;
    private MutableDecimal6f factorMD6;
    private Decimal7f factorD7;
    private MutableDecimal7f factorMD7;

    @Setup(Level.Trial)
    public void setup() {
        // Initialize base value (e.g., 1.0)
        baseDecimal = Decimal11f.ONE;
        multipliable = new Multipliable11f(baseDecimal);
        equalInstance = new Multipliable11f(baseDecimal);

        // Initialize factors (using simple values like 2.0 or 0.5)
        factorD0 = Decimal0f.TWO;
        factorMD0 = new MutableDecimal0f(2);

        factorD1 = Decimal1f.FIVE;
        factorMD1 = new MutableDecimal1f(5);

        factorD2 = Decimal2f.ONE;
        factorMD2 = new MutableDecimal2f(1);

        factorD3 = Decimal3f.HALF;
        factorMD3 = new MutableDecimal3f(0.5);

        factorD4 = Decimal4f.TEN;
        factorMD4 = new MutableDecimal4f(10);

        factorD5 = Decimal5f.TWO;
        factorMD5 = new MutableDecimal5f(2);

        factorD6 = Decimal6f.ONE;
        factorMD6 = new MutableDecimal6f(1);

        factorD7 = Decimal7f.HALF;
        factorMD7 = new MutableDecimal7f(0.5);
    }

    @Benchmark
    public void benchmark_by_D0(Blackhole bh) {
        Decimal11f result = multipliable.by(factorD0);
        bh.consume(result);
    }

    @Benchmark
    public void benchmark_by_MD0(Blackhole bh) {
        Decimal11f result = multipliable.by(factorMD0);
        bh.consume(result);
    }

    @Benchmark
    public void benchmark_by_D1(Blackhole bh) {
        Decimal12f result = multipliable.by(factorD1);
        bh.consume(result);
    }

    @Benchmark
    public void benchmark_by_MD1(Blackhole bh) {
        Decimal12f result = multipliable.by(factorMD1);
        bh.consume(result);
    }

    @Benchmark
    public void benchmark_by_D2(Blackhole bh) {
        Decimal13f result = multipliable.by(factorD2);
        bh.consume(result);
    }

    @Benchmark
    public void benchmark_by_MD2(Blackhole bh) {
        Decimal13f result = multipliable.by(factorMD2);
        bh.consume(result);
    }

    @Benchmark
    public void benchmark_by_D3(Blackhole bh) {
        Decimal14f result = multipliable.by(factorD3);
        bh.consume(result);
    }

    @Benchmark
    public void benchmark_by_MD3(Blackhole bh) {
        Decimal14f result = multipliable.by(factorMD3);
        bh.consume(result);
    }

    @Benchmark
    public void benchmark_by_D4(Blackhole bh) {
        Decimal15f result = multipliable.by(factorD4);
        bh.consume(result);
    }

    @Benchmark
    public void benchmark_by_MD4(Blackhole bh) {
        Decimal15f result = multipliable.by(factorMD4);
        bh.consume(result);
    }

    @Benchmark
    public void benchmark_by_D5(Blackhole bh) {
        Decimal16f result = multipliable.by(factorD5);
        bh.consume(result);
    }

    @Benchmark
    public void benchmark_by_MD5(Blackhole bh) {
        Decimal16f result = multipliable.by(factorMD5);
        bh.consume(result);
    }

    @Benchmark
    public void benchmark_by_D6(Blackhole bh) {
        Decimal17f result = multipliable.by(factorD6);
        bh.consume(result);
    }

    @Benchmark
    public void benchmark_by_MD6(Blackhole bh) {
        Decimal17f result = multipliable.by(factorMD6);
        bh.consume(result);
    }

    @Benchmark
    public void benchmark_by_D7(Blackhole bh) {
        Decimal18f result = multipliable.by(factorD7);
        bh.consume(result);
    }

    @Benchmark
    public void benchmark_by_MD7(Blackhole bh) {
        Decimal18f result = multipliable.by(factorMD7);
        bh.consume(result);
    }

    @Benchmark
    public void benchmark_getValue(Blackhole bh) {
        Decimal<Scale11f> result = multipliable.getValue();
        bh.consume(result);
    }

    @Benchmark
    public void benchmark_hashCode(Blackhole bh) {
        int result = multipliable.hashCode();
        bh.consume(result);
    }

    @Benchmark
    public void benchmark_equals(Blackhole bh) {
        boolean result = multipliable.equals(equalInstance);
        bh.consume(result);
    }

    @Benchmark
    public void benchmark_toString(Blackhole bh) {
        String result = multipliable.toString();
        bh.consume(result);
    }
}
