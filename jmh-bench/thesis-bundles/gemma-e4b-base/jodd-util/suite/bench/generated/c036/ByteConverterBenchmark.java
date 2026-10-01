package bench.generated.c036;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.typeconverter.impl.ByteConverter;
import java.lang.Byte;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Double;
import java.lang.Boolean;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ByteConverterBenchmark {

    private ByteConverter converter;

    // Inputs for testing different types
    private Object byteInput;
    private Object integerInput;
    private Object longInput;
    private Object doubleInput;
    private Object booleanTrueInput;
    private Object booleanFalseInput;
    private Object stringPositiveInput;
    private Object stringNegativeInput;
    private Object stringWithPlusInput;
    private Object stringTrimmedInput;
    private Object nullInput;

    @Setup(Level.Trial)
    public void setup() {
        converter = new ByteConverter();

        // 1. Byte input
        byteInput = Byte.valueOf((byte) 42);

        // 2. Number inputs
        integerInput = Integer.valueOf(100);
        longInput = Long.valueOf(-500L);
        doubleInput = Double.valueOf(12.5);

        // 3. Boolean inputs
        booleanTrueInput = Boolean.TRUE;
        booleanFalseInput = Boolean.FALSE;

        // 4. String inputs
        stringPositiveInput = "123";
        stringNegativeInput = "-45";
        stringWithPlusInput = "+99";
        stringTrimmedInput = "  -10  ";

        // 5. Null input
        nullInput = null;
    }

    @Benchmark
    public Byte benchmarkConvertByteInput(Blackhole bh) {
        Byte result = converter.convert(byteInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Byte benchmarkConvertIntegerInput(Blackhole bh) {
        Byte result = converter.convert(integerInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Byte benchmarkConvertLongInput(Blackhole bh) {
        Byte result = converter.convert(longInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Byte benchmarkConvertDoubleInput(Blackhole bh) {
        Byte result = converter.convert(doubleInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Byte benchmarkConvertBooleanTrue(Blackhole bh) {
        Byte result = converter.convert(booleanTrueInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Byte benchmarkConvertBooleanFalse(Blackhole bh) {
        Byte result = converter.convert(booleanFalseInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Byte benchmarkConvertStringPositive(Blackhole bh) {
        Byte result = converter.convert(stringPositiveInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Byte benchmarkConvertStringNegative(Blackhole bh) {
        Byte result = converter.convert(stringNegativeInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Byte benchmarkConvertStringWithPlus(Blackhole bh) {
        Byte result = converter.convert(stringWithPlusInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Byte benchmarkConvertStringTrimmed(Blackhole bh) {
        Byte result = converter.convert(stringTrimmedInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Byte benchmarkConvertNullInput(Blackhole bh) {
        Byte result = converter.convert(nullInput);
        bh.consume(result);
        return result;
    }
}
