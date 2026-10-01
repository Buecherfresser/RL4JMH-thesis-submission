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

    @Setup
    public void setup() {
        // Initialize the converter instance. Since it's stateless, this is safe.
        this.converter = new LocaleConverter();
    }

    @Benchmark
    public void testConvertNull(Blackhole bh) {
        // Test case 1: Null input
        Locale result = converter.convert(null);
        bh.consume(result);
    }

    @Benchmark
    public void testConvertLocale(Blackhole bh) {
        // Test case 2: Input is already a Locale
        Locale inputLocale = Locale.US;
        Locale result = converter.convert(inputLocale);
        bh.consume(result);
    }

    @Benchmark
    public void testConvertString(Blackhole bh) {
        // Test case 3: Input is a String (requires toString() and Locale.forLanguageTag())
        String inputString = "en_US";
        Locale result = converter.convert(inputString);
        bh.consume(result);
    }

    @Benchmark
    public void testConvertInteger(Blackhole bh) {
        // Test case 4: Input is a primitive wrapper (Integer)
        Integer inputInt = 12345;
        Locale result = converter.convert(inputInt);
        bh.consume(result);
    }
}
