package bench.generated.c048;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.typeconverter.impl.FloatConverter;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class FloatConverterBenchmark {

    private FloatConverter converter;

    // Inputs for testing different conversion paths
    private Object floatInput;
    private Object integerInput;
    private Object doubleInput;
    private Object booleanTrueInput;
    private Object booleanFalseInput;
    private Object stringInputStandard;
    private Object stringInputSigned;

    @Setup(Level.Trial)
    public void setup() {
        converter = new FloatConverter();

        // 1. Direct Float input
        floatInput = 123.45f;

        // 2. Integer input
        integerInput = 42;

        // 3. Double input
        doubleInput = 987.654321;

        // 4. Boolean inputs
        booleanTrueInput = true;
        booleanFalseInput = false;

        // 5. String inputs
        stringInputStandard = "123.45";
        stringInputSigned = "+-99.99"; // Testing both + and - signs
    }

    @Benchmark
    public Float benchmarkFloatToFloat() {
        return converter.convert(floatInput);
    }

    @Benchmark
    public Float benchmarkIntegerToFloat() {
        return converter.convert(integerInput);
    }

    @Benchmark
    public Float benchmarkDoubleToFloat() {
        return converter.convert(doubleInput);
    }

    @Benchmark
    public Float benchmarkBooleanTrueToFloat() {
        return converter.convert(booleanTrueInput);
    }

    @Benchmark
    public Float benchmarkBooleanFalseToFloat() {
        return converter.convert(booleanFalseInput);
    }

    @Benchmark
    public Float benchmarkStringToFloatStandard() {
        return converter.convert(stringInputStandard);
    }

    @Benchmark
    public Float benchmarkStringToFloatSigned() {
        return converter.convert(stringInputSigned);
    }
}
