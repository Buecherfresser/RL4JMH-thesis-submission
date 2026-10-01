package bench.generated.c040;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.typeconverter.impl.ClassArrayConverter;
import jodd.typeconverter.TypeConverterManager;
import java.util.List;
import java.util.ArrayList;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ClassArrayConverterBenchmark {

    private ClassArrayConverter converter;
    private Object nullInput;
    private Object singleClassInput;
    private Object csvStringInput;
    private Object listInput;

    @Setup(Level.Trial)
    public void setup() {
        TypeConverterManager manager = TypeConverterManager.get();
        converter = new ClassArrayConverter(manager);

        nullInput = null;

        singleClassInput = String.class; // a single Class instance

        // CSV string with delimiters, blank lines, comments, and spaces
        csvStringInput = "java.lang.String, java.util.List ;\n"
                       + "# this is a comment line\n"
                       + "java.util.Map\n"
                       + "   \n"
                       + "java.lang.Integer";

        // List of class name strings
        List<String> classNames = new ArrayList<>();
        classNames.add("java.lang.String");
        classNames.add("java.util.List");
        classNames.add("java.util.Map");
        classNames.add("java.lang.Integer");
        listInput = classNames;
    }

    @Benchmark
    public Class[] convertFromNull() {
        return converter.convert(nullInput);
    }

    @Benchmark
    public Class[] convertFromSingleClass() {
        return converter.convert(singleClassInput);
    }

    @Benchmark
    public Class[] convertFromCsvString() {
        return converter.convert(csvStringInput);
    }

    @Benchmark
    public Class[] convertFromList() {
        return converter.convert(listInput);
    }
}
