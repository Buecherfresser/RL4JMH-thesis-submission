package bench.generated.c041;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.typeconverter.impl.ClassConverter;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ClassConverterBenchmark {

    private ClassConverter converter;
    private String validClassNameString;

    @Setup(Level.Trial)
    public void setup() {
        converter = new ClassConverter();
        // Use a simple, guaranteed class name available on the classpath
        validClassNameString = "java.lang.String";
    }

    @Benchmark
    public void benchmarkConvertValidClass(Blackhole bh) {
        // The input object must be an Object, so we pass the String.
        // The converter will call toString() on this String, which returns itself.
        Object input = validClassNameString;
        Class result = converter.convert(input);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkConvertNull(Blackhole bh) {
        Object input = null;
        Class result = converter.convert(input);
        // Must consume the result even if it is null
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkConvertAlreadyClass(Blackhole bh) {
        // Input is already a Class object
        Object input = String.class;
        Class result = converter.convert(input);
        // Must consume the result
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkConvertInvalidClass() {
        // Use a class name that definitely does not exist
        String invalidClassName = "com.nonexistent.DefinitelyNotHereClass12345";
        Object input = invalidClassName;
        
        // This call is expected to throw TypeConversionException (wrapping ClassNotFoundException)
        // We must handle the exception to prevent JMH from failing the benchmark run.
        try {
            converter.convert(input);
        } catch (Exception e) {
            // Expected behavior for invalid class name
        }
    }
}
