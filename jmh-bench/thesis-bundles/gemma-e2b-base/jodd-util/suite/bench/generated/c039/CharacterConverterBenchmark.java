package bench.generated.c039;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

import jodd.typeconverter.TypeConversionException;
import jodd.typeconverter.TypeConverter;
import jodd.util.StringUtil;
import jodd.typeconverter.impl.CharacterConverter;

/**
 * JMH benchmark suite for CharacterConverter.
 */
@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 5, time = 2)
@Measurement(iterations = 10, time = 5)
public class CharacterConverterBenchmark {

    private CharacterConverter converter;

    // --- Setup Fixtures ---

    // Input for null case
    private Object nullInput = null;

    // Input for Character case
    private Character charInput = 'A';

    // Input for Number case (Integer)
    private Integer numberInput = 42;

    // Input for Number case (Double)
    private Double doubleInput = 3.14;

    // Input for Single Character String case
    private String singleCharStringInput = "z";

    // Input for Multi-Character String (Success case: digits/signs)
    private String multiCharDigitStringInput = "123";

    // Input for Multi-Character String (Failure case: contains non-digit/sign)
    private String multiCharInvalidStringInput = "a123";

    // Input for Multi-Character String (Failure case: non-numeric structure)
    private String multiCharNonNumericStringInput = "abc";


    @Setup
    public void setup() {
        converter = new CharacterConverter();
    }

    // --- Benchmarks ---

    @Benchmark
    public void testNullInput(Blackhole bh) {
        Character result = converter.convert(nullInput);
        bh.consume(result);
    }

    @Benchmark
    public void testCharacterInput(Blackhole bh) {
        Character result = converter.convert(charInput);
        bh.consume(result);
    }

    @Benchmark
    public void testIntegerNumberInput(Blackhole bh) {
        Character result = converter.convert(numberInput);
        bh.consume(result);
    }

    @Benchmark
    public void testDoubleNumberInput(Blackhole bh) {
        Character result = converter.convert(doubleInput);
        bh.consume(result);
    }

    @Benchmark
    public void testSingleCharStringInput(Blackhole bh) {
        Character result = converter.convert(singleCharStringInput);
        bh.consume(result);
    }

    @Benchmark
    public void testMultiCharDigitStringSuccess(Blackhole bh) {
        Character result = converter.convert(multiCharDigitStringInput);
        bh.consume(result);
    }

    @Benchmark
    public void testMultiCharInvalidStringFailure(Blackhole bh) {
        // This path should throw TypeConversionException
        try {
            converter.convert(multiCharInvalidStringInput);
        } catch (TypeConversionException e) {
            // Expected behavior
        }
    }

    @Benchmark
    public void testMultiCharNonNumericStringFailure(Blackhole bh) {
        // This path should throw TypeConversionException
        try {
            converter.convert(multiCharNonNumericStringInput);
        } catch (TypeConversionException e) {
            // Expected behavior
        }
    }
}
