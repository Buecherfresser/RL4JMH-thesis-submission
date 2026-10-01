package bench.generated.c024;

import org.decimal4j.exact.Multipliable7f;
import org.decimal4j.immutable.Decimal0f;
import org.decimal4j.immutable.Decimal10f;
import org.decimal4j.immutable.Decimal11f;
import org.decimal4j.immutable.Decimal1f;
import org.decimal4j.immutable.Decimal2f;
import org.decimal4j.immutable.Decimal3f;
import org.decimal4j.immutable.Decimal4f;
import org.decimal4j.immutable.Decimal5f;
import org.decimal4j.immutable.Decimal6f;
import org.decimal4j.immutable.Decimal7f;
import org.decimal4j.immutable.Decimal8f;
import org.decimal4j.immutable.Decimal9f;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Multipliable7fBenchmark {

    private Multipliable7f multipliable;
    private Multipliable7f otherMultipliable;

    private Decimal7f value7;
    private Decimal0f factor0;
    private Decimal1f factor1;
    private Decimal2f factor2;
    private Decimal3f factor3;
    private Decimal4f factor4;
    private Decimal5f factor5;
    private Decimal6f factor6;
    private Decimal8f factor8;
    private Decimal9f factor9;
    private Decimal10f factor10;
    private Decimal11f factor11;

    @Setup(Level.Trial)
    public void setup() {
        value7 = Decimal7f.valueOf("123.4567");
        multipliable = new Multipliable7f(value7);
        otherMultipliable = new Multipliable7f(Decimal7f.valueOf("0.0001"));

        factor0 = Decimal0f.valueOf("2");
        factor1 = Decimal1f.valueOf("1.5");
        factor2 = Decimal2f.valueOf("2.25");
        factor3 = Decimal3f.valueOf("3.125");
        factor4 = Decimal4f.valueOf("4.0625");
        factor5 = Decimal5f.valueOf("5.03125");
        factor6 = Decimal6f.valueOf("6.015625");
        factor8 = Decimal8f.valueOf("8.00390625");
        factor9 = Decimal9f.valueOf("9.001953125");
        factor10 = Decimal10f.valueOf("10.0009765625");
        factor11 = Decimal11f.valueOf("11.00048828125");
    }

    @Benchmark
    public Object byDecimal7f() {
        return multipliable.by(value7);
    }

    @Benchmark
    public Object byDecimal0f() {
        return multipliable.by(factor0);
    }

    @Benchmark
    public Object byDecimal1f() {
        return multipliable.by(factor1);
    }

    @Benchmark
    public Object byDecimal2f() {
        return multipliable.by(factor2);
    }

    @Benchmark
    public Object byDecimal3f() {
        return multipliable.by(factor3);
    }

    @Benchmark
    public Object byDecimal4f() {
        return multipliable.by(factor4);
    }

    @Benchmark
    public Object byDecimal5f() {
        return multipliable.by(factor5);
    }

    @Benchmark
    public Object byDecimal6f() {
        return multipliable.by(factor6);
    }

    @Benchmark
    public Object byDecimal8f() {
        return multipliable.by(factor8);
    }

    @Benchmark
    public Object byDecimal9f() {
        return multipliable.by(factor9);
    }

    @Benchmark
    public Object byDecimal10f() {
        return multipliable.by(factor10);
    }

    @Benchmark
    public Object byDecimal11f() {
        return multipliable.by(factor11);
    }

    @Benchmark
    public Object square() {
        return multipliable.square();
    }

    @Benchmark
    public Object getValue() {
        return multipliable.getValue();
    }

    @Benchmark
    public int hashCodeBenchmark() {
        return multipliable.hashCode();
    }

    @Benchmark
    public boolean equalsBenchmark() {
        return multipliable.equals(otherMultipliable);
    }

    @Benchmark
    public String toStringBenchmark() {
        return multipliable.toString();
    }
}
