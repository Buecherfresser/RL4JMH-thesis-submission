package bench.generated.c030;

import jodd.typeconverter.TypeConverterManager;
import jodd.typeconverter.impl.ArrayConverter;
import org.openjdk.jmh.annotations.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ArrayConverterBenchmark {

    private ArrayConverter<Integer> intConverter;
    private ArrayConverter<String> stringConverter;
    private ArrayConverter<Long> longConverter;
    private ArrayConverter<Boolean> booleanConverter;
    private ArrayConverter<Character> charConverter;
    private ArrayConverter<Byte> byteConverter;
    private ArrayConverter<Short> shortConverter;
    private ArrayConverter<Float> floatConverter;
    private ArrayConverter<Double> doubleConverter;

    private int[] intArray;
    private long[] longArray;
    private float[] floatArray;
    private double[] doubleArray;
    private short[] shortArray;
    private byte[] byteArray;
    private char[] charArray;
    private boolean[] booleanArray;
    private String[] stringArray;
    private Integer[] integerArray;
    private List<Integer> intList;
    private Set<Integer> intSet;
    private String csvString;
    private Integer singleInt;
    private String singleString;

    @Setup(Level.Trial)
    public void setup() {
        TypeConverterManager manager = TypeConverterManager.get();
        intConverter = new ArrayConverter<>(manager, Integer.class);
        stringConverter = new ArrayConverter<>(manager, String.class);
        longConverter = new ArrayConverter<>(manager, Long.class);
        booleanConverter = new ArrayConverter<>(manager, Boolean.class);
        charConverter = new ArrayConverter<>(manager, Character.class);
        byteConverter = new ArrayConverter<>(manager, Byte.class);
        shortConverter = new ArrayConverter<>(manager, Short.class);
        floatConverter = new ArrayConverter<>(manager, Float.class);
        doubleConverter = new ArrayConverter<>(manager, Double.class);

        intArray = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        longArray = new long[]{1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L};
        floatArray = new float[]{1.0f, 2.0f, 3.0f, 4.0f, 5.0f, 6.0f, 7.0f, 8.0f, 9.0f, 10.0f};
        doubleArray = new double[]{1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0};
        shortArray = new short[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        byteArray = new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        charArray = new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j'};
        booleanArray = new boolean[]{true, false, true, false, true, false, true, false, true, false};

        stringArray = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
        integerArray = new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};

        intList = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            intList.add(i);
        }

        intSet = new HashSet<>();
        for (int i = 1; i <= 10; i++) {
            intSet.add(i);
        }

        csvString = "1,2,3,4,5,6,7,8,9,10";
        singleInt = 42;
        singleString = "hello";
    }

    @Benchmark
    public Integer[] convertIntArrayToIntegerArray() {
        return intConverter.convert(intArray);
    }

    @Benchmark
    public Long[] convertLongArrayToLongArray() {
        return longConverter.convert(longArray);
    }

    @Benchmark
    public Float[] convertFloatArrayToFloatArray() {
        return floatConverter.convert(floatArray);
    }

    @Benchmark
    public Double[] convertDoubleArrayToDoubleArray() {
        return doubleConverter.convert(doubleArray);
    }

    @Benchmark
    public Short[] convertShortArrayToShortArray() {
        return shortConverter.convert(shortArray);
    }

    @Benchmark
    public Byte[] convertByteArrayToByteArray() {
        return byteConverter.convert(byteArray);
    }

    @Benchmark
    public Character[] convertCharArrayToCharacterArray() {
        return charConverter.convert(charArray);
    }

    @Benchmark
    public Boolean[] convertBooleanArrayToBooleanArray() {
        return booleanConverter.convert(booleanArray);
    }

    @Benchmark
    public Integer[] convertStringArrayToIntegerArray() {
        return intConverter.convert(stringArray);
    }

    @Benchmark
    public Integer[] convertIntegerArrayToIntegerArray() {
        return intConverter.convert(integerArray);
    }

    @Benchmark
    public Integer[] convertListToIntegerArray() {
        return intConverter.convert(intList);
    }

    @Benchmark
    public Integer[] convertSetToIntegerArray() {
        return intConverter.convert(intSet);
    }

    @Benchmark
    public Integer[] convertCsvToIntegerArray() {
        return intConverter.convert(csvString);
    }

    @Benchmark
    public Integer[] convertSingleIntToIntegerArray() {
        return intConverter.convert(singleInt);
    }

    @Benchmark
    public String[] convertSingleStringToStringArray() {
        return stringConverter.convert(singleString);
    }
}
