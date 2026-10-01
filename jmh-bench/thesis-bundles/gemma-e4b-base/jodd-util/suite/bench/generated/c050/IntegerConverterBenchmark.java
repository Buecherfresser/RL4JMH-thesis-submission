package bench.generated.c050;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.typeconverter.impl.IntegerConverter;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class IntegerConverterBenchmark {

    private IntegerConverter converter;

    // Test inputs
    private Object nullInput;
    private Object integerInput;
    private Object longInput;
    private Object doubleInput;
    private Object booleanTrueInput;
    private Object booleanFalseInput;
    private Object stringStandardInput;
    private Object stringPositiveInput;
    private Object stringNegativeInput;
    private Object stringWhitespaceInput;
    private Object stringInvalidInput;

    @Setup(Level.Trial)
    public void setup() {
        converter = new IntegerConverter();

        // 1. Null
        nullInput = null;

        // 2. Integer
        integerInput = 42;

        // 3. Long
        longInput = 987654321L;

        // 4. Double
        doubleInput = 123.45;

        // 5. Boolean
        booleanTrueInput = true;
        booleanFalseInput = false;

        // 6. String inputs
        stringStandardInput = "12345";
        stringPositiveInput = "+54321";
        stringNegativeInput = "-999";
        stringWhitespaceInput = "  -100  ";
        stringInvalidInput = "not_a_number";
    }

    @Benchmark
    public void convert_nullInput(Blackhole bh) {
        Integer result = converter.convert((Object) nullInput);
        bh.consume(result);
    }

    @Benchmark
    public void convert_integerInput(Blackhole bh) {
        Integer result = converter.convert((Object) integerInput);
        bh.consume(result);
    }

    @Benchmark
    public void convert_longInput(Blackhole bh) {
        Integer result = converter.convert((Object) longInput);
        bh.consume(result);
    }

    @Benchmark
    public void convert_doubleInput(Blackhole bh) {
        Integer result = converter.convert((Object) doubleInput);
        bh.consume(result);
    }

    @Benchmark
    public void convert_booleanTrueInput(Blackhole bh) {
        Integer result = converter.convert((Object) booleanTrueInput);
        bh.consume(result);
    }

    @Benchmark
    public void convert_booleanFalseInput(Blackhole bh) {
        Integer result = converter.convert((Object) booleanFalseInput);
        bh.consume(result);
    }

    @Benchmark
    public void convert_stringStandardInput(Blackhole bh) {
        Integer result = converter.convert((Object) stringStandardInput);
        bh.consume(result);
    }

    @Benchmark
    public void convert_stringPositiveInput(Blackhole bh) {
        Integer result = converter.convert((Object) stringPositiveInput);
        bh.consume(result);
    }

    @Benchmark
    public void convert_stringNegativeInput(Blackhole bh) {
        Integer result = converter.convert((Object) stringNegativeInput);
        bh.consume(result);
    }

    @Benchmark
    public void convert_stringWhitespaceInput(Blackhole bh) {
        Integer result = converter.convert((Object) stringWhitespaceInput);
        bh.consume(result);
    }

    @Benchmark
    public void convert_stringInvalidInput(Blackhole bh) {
        // This test case expects an exception, but JMH benchmarks typically measure successful paths.
        // To handle exceptions without crashing the benchmark, we wrap the call in a try-catch
        // and consume the result (which is null/void in this case, but we must consume something).
        // Since the method throws TypeConversionException, we must ensure the benchmark runs without failure.
        try {
            converter.convert((Object) stringInvalidInput);
        } catch (jodd.typeconverter.TypeConversionException e) {
            // Expected behavior, consume a dummy value to satisfy JMH rules
            bh.consume(e);
        }
    }
}
