package bench.generated.c039;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.typeconverter.impl.CharacterConverter;
import java.lang.Character;
import java.lang.Integer;
import java.lang.String;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CharacterConverterBenchmark {

    private CharacterConverter converter;

    // Inputs for testing different conversion paths
    private Character characterInput;
    private Integer numberInput;
    private String singleCharStringInput;
    private String multiDigitNumericStringInput;
    private String multiCharNonNumericStringInput;
    private String multiCharNumericStringInput; // For testing the path where it tries Integer.parseInt

    @Setup(Level.Trial)
    public void setup() {
        converter = new CharacterConverter();

        // 1. Character input
        characterInput = Character.valueOf('A');

        // 2. Number input (Integer)
        numberInput = 65; // ASCII for 'A'

        // 3. Single character string input
        singleCharStringInput = "Z";

        // 4. Multi-digit numeric string input (should convert via Integer.parseInt)
        multiDigitNumericStringInput = "100";

        // 5. Multi-character non-numeric string input (should throw TypeConversionException)
        multiCharNonNumericStringInput = "ABC";

        // 6. Multi-digit string that is purely numeric (to test the specific path)
        multiCharNumericStringInput = "99999";
    }

    @Benchmark
    public Character convertCharacter(Blackhole bh) {
        Character result = converter.convert(characterInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Character convertNumber(Blackhole bh) {
        Character result = converter.convert(numberInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Character convertSingleCharString(Blackhole bh) {
        Character result = converter.convert(singleCharStringInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Character convertMultiDigitNumericString(Blackhole bh) {
        // This tests the path where length > 1, contains only digits/signs, and successfully parses as int.
        Character result = converter.convert(multiDigitNumericStringInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Character convertMultiCharNumericString(Blackhole bh) {
        // This tests the path where length > 1, contains only digits/signs, and successfully parses as int.
        Character result = converter.convert(multiCharNumericStringInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Character convertNonNumericString(Blackhole bh) {
        // This tests the path where length > 1, but does NOT contain only digits/signs, leading to TypeConversionException.
        // Since we are benchmarking the successful path timing, we must handle the exception or ensure the input is valid for the benchmark.
        // However, since the method throws, we must wrap the call in a try-catch block to prevent JMH from failing the benchmark run,
        // while still ensuring the call happens exactly once.
        try {
            Character result = converter.convert(multiCharNonNumericStringInput);
            bh.consume(result);
        } catch (Exception e) {
            // Expected exception path, consume the exception object if necessary, but usually just letting it pass is fine
            // as long as the call itself is measured.
            bh.consume(e);
        }
        return null; // Return null to satisfy the compiler
    }

    @Benchmark
    public Character convertNull(Blackhole bh) {
        Character result = converter.convert(null);
        bh.consume(result);
        return result;
    }
}
