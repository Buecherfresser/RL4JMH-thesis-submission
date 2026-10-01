package bench.generated.c041;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.typeconverter.impl.ClassConverter;
import jodd.typeconverter.TypeConversionException;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ClassConverterBenchmark {

    private ClassConverter converter;
    private Class<?> classInstance;
    private String className;
    private String classNameWithSuffix;
    private String invalidClassName;

    @Setup(Level.Trial)
    public void setup() {
        converter = new ClassConverter();
        classInstance = String.class;
        className = "java.lang.String";
        classNameWithSuffix = "java.lang.String.class";
        invalidClassName = "no.such.Class";
    }

    @Benchmark
    public Class<?> convertNull() {
        return converter.convert(null);
    }

    @Benchmark
    public Class<?> convertClassInstance() {
        return converter.convert(classInstance);
    }

    @Benchmark
    public Class<?> convertStringClassName() {
        return converter.convert(className);
    }

    @Benchmark
    public Class<?> convertStringWithClassSuffix() {
        return converter.convert(classNameWithSuffix);
    }

    @Benchmark
    public void convertInvalidClassName(Blackhole bh) {
        try {
            converter.convert(invalidClassName);
        } catch (TypeConversionException e) {
            bh.consume(e);
        }
    }
}
