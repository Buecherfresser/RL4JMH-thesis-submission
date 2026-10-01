package bench.generated.c041;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

import jodd.typeconverter.impl.ClassConverter;
import jodd.util.ClassLoaderUtil;
import jodd.typeconverter.TypeConversionException;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ClassConverterBenchmark {

    // State fields for inputs
    private Object nullValue;
    private Class<?> existingClass;
    private String standardClassName;
    private String classWithExtension;

    // The subject under test instance
    private ClassConverter converter;

    @Setup
    public void setup() {
        // 1. Setup null input
        this.nullValue = null;

        // 2. Setup existing Class object input
        try {
            // Load a standard Java class for testing
            this.existingClass = Class.forName("java.lang.String");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to load required class for setup", e);
        }

        // 3. Setup standard class name string input
        this.standardClassName = "java.util.ArrayList";

        // 4. Setup class name string ending in .class input
        this.classWithExtension = "com.example.MyTestClass.class";

        // Initialize the converter
        this.converter = new ClassConverter();
    }

    @Benchmark
    public void testNullInput(Blackhole bh) {
        Class<?> result = converter.convert(nullValue);
        bh.consume(result);
    }

    @Benchmark
    public void testExistingClassInput(Blackhole bh) {
        Class<?> result = converter.convert(existingClass);
        bh.consume(result);
    }

    @Benchmark
    public void testStandardClassNameInput(Blackhole bh) {
        Class<?> result = converter.convert(standardClassName);
        bh.consume(result);
    }

    @Benchmark
    public void testClassNameWithExtensionInput(Blackhole bh) {
        Class<?> result = converter.convert(classWithExtension);
        bh.consume(result);
    }
}
