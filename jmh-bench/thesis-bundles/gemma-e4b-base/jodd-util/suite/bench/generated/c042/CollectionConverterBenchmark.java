package bench.generated.c042;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import jodd.typeconverter.impl.CollectionConverter;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CollectionConverterBenchmark {

    private CollectionConverter<Integer> converter;

    // Input data for collection conversion (e.g., List<String> -> List<Integer>)
    private List<String> stringCollectionInput;
    
    // Input data for array conversion (e.g., String[] -> List<Integer>)
    private String[] stringArrayInput;

    // Input data for primitive array conversion (e.g., byte[] -> List<Integer>)
    private byte[] bytePrimitiveArrayInput;

    // Input data for single value conversion
    private String singleStringInput;

    private static final int INPUT_SIZE = 100;

    @Setup(Level.Trial)
    public void setup() {
        // 1. Initialize the converter: Target Collection<Integer>
        // T = Integer, Collection<T> = List.class
        converter = new CollectionConverter<>(List.class, Integer.class);

        // 2. Setup Collection Input (List<String>)
        stringCollectionInput = new ArrayList<>(INPUT_SIZE);
        for (int i = 0; i < INPUT_SIZE; i++) {
            stringCollectionInput.add("Value" + i);
        }

        // 3. Setup Array Input (String[])
        stringArrayInput = new String[INPUT_SIZE];
        for (int i = 0; i < INPUT_SIZE; i++) {
            stringArrayInput[i] = "Value" + i;
        }

        // 4. Setup Primitive Array Input (byte[])
        bytePrimitiveArrayInput = new byte[INPUT_SIZE];
        for (int i = 0; i < INPUT_SIZE; i++) {
            bytePrimitiveArrayInput[i] = (byte) (i % 256);
        }

        // 5. Setup Single Value Input
        singleStringInput = "SingleValue";
    }

    @Benchmark
    public void convertCollectionInput(Blackhole bh) {
        // Test: Collection<String> -> Collection<Integer>
        bh.consume(converter.convert(stringCollectionInput));
    }

    @Benchmark
    public void convertObjectArrayInput(Blackhole bh) {
        // Test: Object[] (String[]) -> Collection<Integer>
        bh.consume(converter.convert(stringArrayInput));
    }

    @Benchmark
    public void convertPrimitiveArrayInput(Blackhole bh) {
        // Test: byte[] -> Collection<Integer>
        bh.consume(converter.convert(bytePrimitiveArrayInput));
    }

    @Benchmark
    public void convertSingleValueInput(Blackhole bh) {
        // Test: Single String -> Collection<Integer>
        bh.consume(converter.convert(singleStringInput));
    }
}
