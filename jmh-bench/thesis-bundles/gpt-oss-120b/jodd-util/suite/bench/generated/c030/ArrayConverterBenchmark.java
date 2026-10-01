package bench.generated.c030;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.typeconverter.impl.ArrayConverter;
import jodd.typeconverter.TypeConverterManager;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Collection;
import java.util.Iterator;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ArrayConverterBenchmark {

    private ArrayConverter<Integer> converter;
    private List<Integer> integerList;
    private Set<Integer> integerSet;
    private Iterable<Integer> integerIterable;
    private String csvString;
    private int[] intArray;
    private Integer[] integerArray;

    @Setup(Level.Trial)
    public void setup() {
        TypeConverterManager manager = TypeConverterManager.get();
        converter = new ArrayConverter<>(manager, Integer.class);

        integerList = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            integerList.add(i);
        }

        integerSet = new HashSet<>();
        for (int i = 0; i < 100; i++) {
            integerSet.add(i);
        }

        // Iterable can be the same list (List implements Iterable)
        integerIterable = integerList;

        // CSV string with numbers separated by commas
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            if (i > 0) {
                sb.append(',');
            }
            sb.append(i);
        }
        csvString = sb.toString();

        intArray = new int[100];
        for (int i = 0; i < 100; i++) {
            intArray[i] = i;
        }

        integerArray = new Integer[100];
        for (int i = 0; i < 100; i++) {
            integerArray[i] = i;
        }
    }

    @Benchmark
    public Integer[] benchmarkConvertCollection() {
        return converter.convert(integerList);
    }

    @Benchmark
    public Integer[] benchmarkConvertSet() {
        return converter.convert(integerSet);
    }

    @Benchmark
    public Integer[] benchmarkConvertIterable() {
        return converter.convert(integerIterable);
    }

    @Benchmark
    public Integer[] benchmarkConvertString() {
        return converter.convert(csvString);
    }

    @Benchmark
    public Integer[] benchmarkConvertPrimitiveIntArray() {
        return converter.convert(intArray);
    }

    @Benchmark
    public Integer[] benchmarkConvertObjectArray() {
        return converter.convert(integerArray);
    }

    @Benchmark
    public Integer[] benchmarkConvertNull() {
        return converter.convert(null);
    }
}
