package bench.generated.c068;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.util.ArraysUtil;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ArraysUtilBenchmark {

    // Primitive arrays
    private byte[] byteArrayA;
    private byte[] byteArrayB;
    private char[] charArrayA;
    private char[] charArrayB;
    private short[] shortArrayA;
    private short[] shortArrayB;
    private int[] intArrayA;
    private int[] intArrayB;
    private long[] longArrayA;
    private long[] longArrayB;
    private float[] floatArrayA;
    private float[] floatArrayB;
    private double[] doubleArrayA;
    private double[] doubleArrayB;
    private boolean[] booleanArrayA;
    private boolean[] booleanArrayB;

    // Object arrays
    private Integer[] integerArrayA;
    private Integer[] integerArrayB;
    private String[] stringArrayA;
    private String[] stringArrayB;
    private Object[] objectArray;

    // Sub‑array patterns
    private int[] subIntArray;
    private byte[] subByteArray;
    private char[] subCharArray;
    private short[] subShortArray;
    private long[] subLongArray;
    private float[] subFloatArray;
    private double[] subDoubleArray;
    private boolean[] subBooleanArray;

    // Wrapper arrays for values/valuesOf
    private Byte[] byteObjectArray;
    private Character[] charObjectArray;
    private Short[] shortObjectArray;
    private Integer[] intObjectArray;
    private Long[] longObjectArray;
    private Float[] floatObjectArray;
    private Double[] doubleObjectArray;
    private Boolean[] booleanObjectArray;

    @Setup
    public void setup() {
        int size = 1024;

        // Fill primitive arrays with deterministic data
        byteArrayA = new byte[size];
        byteArrayB = new byte[size];
        for (int i = 0; i < size; i++) {
            byteArrayA[i] = (byte) (i & 0xFF);
            byteArrayB[i] = (byte) ((i * 2) & 0xFF);
        }

        charArrayA = new char[size];
        charArrayB = new char[size];
        for (int i = 0; i < size; i++) {
            charArrayA[i] = (char) (i % 65535);
            charArrayB[i] = (char) ((i * 3) % 65535);
        }

        shortArrayA = new short[size];
        shortArrayB = new short[size];
        for (int i = 0; i < size; i++) {
            shortArrayA[i] = (short) i;
            shortArrayB[i] = (short) (i * 2);
        }

        intArrayA = new int[size];
        intArrayB = new int[size];
        for (int i = 0; i < size; i++) {
            intArrayA[i] = i;
            intArrayB[i] = i * 2;
        }

        longArrayA = new long[size];
        longArrayB = new long[size];
        for (int i = 0; i < size; i++) {
            longArrayA[i] = i;
            longArrayB[i] = i * 2L;
        }

        floatArrayA = new float[size];
        floatArrayB = new float[size];
        for (int i = 0; i < size; i++) {
            floatArrayA[i] = i + 0.5f;
            floatArrayB[i] = i * 2.5f;
        }

        doubleArrayA = new double[size];
        doubleArrayB = new double[size];
        for (int i = 0; i < size; i++) {
            doubleArrayA[i] = i + 0.25;
            doubleArrayB[i] = i * 2.75;
        }

        booleanArrayA = new boolean[size];
        booleanArrayB = new boolean[size];
        for (int i = 0; i < size; i++) {
            booleanArrayA[i] = (i % 2) == 0;
            booleanArrayB[i] = (i % 3) == 0;
        }

        // Object arrays
        integerArrayA = new Integer[size];
        integerArrayB = new Integer[size];
        stringArrayA = new String[size];
        stringArrayB = new String[size];
        objectArray = new Object[size];
        for (int i = 0; i < size; i++) {
            integerArrayA[i] = i;
            integerArrayB[i] = i * 2;
            stringArrayA[i] = "A" + i;
            stringArrayB[i] = "B" + i;
            objectArray[i] = i;
        }

        // Sub‑array patterns (small slices)
        subIntArray = new int[] { intArrayA[100], intArrayA[101], intArrayA[102] };
        subByteArray = new byte[] { byteArrayA[200], byteArrayA[201] };
        subCharArray = new char[] { charArrayA[300], charArrayA[301] };
        subShortArray = new short[] { shortArrayA[400] };
        subLongArray = new long[] { longArrayA[500], longArrayA[501] };
        subFloatArray = new float[] { floatArrayA[600] };
        subDoubleArray = new double[] { doubleArrayA[700], doubleArrayA[701] };
        subBooleanArray = new boolean[] { booleanArrayA[800] };

        // Wrapper arrays for values/valuesOf
        byteObjectArray = new Byte[size];
        charObjectArray = new Character[size];
        shortObjectArray = new Short[size];
        intObjectArray = new Integer[size];
        longObjectArray = new Long[size];
        floatObjectArray = new Float[size];
        doubleObjectArray = new Double[size];
        booleanObjectArray = new Boolean[size];
        for (int i = 0; i < size; i++) {
            byteObjectArray[i] = (byte) i;
            charObjectArray[i] = (char) (i % 65535);
            shortObjectArray[i] = (short) i;
            intObjectArray[i] = i;
            longObjectArray[i] = (long) i;
            floatObjectArray[i] = i + 0.5f;
            doubleObjectArray[i] = i + 0.25;
            booleanObjectArray[i] = (i % 2) == 0;
        }
    }

    // ---------------------------------------------------------------- join benchmarks

    @Benchmark
    public Integer[] benchmarkJoinObject() {
        return ArraysUtil.join(integerArrayA, integerArrayB);
    }

    @Benchmark
    public byte[] benchmarkJoinByte() {
        return ArraysUtil.join(byteArrayA, byteArrayB);
    }

    @Benchmark
    public char[] benchmarkJoinChar() {
        return ArraysUtil.join(charArrayA, charArrayB);
    }

    @Benchmark
    public short[] benchmarkJoinShort() {
        return ArraysUtil.join(shortArrayA, shortArrayB);
    }

    @Benchmark
    public int[] benchmarkJoinInt() {
        return ArraysUtil.join(intArrayA, intArrayB);
    }

    @Benchmark
    public long[] benchmarkJoinLong() {
        return ArraysUtil.join(longArrayA, longArrayB);
    }

    @Benchmark
    public float[] benchmarkJoinFloat() {
        return ArraysUtil.join(floatArrayA, floatArrayB);
    }

    @Benchmark
    public double[] benchmarkJoinDouble() {
        return ArraysUtil.join(doubleArrayA, doubleArrayB);
    }

    @Benchmark
    public boolean[] benchmarkJoinBoolean() {
        return ArraysUtil.join(booleanArrayA, booleanArrayB);
    }

    @Benchmark
    public String[] benchmarkJoinString() {
        return ArraysUtil.join(stringArrayA, stringArrayB);
    }

    // ---------------------------------------------------------------- resize benchmarks

    @Benchmark
    public int[] benchmarkResizeInt() {
        return ArraysUtil.resize(intArrayA, intArrayA.length * 2);
    }

    @Benchmark
    public byte[] benchmarkResizeByte() {
        return ArraysUtil.resize(byteArrayA, byteArrayA.length * 2);
    }

    @Benchmark
    public char[] benchmarkResizeChar() {
        return ArraysUtil.resize(charArrayA, charArrayA.length * 2);
    }

    @Benchmark
    public short[] benchmarkResizeShort() {
        return ArraysUtil.resize(shortArrayA, shortArrayA.length * 2);
    }

    @Benchmark
    public long[] benchmarkResizeLong() {
        return ArraysUtil.resize(longArrayA, longArrayA.length * 2);
    }

    @Benchmark
    public float[] benchmarkResizeFloat() {
        return ArraysUtil.resize(floatArrayA, floatArrayA.length * 2);
    }

    @Benchmark
    public double[] benchmarkResizeDouble() {
        return ArraysUtil.resize(doubleArrayA, doubleArrayA.length * 2);
    }

    @Benchmark
    public boolean[] benchmarkResizeBoolean() {
        return ArraysUtil.resize(booleanArrayA, booleanArrayA.length * 2);
    }

    @Benchmark
    public String[] benchmarkResizeString() {
        return ArraysUtil.resize(stringArrayA, stringArrayA.length * 2);
    }

    // ---------------------------------------------------------------- append benchmarks

    @Benchmark
    public int[] benchmarkAppendInt() {
        return ArraysUtil.append(intArrayA, 123456);
    }

    @Benchmark
    public byte[] benchmarkAppendByte() {
        return ArraysUtil.append(byteArrayA, (byte) 0x7F);
    }

    @Benchmark
    public char[] benchmarkAppendChar() {
        return ArraysUtil.append(charArrayA, 'Z');
    }

    @Benchmark
    public short[] benchmarkAppendShort() {
        return ArraysUtil.append(shortArrayA, (short) 3210);
    }

    @Benchmark
    public long[] benchmarkAppendLong() {
        return ArraysUtil.append(longArrayA, 9876543210L);
    }

    @Benchmark
    public float[] benchmarkAppendFloat() {
        return ArraysUtil.append(floatArrayA, 3.1415f);
    }

    @Benchmark
    public double[] benchmarkAppendDouble() {
        return ArraysUtil.append(doubleArrayA, 2.71828);
    }

    @Benchmark
    public boolean[] benchmarkAppendBoolean() {
        return ArraysUtil.append(booleanArrayA, true);
    }

    @Benchmark
    public String[] benchmarkAppendString() {
        return ArraysUtil.append(stringArrayA, "extra");
    }

    // ---------------------------------------------------------------- remove benchmarks

    @Benchmark
    public int[] benchmarkRemoveInt() {
        return ArraysUtil.remove(intArrayA, 100, 50);
    }

    @Benchmark
    public byte[] benchmarkRemoveByte() {
        return ArraysUtil.remove(byteArrayA, 100, 50);
    }

    @Benchmark
    public char[] benchmarkRemoveChar() {
        return ArraysUtil.remove(charArrayA, 100, 50);
    }

    @Benchmark
    public short[] benchmarkRemoveShort() {
        return ArraysUtil.remove(shortArrayA, 100, 50);
    }

    @Benchmark
    public long[] benchmarkRemoveLong() {
        return ArraysUtil.remove(longArrayA, 100, 50);
    }

    @Benchmark
    public float[] benchmarkRemoveFloat() {
        return ArraysUtil.remove(floatArrayA, 100, 50);
    }

    @Benchmark
    public double[] benchmarkRemoveDouble() {
        return ArraysUtil.remove(doubleArrayA, 100, 50);
    }

    @Benchmark
    public boolean[] benchmarkRemoveBoolean() {
        return ArraysUtil.remove(booleanArrayA, 100, 50);
    }

    @Benchmark
    public String[] benchmarkRemoveString() {
        return ArraysUtil.remove(stringArrayA, 100, 50);
    }

    // ---------------------------------------------------------------- subarray benchmarks

    @Benchmark
    public int[] benchmarkSubarrayInt() {
        return ArraysUtil.subarray(intArrayA, 200, 300);
    }

    @Benchmark
    public byte[] benchmarkSubarrayByte() {
        return ArraysUtil.subarray(byteArrayA, 200, 300);
    }

    @Benchmark
    public char[] benchmarkSubarrayChar() {
        return ArraysUtil.subarray(charArrayA, 200, 300);
    }

    @Benchmark
    public short[] benchmarkSubarrayShort() {
        return ArraysUtil.subarray(shortArrayA, 200, 300);
    }

    @Benchmark
    public long[] benchmarkSubarrayLong() {
        return ArraysUtil.subarray(longArrayA, 200, 300);
    }

    @Benchmark
    public float[] benchmarkSubarrayFloat() {
        return ArraysUtil.subarray(floatArrayA, 200, 300);
    }

    @Benchmark
    public double[] benchmarkSubarrayDouble() {
        return ArraysUtil.subarray(doubleArrayA, 200, 300);
    }

    @Benchmark
    public boolean[] benchmarkSubarrayBoolean() {
        return ArraysUtil.subarray(booleanArrayA, 200, 300);
    }

    @Benchmark
    public String[] benchmarkSubarrayString() {
        return ArraysUtil.subarray(stringArrayA, 200, 300);
    }

    // ---------------------------------------------------------------- insert benchmarks

    @Benchmark
    public int[] benchmarkInsertIntArray() {
        return ArraysUtil.insert(intArrayA, intArrayB, 150);
    }

    @Benchmark
    public int[] benchmarkInsertIntElement() {
        return ArraysUtil.insert(intArrayA, 777777, 150);
    }

    @Benchmark
    public byte[] benchmarkInsertByteArray() {
        return ArraysUtil.insert(byteArrayA, byteArrayB, 150);
    }

    @Benchmark
    public byte[] benchmarkInsertByteElement() {
        return ArraysUtil.insert(byteArrayA, (byte) 0x55, 150);
    }

    @Benchmark
    public char[] benchmarkInsertCharArray() {
        return ArraysUtil.insert(charArrayA, charArrayB, 150);
    }

    @Benchmark
    public char[] benchmarkInsertCharElement() {
        return ArraysUtil.insert(charArrayA, 'X', 150);
    }

    @Benchmark
    public short[] benchmarkInsertShortArray() {
        return ArraysUtil.insert(shortArrayA, shortArrayB, 150);
    }

    @Benchmark
    public short[] benchmarkInsertShortElement() {
        return ArraysUtil.insert(shortArrayA, (short) 12345, 150);
    }

    @Benchmark
    public long[] benchmarkInsertLongArray() {
        return ArraysUtil.insert(longArrayA, longArrayB, 150);
    }

    @Benchmark
    public long[] benchmarkInsertLongElement() {
        return ArraysUtil.insert(longArrayA, 999999999L, 150);
    }

    @Benchmark
    public float[] benchmarkInsertFloatArray() {
        return ArraysUtil.insert(floatArrayA, floatArrayB, 150);
    }

    @Benchmark
    public float[] benchmarkInsertFloatElement() {
        return ArraysUtil.insert(floatArrayA, 1.618f, 150);
    }

    @Benchmark
    public double[] benchmarkInsertDoubleArray() {
        return ArraysUtil.insert(doubleArrayA, doubleArrayB, 150);
    }

    @Benchmark
    public double[] benchmarkInsertDoubleElement() {
        return ArraysUtil.insert(doubleArrayA, 0.57721, 150);
    }

    @Benchmark
    public boolean[] benchmarkInsertBooleanArray() {
        return ArraysUtil.insert(booleanArrayA, booleanArrayB, 150);
    }

    @Benchmark
    public boolean[] benchmarkInsertBooleanElement() {
        return ArraysUtil.insert(booleanArrayA, true, 150);
    }

    @Benchmark
    public String[] benchmarkInsertStringArray() {
        return ArraysUtil.insert(stringArrayA, stringArrayB, 150);
    }

    @Benchmark
    public String[] benchmarkInsertStringElement() {
        return ArraysUtil.insert(stringArrayA, "inserted", 150);
    }

    // ---------------------------------------------------------------- insertAt benchmarks

    @Benchmark
    public int[] benchmarkInsertAtIntArray() {
        return ArraysUtil.insertAt(intArrayA, intArrayB, 200);
    }

    @Benchmark
    public byte[] benchmarkInsertAtByteArray() {
        return ArraysUtil.insertAt(byteArrayA, byteArrayB, 200);
    }

    @Benchmark
    public char[] benchmarkInsertAtCharArray() {
        return ArraysUtil.insertAt(charArrayA, charArrayB, 200);
    }

    @Benchmark
    public short[] benchmarkInsertAtShortArray() {
        return ArraysUtil.insertAt(shortArrayA, shortArrayB, 200);
    }

    @Benchmark
    public long[] benchmarkInsertAtLongArray() {
        return ArraysUtil.insertAt(longArrayA, longArrayB, 200);
    }

    @Benchmark
    public float[] benchmarkInsertAtFloatArray() {
        return ArraysUtil.insertAt(floatArrayA, floatArrayB, 200);
    }

    @Benchmark
    public double[] benchmarkInsertAtDoubleArray() {
        return ArraysUtil.insertAt(doubleArrayA, doubleArrayB, 200);
    }

    @Benchmark
    public boolean[] benchmarkInsertAtBooleanArray() {
        return ArraysUtil.insertAt(booleanArrayA, booleanArrayB, 200);
    }

    @Benchmark
    public String[] benchmarkInsertAtStringArray() {
        return ArraysUtil.insertAt(stringArrayA, stringArrayB, 200);
    }

    // ---------------------------------------------------------------- values / valuesOf benchmarks

    @Benchmark
    public byte[] benchmarkValuesByte() {
        return ArraysUtil.values(byteObjectArray);
    }

    @Benchmark
    public Byte[] benchmarkValuesOfByte() {
        return ArraysUtil.valuesOf(byteArrayA);
    }

    @Benchmark
    public char[] benchmarkValuesChar() {
        return ArraysUtil.values(charObjectArray);
    }

    @Benchmark
    public Character[] benchmarkValuesOfChar() {
        return ArraysUtil.valuesOf(charArrayA);
    }

    @Benchmark
    public short[] benchmarkValuesShort() {
        return ArraysUtil.values(shortObjectArray);
    }

    @Benchmark
    public Short[] benchmarkValuesOfShort() {
        return ArraysUtil.valuesOf(shortArrayA);
    }

    @Benchmark
    public int[] benchmarkValuesInt() {
        return ArraysUtil.values(intObjectArray);
    }

    @Benchmark
    public Integer[] benchmarkValuesOfInt() {
        return ArraysUtil.valuesOf(intArrayA);
    }

    @Benchmark
    public long[] benchmarkValuesLong() {
        return ArraysUtil.values(longObjectArray);
    }

    @Benchmark
    public Long[] benchmarkValuesOfLong() {
        return ArraysUtil.valuesOf(longArrayA);
    }

    @Benchmark
    public float[] benchmarkValuesFloat() {
        return ArraysUtil.values(floatObjectArray);
    }

    @Benchmark
    public Float[] benchmarkValuesOfFloat() {
        return ArraysUtil.valuesOf(floatArrayA);
    }

    @Benchmark
    public double[] benchmarkValuesDouble() {
        return ArraysUtil.values(doubleObjectArray);
    }

    @Benchmark
    public Double[] benchmarkValuesOfDouble() {
        return ArraysUtil.valuesOf(doubleArrayA);
    }

    @Benchmark
    public boolean[] benchmarkValuesBoolean() {
        return ArraysUtil.values(booleanObjectArray);
    }

    @Benchmark
    public Boolean[] benchmarkValuesOfBoolean() {
        return ArraysUtil.valuesOf(booleanArrayA);
    }

    // ---------------------------------------------------------------- indexOf / contains benchmarks (single value)

    @Benchmark
    public int benchmarkIndexOfInt() {
        return ArraysUtil.indexOf(intArrayA, intArrayA[512]);
    }

    @Benchmark
    public boolean benchmarkContainsInt() {
        return ArraysUtil.contains(intArrayA, intArrayA[512]);
    }

    @Benchmark
    public int benchmarkIndexOfIntFrom() {
        return ArraysUtil.indexOf(intArrayA, intArrayA[512], 256);
    }

    @Benchmark
    public int benchmarkIndexOfIntRange() {
        return ArraysUtil.indexOf(intArrayA, intArrayA[512], 256, 768);
    }

    @Benchmark
    public int benchmarkIndexOfByte() {
        return ArraysUtil.indexOf(byteArrayA, byteArrayA[256]);
    }

    @Benchmark
    public boolean benchmarkContainsByte() {
        return ArraysUtil.contains(byteArrayA, byteArrayA[256]);
    }

    @Benchmark
    public int benchmarkIndexOfChar() {
        return ArraysUtil.indexOf(charArrayA, charArrayA[300]);
    }

    @Benchmark
    public boolean benchmarkContainsChar() {
        return ArraysUtil.contains(charArrayA, charArrayA[300]);
    }

    @Benchmark
    public int benchmarkIndexOfShort() {
        return ArraysUtil.indexOf(shortArrayA, shortArrayA[400]);
    }

    @Benchmark
    public boolean benchmarkContainsShort() {
        return ArraysUtil.contains(shortArrayA, shortArrayA[400]);
    }

    @Benchmark
    public int benchmarkIndexOfLong() {
        return ArraysUtil.indexOf(longArrayA, longArrayA[500]);
    }

    @Benchmark
    public boolean benchmarkContainsLong() {
        return ArraysUtil.contains(longArrayA, longArrayA[500]);
    }

    @Benchmark
    public int benchmarkIndexOfFloat() {
        return ArraysUtil.indexOf(floatArrayA, floatArrayA[600]);
    }

    @Benchmark
    public boolean benchmarkContainsFloat() {
        return ArraysUtil.contains(floatArrayA, floatArrayA[600]);
    }

    @Benchmark
    public int benchmarkIndexOfDouble() {
        return ArraysUtil.indexOf(doubleArrayA, doubleArrayA[700]);
    }

    @Benchmark
    public boolean benchmarkContainsDouble() {
        return ArraysUtil.contains(doubleArrayA, doubleArrayA[700]);
    }

    @Benchmark
    public int benchmarkIndexOfBoolean() {
        return ArraysUtil.indexOf(booleanArrayA, booleanArrayA[800]);
    }

    @Benchmark
    public boolean benchmarkContainsBoolean() {
        return ArraysUtil.contains(booleanArrayA, booleanArrayA[800]);
    }

    // ---------------------------------------------------------------- indexOf sub‑array benchmarks

    @Benchmark
    public int benchmarkIndexOfIntSubarray() {
        return ArraysUtil.indexOf(intArrayA, subIntArray);
    }

    @Benchmark
    public boolean benchmarkContainsIntSubarray() {
        return ArraysUtil.contains(intArrayA, subIntArray);
    }

    @Benchmark
    public int benchmarkIndexOfByteSubarray() {
        return ArraysUtil.indexOf(byteArrayA, subByteArray);
    }

    @Benchmark
    public boolean benchmarkContainsByteSubarray() {
        return ArraysUtil.contains(byteArrayA, subByteArray);
    }

    @Benchmark
    public int benchmarkIndexOfCharSubarray() {
        return ArraysUtil.indexOf(charArrayA, subCharArray);
    }

    @Benchmark
    public boolean benchmarkContainsCharSubarray() {
        return ArraysUtil.contains(charArrayA, subCharArray);
    }

    @Benchmark
    public int benchmarkIndexOfShortSubarray() {
        return ArraysUtil.indexOf(shortArrayA, subShortArray);
    }

    @Benchmark
    public boolean benchmarkContainsShortSubarray() {
        return ArraysUtil.contains(shortArrayA, subShortArray);
    }

    @Benchmark
    public int benchmarkIndexOfLongSubarray() {
        return ArraysUtil.indexOf(longArrayA, subLongArray);
    }

    @Benchmark
    public boolean benchmarkContainsLongSubarray() {
        return ArraysUtil.contains(longArrayA, subLongArray);
    }

    @Benchmark
    public int benchmarkIndexOfFloatSubarray() {
        return ArraysUtil.indexOf(floatArrayA, subFloatArray);
    }

    @Benchmark
    public boolean benchmarkContainsFloatSubarray() {
        return ArraysUtil.contains(floatArrayA, subFloatArray);
    }

    @Benchmark
    public int benchmarkIndexOfDoubleSubarray() {
        return ArraysUtil.indexOf(doubleArrayA, subDoubleArray);
    }

    @Benchmark
    public boolean benchmarkContainsDoubleSubarray() {
        return ArraysUtil.contains(doubleArrayA, subDoubleArray);
    }

    @Benchmark
    public int benchmarkIndexOfBooleanSubarray() {
        return ArraysUtil.indexOf(booleanArrayA, subBooleanArray);
    }

    @Benchmark
    public boolean benchmarkContainsBooleanSubarray() {
        return ArraysUtil.contains(booleanArrayA, subBooleanArray);
    }

    // ---------------------------------------------------------------- toString benchmarks

    @Benchmark
    public String benchmarkToStringInt() {
        return ArraysUtil.toString(intArrayA);
    }

    @Benchmark
    public String benchmarkToStringByte() {
        return ArraysUtil.toString(byteArrayA);
    }

    @Benchmark
    public String benchmarkToStringChar() {
        return ArraysUtil.toString(charArrayA);
    }

    @Benchmark
    public String benchmarkToStringShort() {
        return ArraysUtil.toString(shortArrayA);
    }

    @Benchmark
    public String benchmarkToStringLong() {
        return ArraysUtil.toString(longArrayA);
    }

    @Benchmark
    public String benchmarkToStringFloat() {
        return ArraysUtil.toString(floatArrayA);
    }

    @Benchmark
    public String benchmarkToStringDouble() {
        return ArraysUtil.toString(doubleArrayA);
    }

    @Benchmark
    public String benchmarkToStringBoolean() {
        return ArraysUtil.toString(booleanArrayA);
    }

    @Benchmark
    public String benchmarkToStringString() {
        return ArraysUtil.toString(stringArrayA);
    }

    @Benchmark
    public String benchmarkToStringObject() {
        return ArraysUtil.toString(objectArray);
    }

    // ---------------------------------------------------------------- toStringArray benchmarks

    @Benchmark
    public String[] benchmarkToStringArrayInt() {
        return ArraysUtil.toStringArray(intArrayA);
    }

    @Benchmark
    public String[] benchmarkToStringArrayByte() {
        return ArraysUtil.toStringArray(byteArrayA);
    }

    @Benchmark
    public String[] benchmarkToStringArrayChar() {
        return ArraysUtil.toStringArray(charArrayA);
    }

    @Benchmark
    public String[] benchmarkToStringArrayShort() {
        return ArraysUtil.toStringArray(shortArrayA);
    }

    @Benchmark
    public String[] benchmarkToStringArrayLong() {
        return ArraysUtil.toStringArray(longArrayA);
    }

    @Benchmark
    public String[] benchmarkToStringArrayFloat() {
        return ArraysUtil.toStringArray(floatArrayA);
    }

    @Benchmark
    public String[] benchmarkToStringArrayDouble() {
        return ArraysUtil.toStringArray(doubleArrayA);
    }

    @Benchmark
    public String[] benchmarkToStringArrayBoolean() {
        return ArraysUtil.toStringArray(booleanArrayA);
    }

    @Benchmark
    public String[] benchmarkToStringArrayString() {
        return ArraysUtil.toStringArray(stringArrayA);
    }

    @Benchmark
    public String[] benchmarkToStringArrayObject() {
        return ArraysUtil.toStringArray(objectArray);
    }
}
