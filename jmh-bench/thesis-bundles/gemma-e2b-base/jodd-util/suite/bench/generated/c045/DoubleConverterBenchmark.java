package bench.generated.c045;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.typeconverter.impl.DoubleConverter;
import jodd.typeconverter.TypeConversionException;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
public class DoubleConverterBenchmark {

    private DoubleConverter converter;

    // Inputs for testing different conversion paths
    private Object nullInput;
    private Double doubleInput;
    private Integer numberInput;
    private Boolean booleanInputTrue;
    private Boolean booleanInputFalse;
    private String stringInputStandard;
    private String stringInputWithPlus;
    private String stringInputInvalid;

    @Setup
    public void setup() {
        converter = new DoubleConverter();

        // Case 1: null
        nullInput = null;

        // Case 2: Existing Double
        doubleInput = 3.14159;

        // Case 3: Number (Integer)
        numberInput = 100;

        // Case 4: Boolean (True)
        booleanInputTrue = true;

        // Case 4: Boolean (False)
        booleanInputFalse = false;

        // Case 5: Standard String
        stringInputStandard = "123.45";

        // Case 6: String with leading plus sign
        stringInputWithPlus = "+987.65";

        // Case 7: Invalid String (to test exception path)
        stringInputInvalid = "not_a_number";
    }

    @Benchmark
    public void testNullConversion(Blackhole bh) {
        Double result = converter.convert(nullInput);
        bh.consume(result);
    }

    @Benchmark
    public void testDoubleConversion(Blackhole bh) {
        Double result = converter.convert(doubleInput);
        bh.consume(result);
    }

    @Benchmark
    public void testNumberConversion(Blackhole bh) {
        Double result = converter.convert(numberInput);
        bh.consume(result);
    }

    @Benchmark
    public void testBooleanTrueConversion(Blackhole bh) {
        Double result = converter.convert(booleanInputTrue);
        bh.consume(result);
    }

    @Benchmark
    public void testBooleanFalseConversion(Blackhole bh) {
        Double result = converter.convert(booleanInputFalse);
        bh.consume(result);
    }

    @Benchmark
    public void testStringConversionStandard(Blackhole bh) {
        Double result = converter.convert(stringInputStandard);
        bh.consume(result);
    }

    @Benchmark
    public void testStringConversionWithPlus(Blackhole bh) {
        Double result = converter.convert(stringInputWithPlus);
        bh.consume(result);
    }

    @Benchmark
    public void testStringConversionInvalid(Blackhole bh) {
        // This test is expected to throw TypeConversionException, which JMH handles gracefully
        // by measuring the time taken to throw the exception.
        try {
            converter.convert(stringInputInvalid);
        } catch (TypeConversionException e) {
            // Successfully caught expected exception
        }
        bh.consume(null); // Consume null as the return value is not expected on failure
    }
}
