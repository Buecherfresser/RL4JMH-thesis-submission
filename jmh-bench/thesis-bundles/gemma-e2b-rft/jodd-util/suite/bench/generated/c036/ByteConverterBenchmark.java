package bench.generated.c036;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.typeconverter.impl.ByteConverter;
import jodd.typeconverter.TypeConversionException;
import java.lang.Byte;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ByteConverterBenchmark {

    private ByteConverter converter;

    // Inputs for testing different conversion paths
    private Object nullInput;
    private Byte byteInput;
    private Integer numberInput;
    private Boolean booleanTrueInput;
    private Boolean booleanFalseInput;
    private String stringInputValid;
    private String stringInputValidWithPlus;
    private String stringInputInvalid;

    @Setup
    public void setup() {
        converter = new ByteConverter();

        // Path 1: Null
        nullInput = null;

        // Path 2: Byte
        byteInput = Byte.valueOf((byte) 100);

        // Path 3: Number (Integer)
        numberInput = Integer.valueOf(12345);

        // Path 4: Boolean
        booleanTrueInput = true;
        booleanFalseInput = false;

        // Path 5: String (Valid)
        stringInputValid = "12345";

        // Path 6: String (Valid with +)
        stringInputValidWithPlus = "+9876";

        // Path 7: String (Invalid, should throw TypeConversionException)
        stringInputInvalid = "not_a_number";
    }

    @Benchmark
    public void testNullConversion(Blackhole bh) {
        Byte result = converter.convert(nullInput);
        bh.consume(result);
    }

    @Benchmark
    public void testByteConversion(Blackhole bh) {
        Byte result = converter.convert(byteInput);
        bh.consume(result);
    }

    @Benchmark
    public void testNumberConversion(Blackhole bh) {
        Byte result = converter.convert(numberInput);
        bh.consume(result);
    }

    @Benchmark
    public void testBooleanTrueConversion(Blackhole bh) {
        Byte result = converter.convert(booleanTrueInput);
        bh.consume(result);
    }

    @Benchmark
    public void testBooleanFalseConversion(Blackhole bh) {
        Byte result = converter.convert(booleanFalseInput);
        bh.consume(result);
    }

    @Benchmark
    public void testStringConversionValid(Blackhole bh) {
        Byte result = converter.convert(stringInputValid);
        bh.consume(result);
    }

    @Benchmark
    public void testStringConversionWithPlus(Blackhole bh) {
        Byte result = converter.convert(stringInputValidWithPlus);
        bh.consume(result);
    }

    @Benchmark
    public void testStringConversionInvalid(Blackhole bh) {
        // This test is expected to throw TypeConversionException
        try {
            converter.convert(stringInputInvalid);
        } catch (TypeConversionException e) {
            // Consume the exception or just ensure the benchmark runs without crashing
            bh.consume(e);
        }
    }
}
