package bench.generated.c056;

import jodd.typeconverter.impl.LongConverter;
import jodd.typeconverter.TypeConversionException;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.lang.Long;
import java.lang.Number;
import java.lang.Boolean;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
public class LongConverterBenchmark {

    private LongConverter converter;

    // Inputs for testing different conversion paths
    private Object nullInput;
    private Long longInput;
    private Number numberInput;
    private Boolean booleanTrueInput;
    private Boolean booleanFalseInput;
    private String stringPositiveInput;
    private String stringPlusInput;
    private String stringNegativeInput;

    @Setup
    public void setup() {
        converter = new LongConverter();

        // Case 1: Null
        nullInput = null;

        // Case 2: Existing Long
        longInput = 123456789L;

        // Case 3: Number (Integer)
        numberInput = 987654321;

        // Case 4: Boolean (True)
        booleanTrueInput = true;

        // Case 4: Boolean (False)
        booleanFalseInput = false;

        // Case 5: String (Positive, no sign)
        stringPositiveInput = "12345";

        // Case 6: String (Positive, starts with +)
        stringPlusInput = "+12345";

        // Case 7: String (Negative)
        stringNegativeInput = "-98765";
    }

    @Benchmark
    public void testNullConversion(Blackhole bh) {
        Long result = converter.convert(nullInput);
        bh.consume(result);
    }

    @Benchmark
    public void testLongConversion(Blackhole bh) {
        Long result = converter.convert(longInput);
        bh.consume(result);
    }

    @Benchmark
    public void testNumberConversion(Blackhole bh) {
        Long result = converter.convert(numberInput);
        bh.consume(result);
    }

    @Benchmark
    public void testBooleanTrueConversion(Blackhole bh) {
        Long result = converter.convert(booleanTrueInput);
        bh.consume(result);
    }

    @Benchmark
    public void testBooleanFalseConversion(Blackhole bh) {
        Long result = converter.convert(booleanFalseInput);
        bh.consume(result);
    }

    @Benchmark
    public void testStringConversionPositive(Blackhole bh) {
        Long result = converter.convert(stringPositiveInput);
        bh.consume(result);
    }

    @Benchmark
    public void testStringConversionWithPlus(Blackhole bh) {
        Long result = converter.convert(stringPlusInput);
        bh.consume(result);
    }

    @Benchmark
    public void testStringConversionNegative(Blackhole bh) {
        Long result = converter.convert(stringNegativeInput);
        bh.consume(result);
    }
}
