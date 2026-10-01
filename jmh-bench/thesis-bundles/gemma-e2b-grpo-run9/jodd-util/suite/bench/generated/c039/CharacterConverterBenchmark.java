package bench.generated.c039;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.typeconverter.impl.CharacterConverter;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CharacterConverterBenchmark {

    private CharacterConverter converter;

    @Setup
    public void setup() {
        // Initialize the converter instance.
        this.converter = new CharacterConverter();
    }

    @Benchmark
    public void convertNull(Blackhole bh) {
        // Test case 1: null input. Should return null.
        try {
            converter.convert(null);
        } catch (Exception e) {
            // Ignore exceptions for timing benchmark
        }
    }

    @Benchmark
    public Character convertCharacter(Blackhole bh) {
        // Test case 2: already a Character.
        return converter.convert((Character) 'A');
    }

    @Benchmark
    public Character convertInteger(Blackhole bh) {
        // Test case 3: Number type (Integer).
        // Note: This might throw Character.valueOf(char) exception if the int value is out of char range,
        // but we test the path execution time.
        try {
            return converter.convert(12345);
        } catch (Exception e) {
            // Ignore exceptions
            return null;
        }
    }

    @Benchmark
    public Character convertDouble(Blackhole bh) {
        // Test case 4: Number type (Double). Should convert to int first.
        try {
            return converter.convert(3.14159);
        } catch (Exception e) {
            // Ignore exceptions
            return null;
        }
    }

    @Benchmark
    public Character convertSingleCharString(Blackhole bh) {
        // Test case 5: String of length 1.
        return converter.convert("Z");
    }

    @Benchmark
    public Character convertMultiCharNumericString(Blackhole bh) {
        // Test case 6: String of length > 1, numeric ("987").
        // This tests the path involving Integer.parseInt.
        try {
            return converter.convert("987");
        } catch (Exception e) {
            // Ignore exceptions
            return null;
        }
    }

    @Benchmark
    public void convertEmptyString(Blackhole bh) {
        // Test case 7: Empty string (""). Expected to throw TypeConversionException
        // based on the logic in CharacterConverter.
        try {
            converter.convert("");
        } catch (Exception e) {
            // Expected behavior if StringUtil.containsOnlyDigitsAndSigns fails on empty string
        }
    }
}
