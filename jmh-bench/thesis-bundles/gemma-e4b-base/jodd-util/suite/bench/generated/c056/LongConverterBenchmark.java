package bench.generated.c056;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.typeconverter.impl.LongConverter;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class LongConverterBenchmark {

    private LongConverter converter;

    // Inputs for testing different types
    private Object longInput;
    private Object integerInput;
    private Object doubleInput;
    private Object floatInput;
    private Object booleanTrueInput;
    private Object booleanFalseInput;
    private Object stringStandardInput;
    private Object stringPositiveInput;
    private Object stringNegativeInput;

    @Setup(Level.Trial)
    public void setup() {
        converter = new LongConverter();

        // 1. Long input
        longInput = 1234567890123L;

        // 2. Number inputs
        integerInput = 98765;
        doubleInput = 123.456;
        floatInput = 789.0f;

        // 3. Boolean inputs
        booleanTrueInput = true;
        booleanFalseInput = false;

        // 4. String inputs
        stringStandardInput = "54321";
        stringPositiveInput = "+100";
        stringNegativeInput = "-200";
    }

    @Benchmark
    public Long benchmarkLongInput(Blackhole bh) {
        Long result = converter.convert(longInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Long benchmarkIntegerInput(Blackhole bh) {
        Long result = converter.convert(integerInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Long benchmarkDoubleInput(Blackhole bh) {
        Long result = converter.convert(doubleInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Long benchmarkFloatInput(Blackhole bh) {
        Long result = converter.convert(floatInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Long benchmarkBooleanTrueInput(Blackhole bh) {
        Long result = converter.convert(booleanTrueInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Long benchmarkBooleanFalseInput(Blackhole bh) {
        Long result = converter.convert(booleanFalseInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Long benchmarkStringStandardInput(Blackhole bh) {
        Long result = converter.convert(stringStandardInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Long benchmarkStringPositiveInput(Blackhole bh) {
        Long result = converter.convert(stringPositiveInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Long benchmarkStringNegativeInput(Blackhole bh) {
        Long result = converter.convert(stringNegativeInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Long benchmarkNullInput(Blackhole bh) {
        Long result = converter.convert(null);
        bh.consume(result);
        return result;
    }
}
