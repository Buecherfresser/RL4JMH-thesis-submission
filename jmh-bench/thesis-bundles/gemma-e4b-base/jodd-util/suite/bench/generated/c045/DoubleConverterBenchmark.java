package bench.generated.c045;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.typeconverter.impl.DoubleConverter;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class DoubleConverterBenchmark {

    private DoubleConverter converter;

    // Inputs for testing
    private Object nullInput;
    private Object doubleInput;
    private Object integerInput;
    private Object longInput;
    private Object booleanTrueInput;
    private Object booleanFalseInput;
    private Object standardStringInput;
    private Object signedStringInput;
    private Object negativeStringInput;

    @Setup(Level.Trial)
    public void setup() {
        converter = new DoubleConverter();

        // 1. Null input
        nullInput = null;

        // 2. Double input
        doubleInput = 123.456;

        // 3. Integer input
        integerInput = 42;

        // 4. Long input
        longInput = 9876543210L;

        // 5. Boolean input (True)
        booleanTrueInput = Boolean.TRUE;

        // 6. Boolean input (False)
        booleanFalseInput = Boolean.FALSE;

        // 7. Standard numeric string input
        standardStringInput = "123.45";

        // 8. Signed string input (Positive)
        signedStringInput = "+99.9";

        // 9. Negative string input
        negativeStringInput = "-50";
    }

    @Benchmark
    public Double testConvertNull(Blackhole bh) {
        Double result = converter.convert(nullInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Double testConvertDouble(Blackhole bh) {
        Double result = converter.convert(doubleInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Double testConvertInteger(Blackhole bh) {
        Double result = converter.convert(integerInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Double testConvertLong(Blackhole bh) {
        Double result = converter.convert(longInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Double testConvertBooleanTrue(Blackhole bh) {
        Double result = converter.convert(booleanTrueInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Double testConvertBooleanFalse(Blackhole bh) {
        Double result = converter.convert(booleanFalseInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Double testConvertStandardString(Blackhole bh) {
        Double result = converter.convert(standardStringInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Double testConvertSignedString(Blackhole bh) {
        Double result = converter.convert(signedStringInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Double testConvertNegativeString(Blackhole bh) {
        Double result = converter.convert(negativeStringInput);
        bh.consume(result);
        return result;
    }
}
