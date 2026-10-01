package bench.generated.c014;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.api.Decimal;
import org.decimal4j.immutable.Decimal0f;
import org.decimal4j.immutable.Decimal1f;
import org.decimal4j.immutable.Decimal2f;
import org.decimal4j.immutable.Decimal3f;
import org.decimal4j.immutable.Decimal15f;
import org.decimal4j.immutable.Decimal16f;
import org.decimal4j.immutable.Decimal17f;
import org.decimal4j.immutable.Decimal18f;
import org.decimal4j.mutable.MutableDecimal0f;
import org.decimal4j.mutable.MutableDecimal1f;
import org.decimal4j.mutable.MutableDecimal2f;
import org.decimal4j.mutable.MutableDecimal3f;
import org.decimal4j.scale.Scale15f;
import org.decimal4j.exact.Multipliable15f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Multipliable15fBenchmark {

    private Multipliable15f multipliable15f;

    // Inputs for Decimal0f
    private Decimal0f decimal0fInput;
    private MutableDecimal0f mutableDecimal0fInput;

    // Inputs for Decimal1f
    private Decimal1f decimal1fInput;
    private MutableDecimal1f mutableDecimal1fInput;

    // Inputs for Decimal2f
    private Decimal2f decimal2fInput;
    private MutableDecimal2f mutableDecimal2fInput;

    // Inputs for Decimal3f
    private Decimal3f decimal3fInput;
    private MutableDecimal3f mutableDecimal3fInput;

    @Setup
    public void setup() {
        // 1. Setup the base Multipliable15f object.
        // We need a base Decimal<Scale15f> value. Let's use Decimal15f.
        Decimal<Scale15f> baseDecimal = Decimal15f.valueOf(100L);
        multipliable15f = new Multipliable15f(baseDecimal);

        // 2. Setup Decimal0f inputs
        decimal0fInput = Decimal0f.valueOf(5L);
        mutableDecimal0fInput = new MutableDecimal0f(5L);

        // 3. Setup Decimal1f inputs
        decimal1fInput = Decimal1f.valueOf(2.5);
        mutableDecimal1fInput = new MutableDecimal1f(2.5);

        // 4. Setup Decimal2f inputs
        decimal2fInput = Decimal2f.valueOf(0.125);
        mutableDecimal2fInput = new MutableDecimal2f(0.125);

        // 5. Setup Decimal3f inputs
        decimal3fInput = Decimal3f.valueOf(0.001);
        mutableDecimal3fInput = new MutableDecimal3f(0.001);
    }

    @Benchmark
    public void by_Decimal0f(Blackhole bh) {
        Decimal15f result = multipliable15f.by(decimal0fInput);
        bh.consume(result);
    }

    @Benchmark
    public void by_MutableDecimal0f(Blackhole bh) {
        Decimal15f result = multipliable15f.by(mutableDecimal0fInput);
        bh.consume(result);
    }

    @Benchmark
    public void by_Decimal1f(Blackhole bh) {
        Decimal16f result = multipliable15f.by(decimal1fInput);
        bh.consume(result);
    }

    @Benchmark
    public void by_MutableDecimal1f(Blackhole bh) {
        Decimal16f result = multipliable15f.by(mutableDecimal1fInput);
        bh.consume(result);
    }

    @Benchmark
    public void by_Decimal2f(Blackhole bh) {
        Decimal17f result = multipliable15f.by(decimal2fInput);
        bh.consume(result);
    }

    @Benchmark
    public void by_MutableDecimal2f(Blackhole bh) {
        Decimal17f result = multipliable15f.by(mutableDecimal2fInput);
        bh.consume(result);
    }

    @Benchmark
    public void by_Decimal3f(Blackhole bh) {
        Decimal18f result = multipliable15f.by(decimal3fInput);
        bh.consume(result);
    }

    @Benchmark
    public void by_MutableDecimal3f(Blackhole bh) {
        Decimal18f result = multipliable15f.by(mutableDecimal3fInput);
        bh.consume(result);
    }
}
