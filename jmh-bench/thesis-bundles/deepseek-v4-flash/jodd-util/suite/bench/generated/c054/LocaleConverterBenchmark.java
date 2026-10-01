package bench.generated.c054;

import org.openjdk.jmh.annotations.*;
import jodd.typeconverter.impl.LocaleConverter;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class LocaleConverterBenchmark {

    private LocaleConverter converter;
    private Locale locale;
    private String languageTag;
    private Object customObject;

    @Setup(Level.Trial)
    public void setup() {
        converter = new LocaleConverter();
        locale = Locale.US;
        languageTag = "en-US";
        customObject = new Object() {
            @Override
            public String toString() {
                return "fr-FR";
            }
        };
    }

    @Benchmark
    public Locale convertNull() {
        return converter.convert(null);
    }

    @Benchmark
    public Locale convertLocale() {
        return converter.convert(locale);
    }

    @Benchmark
    public Locale convertString() {
        return converter.convert(languageTag);
    }

    @Benchmark
    public Locale convertCustomObject() {
        return converter.convert(customObject);
    }
}
