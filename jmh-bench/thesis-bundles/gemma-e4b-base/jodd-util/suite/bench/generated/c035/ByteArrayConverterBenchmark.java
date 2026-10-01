package bench.generated.c035;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import jodd.typeconverter.TypeConverterManager;
import jodd.typeconverter.impl.ByteArrayConverter;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ByteArrayConverterBenchmark {

    private ByteArrayConverter converter;
    private TypeConverterManager typeConverterManager;

    // Inputs for testing
    private Object scalarInput;
    private int[] primitiveArrayInput;
    private String[] objectArrayInput;
    private Collection<Integer> collectionInput;
    private List<Integer> iterableInput;
    private String charSequenceInput;

    @Setup(Level.Trial)
    public void setup() {
        // NOTE: Since the source for TypeConverterManager is not provided,
        // we assume a functional instance can be created for the benchmark environment.
        // In a real scenario, this would require a concrete implementation or mock.
        this.typeConverterManager = new TypeConverterManager();
        this.converter = new ByteArrayConverter(this.typeConverterManager);

        // 1. Scalar Input (e.g., Integer)
        this.scalarInput = 123;

        // 2. Primitive Array Input (int[])
        this.primitiveArrayInput = new int[]{1, 2, 3, 4, 5};

        // 3. Object Array Input (String[])
        this.objectArrayInput = new String[]{"a", "b", "c"};

        // 4. Collection Input (List<Integer>)
        this.collectionInput = List.of(1, 2, 3);

        // 5. Iterable Input (List<Integer>)
        this.iterableInput = new ArrayList<>(List.of(1, 2, 3));

        // 6. CharSequence Input (String)
        this.charSequenceInput = "12345";
    }

    @Benchmark
    public byte[] convert_null(Blackhole bh) {
        byte[] result = converter.convert(null);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public byte[] convert_scalar(Blackhole bh) {
        byte[] result = converter.convert(scalarInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public byte[] convert_primitive_array(Blackhole bh) {
        byte[] result = converter.convert(primitiveArrayInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public byte[] convert_object_array(Blackhole bh) {
        byte[] result = converter.convert(objectArrayInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public byte[] convert_collection(Blackhole bh) {
        byte[] result = converter.convert(collectionInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public byte[] convert_iterable(Blackhole bh) {
        byte[] result = converter.convert(iterableInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public byte[] convert_charsequence(Blackhole bh) {
        byte[] result = converter.convert(charSequenceInput);
        bh.consume(result);
        return result;
    }
}
