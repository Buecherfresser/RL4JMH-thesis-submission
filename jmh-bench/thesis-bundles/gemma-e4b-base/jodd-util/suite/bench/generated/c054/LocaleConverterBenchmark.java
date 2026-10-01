package bench.generated.c054;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.typeconverter.impl.LocaleConverter;
import java.util.Locale;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class LocaleConverterBenchmark {

    private LocaleConverter converter;

    // Inputs for testing
    private Locale localeInput;
    private String stringInput;
    private Object nonLocaleObjectInput;

    @Setup(Level.Trial)
    public void setup() {
        converter = new LocaleConverter();
        
        // 1. Input is already a Locale
        localeInput = Locale.forLanguageTag("en-US");
        
        // 2. Input is a String (language tag)
        stringInput = "fr-CA";
        
        // 3. Input is another type (e.g., Integer, which will be converted to String first)
        nonLocaleObjectInput = 12345;
    }

    @Benchmark
    public Locale benchmarkConvertLocaleInput(Blackhole bh) {
        Locale result = converter.convert(localeInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Locale benchmarkConvertStringInput(Blackhole bh) {
        Locale result = converter.convert(stringInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Locale benchmarkConvertOtherObjectInput(Blackhole bh) {
        // This tests the path where the object is converted to String first (e.g., Integer -> "12345")
        Locale result = converter.convert(nonLocaleObjectInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Locale benchmarkConvertNullInput(Blackhole bh) {
        // Test the null handling path
        Locale result = converter.convert(null);
        bh.consume(result);
        return result;
    }
}
