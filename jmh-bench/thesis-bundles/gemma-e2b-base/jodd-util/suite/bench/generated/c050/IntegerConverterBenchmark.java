package bench.generated.c050;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

import jodd.typeconverter.impl.IntegerConverter;
import jodd.typeconverter.TypeConversionException;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class IntegerConverterBenchmark {

    private IntegerConverter converter;

    // Inputs for testing different conversion paths
    private Object nullInput;
    private Integer integerInput;
    private Double numberInput;
    private Boolean booleanTrueInput;
    private Boolean booleanFalseInput;
    private String stringPositiveInput;
    private String stringNegativeInput;
    private String stringTrimmedInput;
    private String stringWithPlusInput;
    private String stringInvalidInput;

    @Setup
    public void setup() {
        converter = new IntegerConverter();

        // Case 1: null
        nullInput = null;

        // Case 2: Integer
        integerInput = 12345;

        // Case 3: Number (Double)
        numberInput = 987.65;

        // Case 4: Boolean (True)
        booleanTrueInput = true;

        // Case 4: Boolean (False)
        booleanFalseInput = false;

        // Case 5: String inputs
        stringPositiveInput = "123";
        stringNegativeInput = "-456";
        stringTrimmedInput = "  789  ";
        stringWithPlusInput = "+100";
        stringInvalidInput = "abc";
    }

    @Benchmark
    public void testNullConversion(Blackhole bh) {
        Integer result = converter.convert(nullInput);
        bh.consume(result);
    }

    @Benchmark
    public void testIntegerConversion(Blackhole bh) {
        Integer result = converter.convert(integerInput);
        bh.consume(result);
    }

    @Benchmark
    public void testNumberConversion(Blackhole bh) {
        Integer result = converter.convert(numberInput);
        bh.consume(result);
    }

    @Benchmark
    public void testBooleanTrueConversion(Blackhole bh) {
        Integer result = converter.convert(booleanTrueInput);
        bh.consume(result);
    }

    @Benchmark
    public void testBooleanFalseConversion(Blackhole bh) {
        Integer result = converter.convert(booleanFalseInput);
        bh.consume(result);
    }

    @Benchmark
    public void testStringPositiveConversion(Blackhole bh) {
        Integer result = converter.convert(stringPositiveInput);
        bh.consume(result);
    }

    @Benchmark
    public void testStringNegativeConversion(Blackhole bh) {
        Integer result = converter.convert(stringNegativeInput);
        bh.consume(result);
    }

    @Benchmark
    public void testStringTrimmedConversion(Blackhole bh) {
        Integer result = converter.convert(stringTrimmedInput);
        bh.consume(result);
    }

    @Benchmark
    public void testStringWithPlusConversion(Blackhole bh) {
        Integer result = converter.convert(stringWithPlusInput);
        bh.consume(result);
    }

    @Benchmark
    public void testStringInvalidConversion(Blackhole bh) {
        // Measure the time taken to execute the conversion logic, including throwing the exception.
        try {
            converter.convert(stringInvalidInput);
        } catch (TypeConversionException e) {
            // Consume the exception if caught, ensuring the call is measured.
        }
    }
}
