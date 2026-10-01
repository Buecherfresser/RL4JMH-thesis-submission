package bench.generated.c040;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.lang.reflect.Method;
import jodd.typeconverter.impl.ClassArrayConverter;
import jodd.typeconverter.TypeConverterManager;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ClassArrayConverterBenchmark {

    private ClassArrayConverter converter;
    private TypeConverterManager typeConverterManager;

    // Test inputs for convertStringToArray
    private String simpleCsvInput;
    private String complexDelimitedInput;
    private String inputWithCommentsAndBlanks;
    private String emptyInput;

    // Reflection setup for protected method
    private Method convertStringToArrayMethod;

    @Setup(Level.Trial)
    public void setup() throws NoSuchMethodException {
        // Initialize dependencies
        // Assuming TypeConverterManager can be instantiated or obtained simply
        this.typeConverterManager = new TypeConverterManager();
        this.converter = new ClassArrayConverter(this.typeConverterManager);

        // Initialize inputs
        simpleCsvInput = "java.lang.String,java.lang.Integer,java.lang.Boolean";
        complexDelimitedInput = "java.lang.String;java.lang.Integer\njava.lang.Boolean";
        inputWithCommentsAndBlanks = "# This is a comment\n\njava.lang.String, # Another comment\njava.lang.Integer\n";
        emptyInput = "";

        // Setup reflection access to the protected method
        convertStringToArrayMethod = ClassArrayConverter.class.getDeclaredMethod(
                "convertStringToArray", String.class
        );
        convertStringToArrayMethod.setAccessible(true);
    }

    @Benchmark
    public void benchmarkSimpleCsvConversion(Blackhole bh) throws Exception {
        String[] result = (String[]) convertStringToArrayMethod.invoke(converter, simpleCsvInput);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkComplexDelimitedConversion(Blackhole bh) throws Exception {
        String[] result = (String[]) convertStringToArrayMethod.invoke(converter, complexDelimitedInput);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkInputWithCommentsAndBlanks(Blackhole bh) throws Exception {
        String[] result = (String[]) convertStringToArrayMethod.invoke(converter, inputWithCommentsAndBlanks);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkEmptyInput(Blackhole bh) throws Exception {
        String[] result = (String[]) convertStringToArrayMethod.invoke(converter, emptyInput);
        bh.consume(result);
    }
}
