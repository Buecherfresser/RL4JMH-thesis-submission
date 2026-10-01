package bench.generated.c034;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.typeconverter.impl.BooleanConverter;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class BooleanConverterBenchmark {

    private BooleanConverter converter;

    private Object nullInput;
    private Object trueBoolean;
    private Object falseBoolean;
    private Object emptyString;
    private Object trueStringYes;
    private Object trueStringY;
    private Object trueStringTrue;
    private Object trueStringOn;
    private Object trueStringOne;
    private Object falseStringNo;
    private Object falseStringN;
    private Object falseStringFalse;
    private Object falseStringOff;
    private Object falseStringZero;
    private Object invalidString;

    @Setup(Level.Trial)
    public void setup() {
        converter = new BooleanConverter();

        nullInput = null;
        trueBoolean = Boolean.TRUE;
        falseBoolean = Boolean.FALSE;
        emptyString = "";
        trueStringYes = "yes";
        trueStringY = "y";
        trueStringTrue = "true";
        trueStringOn = "on";
        trueStringOne = "1";
        falseStringNo = "no";
        falseStringN = "n";
        falseStringFalse = "false";
        falseStringOff = "off";
        falseStringZero = "0";
        invalidString = "maybe";
    }

    @Benchmark
    public Boolean convertNull() {
        return converter.convert(nullInput);
    }

    @Benchmark
    public Boolean convertBooleanTrue() {
        return converter.convert(trueBoolean);
    }

    @Benchmark
    public Boolean convertBooleanFalse() {
        return converter.convert(falseBoolean);
    }

    @Benchmark
    public Boolean convertEmptyString() {
        return converter.convert(emptyString);
    }

    @Benchmark
    public Boolean convertStringYes() {
        return converter.convert(trueStringYes);
    }

    @Benchmark
    public Boolean convertStringY() {
        return converter.convert(trueStringY);
    }

    @Benchmark
    public Boolean convertStringTrue() {
        return converter.convert(trueStringTrue);
    }

    @Benchmark
    public Boolean convertStringOn() {
        return converter.convert(trueStringOn);
    }

    @Benchmark
    public Boolean convertStringOne() {
        return converter.convert(trueStringOne);
    }

    @Benchmark
    public Boolean convertStringNo() {
        return converter.convert(falseStringNo);
    }

    @Benchmark
    public Boolean convertStringN() {
        return converter.convert(falseStringN);
    }

    @Benchmark
    public Boolean convertStringFalse() {
        return converter.convert(falseStringFalse);
    }

    @Benchmark
    public Boolean convertStringOff() {
        return converter.convert(falseStringOff);
    }

    @Benchmark
    public Boolean convertStringZero() {
        return converter.convert(falseStringZero);
    }

    @Benchmark
    public boolean convertInvalidString(Blackhole bh) {
        try {
            converter.convert(invalidString);
            return false;
        } catch (Exception e) {
            bh.consume(e);
            return true;
        }
    }
}
