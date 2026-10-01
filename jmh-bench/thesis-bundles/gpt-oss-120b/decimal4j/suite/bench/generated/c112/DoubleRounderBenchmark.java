package bench.generated.c112;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.util.DoubleRounder;
import java.math.RoundingMode;
import java.util.Random;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class DoubleRounderBenchmark {

    private DoubleRounder rounderPrecision5;
    private DoubleRounder rounderPrecision7;
    private double[] values;
    private int index;

    @Setup(Level.Trial)
    public void setUp() {
        rounderPrecision5 = new DoubleRounder(5);
        rounderPrecision7 = new DoubleRounder(7);

        Random random = new Random(12345L);
        int size = 1024; // power of two for fast wrap‑around
        values = new double[size];
        for (int i = 0; i < size; i++) {
            // generate a wide range of finite double values (including subnormals)
            double v = (random.nextDouble() - 0.5) * 1e12;
            values[i] = v;
        }
        index = 0;
    }

    private double nextValue() {
        double v = values[index];
        index = (index + 1) & (values.length - 1);
        return v;
    }

    @Benchmark
    public double instanceRoundHalfUp() {
        double v = nextValue();
        return rounderPrecision5.round(v);
    }

    @Benchmark
    public double instanceRoundWithRoundingMode() {
        double v = nextValue();
        return rounderPrecision5.round(v, RoundingMode.DOWN);
    }

    @Benchmark
    public double staticRoundHalfUpPrecision5() {
        double v = nextValue();
        return DoubleRounder.round(v, 5);
    }

    @Benchmark
    public double staticRoundWithRoundingModePrecision7() {
        double v = nextValue();
        return DoubleRounder.round(v, 7, RoundingMode.CEILING);
    }

    @Benchmark
    public int getPrecision() {
        return rounderPrecision7.getPrecision();
    }

    @Benchmark
    public int hashCodeBenchmark() {
        return rounderPrecision5.hashCode();
    }

    @Benchmark
    public boolean equalsSameInstance() {
        return rounderPrecision5.equals(rounderPrecision5);
    }

    @Benchmark
    public boolean equalsDifferentInstance() {
        return rounderPrecision5.equals(rounderPrecision7);
    }

    @Benchmark
    public String toStringBenchmark() {
        return rounderPrecision5.toString();
    }
}
