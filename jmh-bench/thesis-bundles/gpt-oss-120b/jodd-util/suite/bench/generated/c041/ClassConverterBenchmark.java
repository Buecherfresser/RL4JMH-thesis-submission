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
    private Object nullInput;
    private Object classInput;
    private Object stringInput;
    private Object stringClassSuffixInput;

    @Setup(Level.Trial)
    public void setup() {
        converter = new ClassConverter();
        nullInput = null;
        classInput = String.class;
        stringInput = "java.lang.String";
        stringClassSuffixInput = "java.lang.String.class";
    }

    @Benchmark
    public Class convertNull() {
        return converter.convert(nullInput);
    }

    @Benchmark
    public Class convertClassObject() {
        return converter.convert(classInput);
    }

    @Benchmark
    public Class convertString() {
        return converter.convert(stringInput);
    }

    @Benchmark
    public Class convertStringWithSuffix() {
        return converter.convert(stringClassSuffixInput);
    }
}
