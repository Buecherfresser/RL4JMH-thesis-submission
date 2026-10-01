package bench.generated.c058;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.typeconverter.impl.ShortConverter;
import java.lang.Short;
import java.util.Arrays;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ShortConverterBenchmark {

    private ShortConverter converter;

    // Inputs for testing different conversion paths
    private Object nullInput;
    private Short shortInput;
    private Integer numberInput;
    private Boolean booleanTrueInput;
    private Boolean booleanFalseInput;
    private String stringPositiveInput;
    private String stringNegativeInput;
    private String stringWithPlusInput;
    private String stringInvalidInput;

    @Setup
    public void setup() {
        converter = new ShortConverter();

        // Case 1: Null
        nullInput = null;

        // Case 2: Short
        shortInput = Short.valueOf((short) 42);

        // Case 3: Number (Integer)
        numberInput = 1000;

        // Case 4: Boolean (True)
        booleanTrueInput = true;

        // Case 4: Boolean (False)
        booleanFalseInput = false;

        // Case 5: String (Positive)
        stringPositiveInput = "12345";

        // Case 5: String (Negative)
        stringNegativeInput = "-987";

        // Case 5: String (Starts with +)
        stringWithPlusInput = "+500";

        // Case 5: String (Invalid format, should throw NFE)
        stringInvalidInput = "not_a_number";
    }

    @Benchmark
    public void testNullConversion(Blackhole bh) {
        Short result = converter.convert(nullInput);
        bh.consume(result);
    }

    @Benchmark
    public void testShortConversion(Blackhole bh) {
        Short result = converter.convert(shortInput);
        bh.consume(result);
    }

    @Benchmark
    public void testNumberConversion(Blackhole bh) {
        Short result = converter.convert(numberInput);
        bh.consume(result);
    }

    @Benchmark
    public void testBooleanTrueConversion(Blackhole bh) {
        Short result = converter.convert(booleanTrueInput);
        bh.consume(result);
    }

    @Benchmark
    public void testBooleanFalseConversion(Blackhole bh) {
        Short result = converter.convert(booleanFalseInput);
        bh.consume(result);
    }

    @Benchmark
    public void testStringPositiveConversion(Blackhole bh) {
        Short result = converter.convert(stringPositiveInput);
        bh.consume(result);
    }

    @Benchmark
    public void testStringNegativeConversion(Blackhole bh) {
        Short result = converter.convert(stringNegativeInput);
        bh.consume(result);
    }

    @Benchmark
    public void testStringWithPlusConversion(Blackhole bh) {
        Short result = converter.convert(stringWithPlusInput);
        bh.consume(result);
    }

    @Benchmark
    public void testStringInvalidConversion(Blackhole bh) {
        // This test is expected to throw TypeConversionException, which JMH handles gracefully
        try {
            converter.convert(stringInvalidInput);
        } catch (Exception e) {
            // Consume the exception or just let the benchmark run, focusing on the execution path.
        }
    }
}
