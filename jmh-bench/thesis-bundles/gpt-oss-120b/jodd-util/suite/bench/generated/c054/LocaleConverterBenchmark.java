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
    private Locale localeInput;
    private String languageTagInput;
    private Object customObjectInput;

    @Setup(Level.Trial)
    public void setUp() {
        converter = new LocaleConverter();
        localeInput = Locale.US; // e.g., en_US
        languageTagInput = "fr-FR";
        customObjectInput = new Object() {
            @Override
            public String toString() {
                return "de-DE";
            }
        };
    }

    @Benchmark
    public Locale convertFromLocale() {
        return converter.convert(localeInput);
    }

    @Benchmark
    public Locale convertFromString() {
        return converter.convert(languageTagInput);
    }

    @Benchmark
    public Locale convertFromCustomObject() {
        return converter.convert(customObjectInput);
    }

    @Benchmark
    public Locale convertFromNull() {
        return converter.convert(null);
    }
}
