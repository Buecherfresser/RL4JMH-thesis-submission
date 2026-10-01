package bench.generated.c054;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import jodd.typeconverter.impl.LocaleConverter;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class LocaleConverterBenchmark {

    private LocaleConverter converter;

    // State for testing the null case
    private Object nullInput;

    // State for testing the existing Locale case
    private Locale localeInput;

    // State for testing the String input case
    private String stringInput;

    // State for testing a generic Object input case
    private Object complexObjectInput;

    @Setup
    public void setup() {
        converter = new LocaleConverter();

        // Input 1: Null
        nullInput = null;

        // Input 2: Existing Locale
        localeInput = Locale.US;

        // Input 3: String
        stringInput = "en_US";

        // Input 4: Complex Object (to test toString() path)
        complexObjectInput = new Object() {
            @Override
            public String toString() {
                return "ComplexObject{" + System.currentTimeMillis() + "}";
            }
        };
    }

    @Benchmark
    public void testNullInput(Blackhole bh) {
        Locale result = converter.convert(nullInput);
        bh.consume(result);
    }

    @Benchmark
    public void testExistingLocaleInput(Blackhole bh) {
        Locale result = converter.convert(localeInput);
        bh.consume(result);
    }

    @Benchmark
    public void testStringInput(Blackhole bh) {
        Locale result = converter.convert(stringInput);
        bh.consume(result);
    }

    @Benchmark
    public void testComplexObjectInput(Blackhole bh) {
        Locale result = converter.convert(complexObjectInput);
        bh.consume(result);
    }
}
