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

    // Inputs for testing different branches of the convert method
    private Object nullInput;
    private Locale localeInput;
    private String stringInput;
    private Integer integerInput;
    private Object customObjectInput;

    @Setup
    public void setup() {
        converter = new LocaleConverter();

        // Case 1: Null input
        nullInput = null;

        // Case 2: Input is already a Locale
        localeInput = Locale.US;

        // Case 3: Input is a standard String
        stringInput = "fr_CA";

        // Case 4: Input is a primitive wrapper (Integer)
        integerInput = 12345;

        // Case 5: Input is a custom object (e.g., a String representation of an object)
        customObjectInput = "Some arbitrary string value";
    }

    @Benchmark
    public Locale convert_Null(Blackhole bh) {
        return converter.convert(nullInput);
    }

    @Benchmark
    public Locale convert_Locale(Blackhole bh) {
        return converter.convert(localeInput);
    }

    @Benchmark
    public Locale convert_String(Blackhole bh) {
        return converter.convert(stringInput);
    }

    @Benchmark
    public Locale convert_Integer(Blackhole bh) {
        return converter.convert(integerInput);
    }

    @Benchmark
    public Locale convert_CustomObject(Blackhole bh) {
        return converter.convert(customObjectInput);
    }
}
