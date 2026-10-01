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

    // Inputs prepared in @Setup
    private Object nullInput;
    private Object doubleInput;
    private Object integerInput;
    private Object longInput;
    private Object floatInput;
    private Object booleanTrueInput;
    private Object booleanFalseInput;
    private Object stringWithPlusInput;
    private Object stringPlainInput;
    private Object stringWhitespaceInput;

    @Setup(Level.Trial)
    public void setUp() {
        converter = new DoubleConverter();

        nullInput = null;
        doubleInput = Double.valueOf(123.456);
        integerInput = Integer.valueOf(42);
        longInput = Long.valueOf(9876543210L);
        floatInput = Float.valueOf(3.14f);
        booleanTrueInput = Boolean.TRUE;
        booleanFalseInput = Boolean.FALSE;
        stringWithPlusInput = "+789.01";
        stringPlainInput = "456.78";
        stringWhitespaceInput = "   321.0   ";
    }

    @Benchmark
    public Double convertNull() {
        return converter.convert(nullInput);
    }

    @Benchmark
    public Double convertDouble() {
        return converter.convert(doubleInput);
    }

    @Benchmark
    public Double convertInteger() {
        return converter.convert(integerInput);
    }

    @Benchmark
    public Double convertLong() {
        return converter.convert(longInput);
    }

    @Benchmark
    public Double convertFloat() {
        return converter.convert(floatInput);
    }

    @Benchmark
    public Double convertBooleanTrue() {
        return converter.convert(booleanTrueInput);
    }

    @Benchmark
    public Double convertBooleanFalse() {
        return converter.convert(booleanFalseInput);
    }

    @Benchmark
    public Double convertStringWithPlus() {
        return converter.convert(stringWithPlusInput);
    }

    @Benchmark
    public Double convertStringPlain() {
        return converter.convert(stringPlainInput);
    }

    @Benchmark
    public Double convertStringWhitespace() {
        return converter.convert(stringWhitespaceInput);
    }

    // Example of using Blackhole for a void benchmark (not required here)
    @Benchmark
    public void consumeResult(Blackhole bh) {
        bh.consume(converter.convert(stringPlainInput));
    }
}
