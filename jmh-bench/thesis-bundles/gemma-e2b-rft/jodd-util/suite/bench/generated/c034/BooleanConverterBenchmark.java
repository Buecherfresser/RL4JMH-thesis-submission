package bench.generated.c034;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.typeconverter.TypeConversionException;
import jodd.typeconverter.TypeConverter;
import jodd.typeconverter.impl.BooleanConverter;

import java.util.Arrays;
import java.util.List;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class BooleanConverterBenchmark {

    private BooleanConverter converter;

    // Inputs for testing different conversion paths
    private Object nullInput;
    private Boolean trueBoolean;
    private Boolean falseBoolean;

    // Test strings for TRUE conversion
    private List<String> trueStrings;
    // Test strings for FALSE conversion
    private List<String> falseStrings;
    // Test strings that should fail conversion
    private List<String> invalidStrings;

    @Setup
    public void setup() {
        converter = new BooleanConverter();

        // 1. Null case
        nullInput = null;

        // 2. Boolean cases
        trueBoolean = Boolean.TRUE;
        falseBoolean = Boolean.FALSE;

        // 3. String inputs for TRUE
        trueStrings = Arrays.asList("yes", "y", "true", "on", "1", "YES", "On");

        // 4. String inputs for FALSE
        falseStrings = Arrays.asList("no", "n", "false", "off", "0", "NO", "OFF");

        // 5. Invalid inputs (should throw TypeConversionException)
        invalidStrings = Arrays.asList("maybe", "invalid_value", "xyz");
    }

    @Benchmark
    public void testNullConversion(Blackhole bh) {
        Boolean result = converter.convert(nullInput);
        bh.consume(result);
    }

    @Benchmark
    public void testBooleanTrueConversion(Blackhole bh) {
        Boolean result = converter.convert(trueBoolean);
        bh.consume(result);
    }

    @Benchmark
    public void testBooleanFalseConversion(Blackhole bh) {
        Boolean result = converter.convert(falseBoolean);
        bh.consume(result);
    }

    @Benchmark
    public void testStringTrueConversion(Blackhole bh) {
        // Cycle through the list of true strings
        int index = 0;
        List<String> currentStrings = trueStrings;
        while (index < currentStrings.size()) {
            String input = currentStrings.get(index);
            Boolean result = converter.convert(input);
            bh.consume(result);
            index++;
        }
    }

    @Benchmark
    public void testStringFalseConversion(Blackhole bh) {
        // Cycle through the list of false strings
        int index = 0;
        List<String> currentStrings = falseStrings;
        while (index < currentStrings.size()) {
            String input = currentStrings.get(index);
            Boolean result = converter.convert(input);
            bh.consume(result);
            index++;
        }
    }

    @Benchmark
    public void testInvalidStringConversion(Blackhole bh) {
        // Test the path that throws TypeConversionException
        String input = invalidStrings.get(0);
        try {
            converter.convert(input);
        } catch (TypeConversionException e) {
            // Successfully caught expected exception
        }
    }
}
