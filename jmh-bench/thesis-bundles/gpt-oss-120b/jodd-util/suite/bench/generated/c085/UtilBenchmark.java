package bench.generated.c085;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.util.Util;
import jodd.util.ArraysUtil;
import jodd.util.StringPool;
import java.util.*;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class UtilBenchmark {

    // Simple objects
    private String sampleString;
    private List<String> sampleList;
    private Map<String, String> sampleMap;
    private int[] intArray;
    private long[] longArray;
    private double[] doubleArray;
    private float[] floatArray;
    private boolean[] booleanArray;
    private short[] shortArray;
    private byte[] byteArray;
    private Object[] objectArray;

    @Setup(Level.Trial)
    public void setup() {
        sampleString = "The quick brown fox jumps over the lazy dog";

        sampleList = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            sampleList.add("elem" + i);
        }

        sampleMap = new HashMap<>();
        for (int i = 0; i < 100; i++) {
            sampleMap.put("key" + i, "value" + i);
        }

        intArray = new int[100];
        longArray = new long[100];
        doubleArray = new double[100];
        floatArray = new float[100];
        booleanArray = new boolean[100];
        shortArray = new short[100];
        byteArray = new byte[100];
        for (int i = 0; i < 100; i++) {
            intArray[i] = i;
            longArray[i] = i;
            doubleArray[i] = i + 0.5;
            floatArray[i] = i + 0.5f;
            booleanArray[i] = (i % 2) == 0;
            shortArray[i] = (short) i;
            byteArray[i] = (byte) i;
        }

        objectArray = new Object[100];
        for (int i = 0; i < 100; i++) {
            objectArray[i] = "obj" + i;
        }
    }

    // -------------------- toString --------------------

    @Benchmark
    public String benchmarkToStringNonNull() {
        return Util.toString(sampleString);
    }

    @Benchmark
    public String benchmarkToStringNull() {
        return Util.toString(null);
    }

    // -------------------- length --------------------

    @Benchmark
    public int benchmarkLengthString() {
        return Util.length(sampleString);
    }

    @Benchmark
    public int benchmarkLengthCollection() {
        return Util.length(sampleList);
    }

    @Benchmark
    public int benchmarkLengthMap() {
        return Util.length(sampleMap);
    }

    @Benchmark
    public int benchmarkLengthIterator() {
        Iterator<String> it = sampleList.iterator();
        return Util.length(it);
    }

    @Benchmark
    public int benchmarkLengthEnumeration() {
        Enumeration<String> en = Collections.enumeration(sampleList);
        return Util.length(en);
    }

    @Benchmark
    public int benchmarkLengthIntArray() {
        return Util.length(intArray);
    }

    @Benchmark
    public int benchmarkLengthObjectArray() {
        return Util.length(objectArray);
    }

    // -------------------- containsElement --------------------

    @Benchmark
    public boolean benchmarkContainsString() {
        return Util.containsElement(sampleString, "fox");
    }

    @Benchmark
    public boolean benchmarkContainsCollection() {
        return Util.containsElement(sampleList, "elem42");
    }

    @Benchmark
    public boolean benchmarkContainsMap() {
        return Util.containsElement(sampleMap, "value42");
    }

    @Benchmark
    public boolean benchmarkContainsIterator() {
        return Util.containsElement(sampleList.iterator(), "elem42");
    }

    @Benchmark
    public boolean benchmarkContainsEnumeration() {
        return Util.containsElement(Collections.enumeration(sampleList), "elem42");
    }

    @Benchmark
    public boolean benchmarkContainsIntArray() {
        return Util.containsElement(intArray, 42);
    }

    @Benchmark
    public boolean benchmarkContainsObjectArray() {
        return Util.containsElement(objectArray, "obj42");
    }

    // -------------------- toPrettyString --------------------

    @Benchmark
    public String benchmarkToPrettyStringNull() {
        return Util.toPrettyString(null);
    }

    @Benchmark
    public String benchmarkToPrettyStringPrimitiveArray() {
        return Util.toPrettyString(intArray);
    }

    @Benchmark
    public String benchmarkToPrettyStringObjectArray() {
        return Util.toPrettyString(objectArray);
    }

    @Benchmark
    public String benchmarkToPrettyStringIterable() {
        return Util.toPrettyString(sampleList);
    }

    @Benchmark
    public String benchmarkToPrettyStringSimpleObject() {
        return Util.toPrettyString(12345);
    }
}
