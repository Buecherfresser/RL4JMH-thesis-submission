package bench.generated.c058;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Arrays;
import java.util.List;

import jodd.typeconverter.impl.ShortConverter;
import jodd.typeconverter.TypeConversionException;
import jodd.util.StringUtil;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ShortConverterBenchmark {

    private ShortConverter converter;

    // Inputs for testing different branches of ShortConverter.convert(Object value)
    private Object nullInput;
    private Short shortInput;
    private Integer numberInput;
    private Boolean booleanTrueInput;
    private Boolean booleanFalseInput;
    private String stringInputValid;
    private String stringInputWithPlus;
    private String stringInputInvalid;

    @Setup
    public void setup() {
        converter = new ShortConverter();

        // Test Case 1: null
        nullInput = null;

        // Test Case 2: Short
        shortInput = (short) 12345;

        // Test Case 3: Number (Integer)
        numberInput = 987654321;

        // Test Case 4: Boolean (True)
        booleanTrueInput = true;

        // Test Case 4: Boolean (False)
        booleanFalseInput = false;

        // Test Case 5: String (Valid, no sign, trimmed)
        stringInputValid = "42";

        // Test Case 6: String (Valid, starts with +)
        stringInputWithPlus = "+100";

        // Test Case 7: String (Invalid format, testing exception path)
        stringInputInvalid = "abc";
    }

    @Benchmark
    public void testNullInput(Blackhole bh) {
        Short result = converter.convert(nullInput);
        bh.consume(result);
    }

    @Benchmark
    public void testShortInput(Blackhole bh) {
        Short result = converter.convert(shortInput);
        bh.consume(result);
    }

    @Benchmark
    public void testNumberInput(Blackhole bh) {
        Short result = converter.convert(numberInput);
        bh.consume(result);
    }

    @Benchmark
    public void testBooleanTrueInput(Blackhole bh) {
        Short result = converter.convert(booleanTrueInput);
        bh.consume(result);
    }

    @Benchmark
    public void testBooleanFalseInput(Blackhole bh) {
        Short result = converter.convert(booleanFalseInput);
        bh.consume(result);
    }

    @Benchmark
    public void testStringInputValid(Blackhole bh) {
        Short result = converter.convert(stringInputValid);
        bh.consume(result);
    }

    @Benchmark
    public void testStringInputWithPlus(Blackhole bh) {
        Short result = converter.convert(stringInputWithPlus);
        bh.consume(result);
    }

    @Benchmark
    public void testStringInputInvalid(Blackhole bh) {
        // This test is expected to throw TypeConversionException, which JMH handles gracefully
        // by measuring the time taken to throw the exception.
        try {
            converter.convert(stringInputInvalid);
        } catch (TypeConversionException e) {
            // Successfully caught expected exception
        }
        bh.consume(null); // Consume null as the return value is not applicable here
    }
}
