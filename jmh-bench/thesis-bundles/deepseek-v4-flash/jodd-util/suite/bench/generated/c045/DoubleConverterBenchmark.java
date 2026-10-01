package bench.generated.c045;

import org.openjdk.jmh.annotations.*;
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

    private Object nullValue;
    private Object doubleValue;
    private Object intValue;
    private Object longValue;
    private Object boolTrue;
    private Object boolFalse;
    private Object stringValue;
    private Object stringWithPlus;
    private Object stringWithSpaces;

    @Setup(Level.Trial)
    public void setUp() {
        converter = new DoubleConverter();
        nullValue = null;
        doubleValue = Double.valueOf(3.14);
        intValue = Integer.valueOf(42);
        longValue = Long.valueOf(123456789L);
        boolTrue = Boolean.valueOf(true);
        boolFalse = Boolean.valueOf(false);
        stringValue = "2.718";
        stringWithPlus = "+0.577";
        stringWithSpaces = "  123.456";
    }

    @Benchmark
    public Double convertNull() {
        return converter.convert(nullValue);
    }

    @Benchmark
    public Double convertDouble() {
        return converter.convert(doubleValue);
    }

    @Benchmark
    public Double convertInteger() {
        return converter.convert(intValue);
    }

    @Benchmark
    public Double convertLong() {
        return converter.convert(longValue);
    }

    @Benchmark
    public Double convertBooleanTrue() {
        return converter.convert(boolTrue);
    }

    @Benchmark
    public Double convertBooleanFalse() {
        return converter.convert(boolFalse);
    }

    @Benchmark
    public Double convertString() {
        return converter.convert(stringValue);
    }

    @Benchmark
    public Double convertStringWithPlus() {
        return converter.convert(stringWithPlus);
    }

    @Benchmark
    public Double convertStringWithSpaces() {
        return converter.convert(stringWithSpaces);
    }
}
