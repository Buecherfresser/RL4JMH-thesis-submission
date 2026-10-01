package bench.generated.c042;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import jodd.typeconverter.impl.CollectionConverter;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CollectionConverterBenchmark {

    private CollectionConverter<Integer> listIntConverter;
    private CollectionConverter<Integer> setIntConverter;
    private CollectionConverter<Integer> arrayListIntConverter;
    private CollectionConverter<Integer> hashSetIntConverter;
    private CollectionConverter<String> listStringConverter;
    private CollectionConverter<Long> listLongConverter;

    private List<Integer> integerList;
    private List<String> stringList;
    private String csvInteger;
    private Integer[] integerArray;
    private String[] stringArray;
    private int[] intArray;
    private long[] longArray;
    private Iterable<Integer> iterableInteger;
    private Integer singleInteger;
    private String singleString;

    @Setup(Level.Trial)
    public void setup() {
        listIntConverter = new CollectionConverter<>(List.class, Integer.class);
        setIntConverter = new CollectionConverter<>(Set.class, Integer.class);
        arrayListIntConverter = new CollectionConverter<>(ArrayList.class, Integer.class);
        hashSetIntConverter = new CollectionConverter<>(HashSet.class, Integer.class);
        listStringConverter = new CollectionConverter<>(List.class, String.class);
        listLongConverter = new CollectionConverter<>(List.class, Long.class);

        int size = 100;

        integerList = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            integerList.add(i);
        }

        stringList = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            stringList.add("item" + i);
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            if (i > 0) sb.append(',');
            sb.append(i);
        }
        csvInteger = sb.toString();

        integerArray = new Integer[size];
        for (int i = 0; i < size; i++) {
            integerArray[i] = i;
        }

        stringArray = new String[size];
        for (int i = 0; i < size; i++) {
            stringArray[i] = "item" + i;
        }

        intArray = new int[size];
        for (int i = 0; i < size; i++) {
            intArray[i] = i;
        }

        longArray = new long[size];
        for (int i = 0; i < size; i++) {
            longArray[i] = i;
        }

        // An Iterable that returns a fresh Iterator over integerList each time
        iterableInteger = new Iterable<Integer>() {
            @Override
            public Iterator<Integer> iterator() {
                return new Iterator<Integer>() {
                    private int index = 0;
                    public boolean hasNext() { return index < integerList.size(); }
                    public Integer next() { return integerList.get(index++); }
                };
            }
        };

        singleInteger = 42;
        singleString = "hello";
    }

    // --- Null input ---
    @Benchmark
    public Collection<Integer> convertNull() {
        return listIntConverter.convert(null);
    }

    // --- Collection input ---
    @Benchmark
    public Collection<Integer> convertListToListInt() {
        return listIntConverter.convert(integerList);
    }

    @Benchmark
    public Collection<Integer> convertListToSetInt() {
        return setIntConverter.convert(integerList);
    }

    @Benchmark
    public Collection<Integer> convertListToArrayListInt() {
        return arrayListIntConverter.convert(integerList);
    }

    @Benchmark
    public Collection<Integer> convertListToHashSetInt() {
        return hashSetIntConverter.convert(integerList);
    }

    // --- CharSequence (CSV) input ---
    @Benchmark
    public Collection<Integer> convertCsvToListInt() {
        return listIntConverter.convert(csvInteger);
    }

    @Benchmark
    public Collection<String> convertCsvToStringList() {
        return listStringConverter.convert(csvInteger);
    }

    // --- Object array input ---
    @Benchmark
    public Collection<Integer> convertIntegerArrayToListInt() {
        return listIntConverter.convert(integerArray);
    }

    @Benchmark
    public Collection<String> convertStringArrayToStringList() {
        return listStringConverter.convert(stringArray);
    }

    // --- Primitive array input ---
    @Benchmark
    public Collection<Integer> convertIntArrayToListInt() {
        return listIntConverter.convert(intArray);
    }

    @Benchmark
    public Collection<Long> convertLongArrayToListLong() {
        return listLongConverter.convert(longArray);
    }

    // --- Iterable input ---
    @Benchmark
    public Collection<Integer> convertIterableToListInt() {
        return listIntConverter.convert(iterableInteger);
    }

    // --- Single element input ---
    @Benchmark
    public Collection<Integer> convertSingleIntegerToListInt() {
        return listIntConverter.convert(singleInteger);
    }

    @Benchmark
    public Collection<String> convertSingleStringToStringList() {
        return listStringConverter.convert(singleString);
    }
}
