package bench.generated.c056;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.typeconverter.impl.LongConverter;
import jodd.typeconverter.TypeConversionException;

import java.util.ArrayList;
import java.util.List;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class LongConverterBenchmark {

    private LongConverter converter;

    // Inputs for testing different conversion paths
    private List<Object> numberInputs;
    private List<Object> booleanInputs;
    private List<String> stringInputs;
    private List<Object> nullInputs;

    // Test data for string conversion paths
    private final String POSITIVE_STRING = "1234567890";
    private final String NEGATIVE_STRING = "-9876543210";
    private final String POSITIVE_STRING_WITH_PLUS = "+1234567890";
    private final String STRING_WITH_WHITESPACE = "  55555  ";
    private final String INVALID_STRING = "not_a_number";

    @Setup(Level.Trial)
    public void setup() {
        converter = new LongConverter();

        // 1. Number inputs (Testing Number instanceof logic)
        numberInputs = new ArrayList<>();
        numberInputs.add(12345L);
        numberInputs.add(9876543210L);
        numberInputs.add(0L);
        numberInputs.add(Long.MAX_VALUE);
        numberInputs.add(Long.MIN_VALUE);

        // 2. Boolean inputs (Testing Boolean instanceof logic)
        booleanInputs = new ArrayList<>();
        booleanInputs.add(true);
        booleanInputs.add(false);

        // 3. String inputs (Testing String conversion logic)
        stringInputs = new ArrayList<>();
        stringInputs.add(POSITIVE_STRING);
        stringInputs.add(NEGATIVE_STRING);
        stringInputs.add(POSITIVE_STRING_WITH_PLUS);
        stringInputs.add(STRING_WITH_WHITESPACE);

        // 4. Null inputs (Testing null check)
        nullInputs = new ArrayList<>();
        nullInputs.add(null);
    }

    @Benchmark
    public void convert_null(Blackhole bh) {
        Long result = converter.convert(null);
        bh.consume(result);
    }

    @Benchmark
    public void convert_existing_long(Blackhole bh) {
        Long input = 12345L;
        Long result = converter.convert(input);
        bh.consume(result);
    }

    @Benchmark
    public void convert_number_type(Blackhole bh) {
        Long input = 9876543210L;
        Long result = converter.convert(input);
        bh.consume(result);
    }

    @Benchmark
    public void convert_boolean_true(Blackhole bh) {
        Boolean input = Boolean.TRUE;
        Long result = converter.convert(input);
        bh.consume(result);
    }

    @Benchmark
    public void convert_boolean_false(Blackhole bh) {
        Boolean input = Boolean.FALSE;
        Long result = converter.convert(input);
        bh.consume(result);
    }

    @Benchmark
    public void convert_string_positive(Blackhole bh) {
        String input = POSITIVE_STRING;
        Long result = converter.convert(input);
        bh.consume(result);
    }

    @Benchmark
    public void convert_string_negative(Blackhole bh) {
        String input = NEGATIVE_STRING;
        Long result = converter.convert(input);
        bh.consume(result);
    }

    @Benchmark
    public void convert_string_with_plus(Blackhole bh) {
        String input = POSITIVE_STRING_WITH_PLUS;
        Long result = converter.convert(input);
        bh.consume(result);
    }

    @Benchmark
    public void convert_string_with_whitespace(Blackhole bh) {
        String input = STRING_WITH_WHITESPACE;
        Long result = converter.convert(input);
        bh.consume(result);
    }

    @Benchmark
    public void convert_string_invalid(Blackhole bh) {
        String input = INVALID_STRING;
        // Test path that throws TypeConversionException
        try {
            converter.convert(input);
        } catch (TypeConversionException e) {
            // Exception caught, benchmark proceeds
        }
        bh.consume(null);
    }
}
