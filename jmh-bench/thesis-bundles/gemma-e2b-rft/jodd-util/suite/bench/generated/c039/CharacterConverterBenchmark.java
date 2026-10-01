package bench.generated.c039;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.typeconverter.impl.CharacterConverter;
import jodd.typeconverter.TypeConversionException;
import jodd.util.StringUtil;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CharacterConverterBenchmark {

    private CharacterConverter converter;

    // --- Setup Data ---
    private Object nullValue;
    private Character characterValue;
    private Number numberValue;
    private String singleCharString;
    private String multiDigitString;
    private String invalidString;

    @Setup
    public void setup() {
        converter = new CharacterConverter();

        // Case 1: Null input
        nullValue = null;

        // Case 2: Existing Character input
        characterValue = 'A';

        // Case 3: Number input (Integer)
        numberValue = 12345;

        // Case 4: Single character String input
        singleCharString = "Z";

        // Case 5: Multi-digit numeric String input (should convert via Integer.parseInt)
        multiDigitString = "98765";

        // Case 6: Invalid String input (contains non-digit/non-sign characters)
        invalidString = "A1B";
    }

    @Benchmark
    public void testNullInput(Blackhole bh) {
        Character result = converter.convert(nullValue);
        bh.consume(result);
    }

    @Benchmark
    public void testCharacterInput(Blackhole bh) {
        Character result = converter.convert(characterValue);
        bh.consume(result);
    }

    @Benchmark
    public void testNumberInput(Blackhole bh) {
        Character result = converter.convert(numberValue);
        bh.consume(result);
    }

    @Benchmark
    public void testSingleCharStringInput(Blackhole bh) {
        Character result = converter.convert(singleCharString);
        bh.consume(result);
    }

    @Benchmark
    public void testMultiDigitStringInput(Blackhole bh) {
        Character result = converter.convert(multiDigitString);
        bh.consume(result);
    }

    @Benchmark
    public void testInvalidStringInput(Blackhole bh) {
        // This test is expected to throw TypeConversionException.
        // JMH handles exceptions by counting them as failures unless explicitly caught.
        // We rely on the framework to measure the time taken to execute the conversion logic path.
        try {
            converter.convert(invalidString);
        } catch (TypeConversionException e) {
            // Successfully caught expected exception
        }
        bh.consume(null); // Consume null as the return value is not expected on failure
    }
}
