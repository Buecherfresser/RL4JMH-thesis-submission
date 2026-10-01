package bench.generated.c041;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

import jodd.typeconverter.impl.ClassConverter;
import jodd.typeconverter.TypeConverter;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ClassConverterBenchmark {

    private ClassConverter converter;

    @Setup
    public void setup() {
        // Initialize the converter instance. Since ClassConverter is stateless,
        // this setup is safe and fast.
        this.converter = new ClassConverter();
    }

    @Benchmark
    public void convertNull(Blackhole bh) {
        try {
            // Test case 1: Null input
            Object result = converter.convert(null);
            bh.consume(result);
        } catch (Exception e) {
            // Ignore exceptions for timing purposes if they are expected paths
        }
    }

    @Benchmark
    public void convertClassObject(Blackhole bh) {
        try {
            // Test case 2: Input is already a Class object
            Class result = converter.convert(Class.class);
            bh.consume(result);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void convertStringRepresentation(Blackhole bh) {
        try {
            // Test case 3: Input is a String representing a class name
            // This tests StringUtil.substring and ClassLoaderUtil.loadClass
            Object result = converter.convert("java.lang.String");
            bh.consume(result);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void convertStringWithSuffix(Blackhole bh) {
        try {
            // Test case 4: Input string with ".class" suffix
            // This tests StringUtil.substring logic
            Object result = converter.convert("com.example.MyClass.class");
            bh.consume(result);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }
}
