package bench.generated.c020;

import org.openjdk.jmh.annotations.*;
import java.util.concurrent.TimeUnit;

import org.decimal4j.api.Decimal;
import org.decimal4j.scale.Scale3f;
import org.decimal4j.exact.Multipliable3f;
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
import org.decimal4j.mutable.MutableDecimal0f;
import org.decimal4j.mutable.MutableDecimal1f;
import org.decimal4j.mutable.MutableDecimal2f;
import org.decimal4j.mutable.MutableDecimal3f;
import org.decimal4j.mutable.MutableDecimal4f;
import org.decimal4j.mutable.MutableDecimal5f;
import org.decimal4j.mutable.MutableDecimal6f;
import org.decimal4j.mutable.MutableDecimal7f;
import org.decimal4j.mutable.MutableDecimal8f;
import org.decimal4j.mutable.MutableDecimal9f;
import org.decimal4j.mutable.MutableDecimal10f;
import org.decimal4j.mutable.MutableDecimal11f;
import org.decimal4j.mutable.MutableDecimal12f;
import org.decimal4j.mutable.MutableDecimal13f;
import org.decimal4j.mutable.MutableDecimal14f;
import org.decimal4j.mutable.MutableDecimal15f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Multipliable3fBenchmark {

    @State(Scope.Benchmark)
    public static class BenchState {
        Multipliable3f multipliable;
        Multipliable3f equalMultipliable;
        Decimal3f value;
        Decimal0f dec0;
        MutableDecimal0f mut0;
        Decimal1f dec1;
        MutableDecimal1f mut1;
        Decimal2f dec2;
        MutableDecimal2f mut2;
        Decimal3f dec3;
        MutableDecimal3f mut3;
        Decimal4f dec4;
        MutableDecimal4f mut4;
        Decimal5f dec5;
        MutableDecimal5f mut5;
        Decimal6f dec6;
        MutableDecimal6f mut6;
        Decimal7f dec7;
        MutableDecimal7f mut7;
        Decimal8f dec8;
        MutableDecimal8f mut8;
        Decimal9f dec9;
        MutableDecimal9f mut9;
        Decimal10f dec10;
        MutableDecimal10f mut10;
        Decimal11f dec11;
        MutableDecimal11f mut11;
        Decimal12f dec12;
        MutableDecimal12f mut12;
        Decimal13f dec13;
        MutableDecimal13f mut13;
        Decimal14f dec14;
        MutableDecimal14f mut14;
        Decimal15f dec15;
        MutableDecimal15f mut15;

        @Setup(Level.Trial)
        public void setup() {
            value = Decimal3f.valueOf("123.456");
            multipliable = new Multipliable3f(value);
            equalMultipliable = new Multipliable3f(Decimal3f.valueOf("123.456"));

            dec0 = Decimal0f.valueOf(2);
            mut0 = new MutableDecimal0f(2);
            dec1 = Decimal1f.valueOf("1.5");
            mut1 = new MutableDecimal1f("1.5");
            dec2 = Decimal2f.valueOf("0.25");
            mut2 = new MutableDecimal2f("0.25");
            dec3 = Decimal3f.valueOf("0.125");
            mut3 = new MutableDecimal3f("0.125");
            dec4 = Decimal4f.valueOf("0.0625");
            mut4 = new MutableDecimal4f("0.0625");
            dec5 = Decimal5f.valueOf("0.03125");
            mut5 = new MutableDecimal5f("0.03125");
            dec6 = Decimal6f.valueOf("0.015625");
            mut6 = new MutableDecimal6f("0.015625");
            dec7 = Decimal7f.valueOf("0.0078125");
            mut7 = new MutableDecimal7f("0.0078125");
            dec8 = Decimal8f.valueOf("0.00390625");
            mut8 = new MutableDecimal8f("0.00390625");
            dec9 = Decimal9f.valueOf("0.001953125");
            mut9 = new MutableDecimal9f("0.001953125");
            dec10 = Decimal10f.valueOf("0.0009765625");
            mut10 = new MutableDecimal10f("0.0009765625");
            dec11 = Decimal11f.valueOf("0.00048828125");
            mut11 = new MutableDecimal11f("0.00048828125");
            dec12 = Decimal12f.valueOf("0.000244140625");
            mut12 = new MutableDecimal12f("0.000244140625");
            dec13 = Decimal13f.valueOf("0.0001220703125");
            mut13 = new MutableDecimal13f("0.0001220703125");
            dec14 = Decimal14f.valueOf("0.00006103515625");
            mut14 = new MutableDecimal14f("0.00006103515625");
            dec15 = Decimal15f.valueOf("0.000030517578125");
            mut15 = new MutableDecimal15f("0.000030517578125");
        }
    }

    @Benchmark
    public Decimal<Scale3f> getValue(BenchState state) {
        return state.multipliable.getValue();
    }

    @Benchmark
    public Decimal6f square(BenchState state) {
        return state.multipliable.square();
    }

    @Benchmark
    public Decimal6f byDecimal3f(BenchState state) {
        return state.multipliable.by(state.dec3);
    }

    @Benchmark
    public Decimal6f byMutableDecimal3f(BenchState state) {
        return state.multipliable.by(state.mut3);
    }

    @Benchmark
    public Decimal3f byDecimal0f(BenchState state) {
        return state.multipliable.by(state.dec0);
    }

    @Benchmark
    public Decimal3f byMutableDecimal0f(BenchState state) {
        return state.multipliable.by(state.mut0);
    }

    @Benchmark
    public Decimal4f byDecimal1f(BenchState state) {
        return state.multipliable.by(state.dec1);
    }

    @Benchmark
    public Decimal4f byMutableDecimal1f(BenchState state) {
        return state.multipliable.by(state.mut1);
    }

    @Benchmark
    public Decimal5f byDecimal2f(BenchState state) {
        return state.multipliable.by(state.dec2);
    }

    @Benchmark
    public Decimal5f byMutableDecimal2f(BenchState state) {
        return state.multipliable.by(state.mut2);
    }

    @Benchmark
    public Decimal7f byDecimal4f(BenchState state) {
        return state.multipliable.by(state.dec4);
    }

    @Benchmark
    public Decimal7f byMutableDecimal4f(BenchState state) {
        return state.multipliable.by(state.mut4);
    }

    @Benchmark
    public Decimal8f byDecimal5f(BenchState state) {
        return state.multipliable.by(state.dec5);
    }

    @Benchmark
    public Decimal8f byMutableDecimal5f(BenchState state) {
        return state.multipliable.by(state.mut5);
    }

    @Benchmark
    public Decimal9f byDecimal6f(BenchState state) {
        return state.multipliable.by(state.dec6);
    }

    @Benchmark
    public Decimal9f byMutableDecimal6f(BenchState state) {
        return state.multipliable.by(state.mut6);
    }

    @Benchmark
    public Decimal10f byDecimal7f(BenchState state) {
        return state.multipliable.by(state.dec7);
    }

    @Benchmark
    public Decimal10f byMutableDecimal7f(BenchState state) {
        return state.multipliable.by(state.mut7);
    }

    @Benchmark
    public Decimal11f byDecimal8f(BenchState state) {
        return state.multipliable.by(state.dec8);
    }

    @Benchmark
    public Decimal11f byMutableDecimal8f(BenchState state) {
        return state.multipliable.by(state.mut8);
    }

    @Benchmark
    public Decimal12f byDecimal9f(BenchState state) {
        return state.multipliable.by(state.dec9);
    }

    @Benchmark
    public Decimal12f byMutableDecimal9f(BenchState state) {
        return state.multipliable.by(state.mut9);
    }

    @Benchmark
    public Decimal13f byDecimal10f(BenchState state) {
        return state.multipliable.by(state.dec10);
    }

    @Benchmark
    public Decimal13f byMutableDecimal10f(BenchState state) {
        return state.multipliable.by(state.mut10);
    }

    @Benchmark
    public Decimal14f byDecimal11f(BenchState state) {
        return state.multipliable.by(state.dec11);
    }

    @Benchmark
    public Decimal14f byMutableDecimal11f(BenchState state) {
        return state.multipliable.by(state.mut11);
    }

    @Benchmark
    public Decimal15f byDecimal12f(BenchState state) {
        return state.multipliable.by(state.dec12);
    }

    @Benchmark
    public Decimal15f byMutableDecimal12f(BenchState state) {
        return state.multipliable.by(state.mut12);
    }
}
