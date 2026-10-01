package bench.generated.c058;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.typeconverter.impl.ShortConverter;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ShortConverterBenchmark {

    private ShortConverter converter;

    // Inputs for testing different conversion paths
    private Object shortInput;
    private Object integerInput;
    private Object doubleInput;
    private Object booleanTrueInput;
    private Object booleanFalseInput;
    private Object stringValidPositive;
    private Object stringValidNegative;
    private Object stringValidWithPlus;
    private Object stringInvalid;

    @Setup(Level.Trial)
    public void setup() {
        converter = new ShortConverter();

        // 1. Short input (direct cast)
        shortInput = (short) 123;

        // 2. Integer input (Number conversion)
        integerInput = 456;

        // 3. Double input (Number conversion)
        doubleInput = 789.5;

        // 4. Boolean input
        booleanTrueInput = true;
        booleanFalseInput = false;

        // 5. String inputs
        stringValidPositive = "100";
        stringValidNegative = "-200";
        stringValidWithPlus = "+300";
        stringInvalid = "not_a_number";
    }

    @Benchmark
    public Short testConvert_ShortInput(Blackhole bh) {
        Short result = converter.convert(shortInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Short testConvert_IntegerInput(Blackhole bh) {
        Short result = converter.convert(integerInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Short testConvert_DoubleInput(Blackhole bh) {
        Short result = converter.convert(doubleInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Short testConvert_BooleanTrue(Blackhole bh) {
        Short result = converter.convert(booleanTrueInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Short testConvert_BooleanFalse(Blackhole bh) {
        Short result = converter.convert(booleanFalseInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Short testConvert_StringValidPositive(Blackhole bh) {
        Short result = converter.convert(stringValidPositive);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Short testConvert_StringValidNegative(Blackhole bh) {
        Short result = converter.convert(stringValidNegative);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Short testConvert_StringValidWithPlus(Blackhole bh) {
        Short result = converter.convert(stringValidWithPlus);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Short testConvert_NullInput(Blackhole bh) {
        Short result = converter.convert(null);
        bh.consume(result);
        return result;
    }

    // Note: Testing the exception path (stringInvalid) is generally discouraged in microbenchmarks
    // unless the exception handling itself is the target. Since the goal is to measure conversion speed,
    // we focus on successful paths. If we were to test the exception, we would need a try-catch block,
    // which complicates the benchmark structure.
}
