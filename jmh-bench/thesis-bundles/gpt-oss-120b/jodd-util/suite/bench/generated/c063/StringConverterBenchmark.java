package bench.generated.c063;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.typeconverter.impl.StringConverter;
import java.sql.Clob;
import javax.sql.rowset.serial.SerialClob;
import java.sql.SQLException;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class StringConverterBenchmark {

    private StringConverter converter;

    private String stringValue;
    private Class<?> classValue;
    private char[] charArray;
    private int[] intArray;
    private long[] longArray;
    private byte[] byteArray;
    private float[] floatArray;
    private double[] doubleArray;
    private short[] shortArray;
    private boolean[] booleanArray;
    private String[] objectArray;
    private Clob clobValue;
    private Object genericObject;

    @Setup(Level.Trial)
    public void setUp() throws Exception {
        converter = new StringConverter();

        stringValue = "The quick brown fox jumps over the lazy dog";
        classValue = String.class;

        charArray = "hello".toCharArray();
        intArray = new int[]{1, 2, 3, 4, 5};
        longArray = new long[]{10L, 20L, 30L};
        byteArray = new byte[]{1, 2, 3};
        floatArray = new float[]{1.1f, 2.2f};
        doubleArray = new double[]{3.3, 4.4};
        shortArray = new short[]{5, 6};
        booleanArray = new boolean[]{true, false, true};

        objectArray = new String[]{"a", "b", "c"};

        genericObject = new Object() {
            @Override
            public String toString() {
                return "genericObject";
            }
        };

        clobValue = new SerialClob("clob content".toCharArray());
    }

    @Benchmark
    public String convertNull() {
        return converter.convert(null);
    }

    @Benchmark
    public String convertCharSequence() {
        return converter.convert(stringValue);
    }

    @Benchmark
    public String convertClass() {
        return converter.convert(classValue);
    }

    @Benchmark
    public String convertCharArray() {
        return converter.convert(charArray);
    }

    @Benchmark
    public String convertIntArray() {
        return converter.convert(intArray);
    }

    @Benchmark
    public String convertLongArray() {
        return converter.convert(longArray);
    }

    @Benchmark
    public String convertByteArray() {
        return converter.convert(byteArray);
    }

    @Benchmark
    public String convertFloatArray() {
        return converter.convert(floatArray);
    }

    @Benchmark
    public String convertDoubleArray() {
        return converter.convert(doubleArray);
    }

    @Benchmark
    public String convertShortArray() {
        return converter.convert(shortArray);
    }

    @Benchmark
    public String convertBooleanArray() {
        return converter.convert(booleanArray);
    }

    @Benchmark
    public String convertObjectArray() {
        return converter.convert(objectArray);
    }

    @Benchmark
    public String convertClob() {
        return converter.convert(clobValue);
    }

    @Benchmark
    public String convertGenericObject() {
        return converter.convert(genericObject);
    }
}
