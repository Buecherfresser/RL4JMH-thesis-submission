package bench.generated.c051;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.infra.Blackhole;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.ObjectByteHashMap;
import com.carrotsearch.hppc.cursors.ObjectCursor;
import com.carrotsearch.hppc.cursors.ByteCursor;
import com.carrotsearch.hppc.procedures.ObjectByteProcedure;
import com.carrotsearch.hppc.predicates.ObjectBytePredicate;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ObjectByteHashMapBenchmark {

    private ObjectByteHashMap<Integer> map;
    private Integer[] keys;
    private byte[] values;
    private int size;

    @Setup(Level.Trial)
    public void setUp() {
        size = 1024;
        map = new ObjectByteHashMap<>(size);
        keys = new Integer[size];
        values = new byte[size];
        Random rand = new Random(12345);
        for (int i = 0; i < size; i++) {
            keys[i] = i;
            values[i] = (byte) (rand.nextInt(256) - 128);
            map.put(keys[i], values[i]);
        }
    }

    @Benchmark
    public byte getExisting() {
        return map.get(keys[0]);
    }

    @Benchmark
    public byte getOrDefaultExisting() {
        return map.getOrDefault(keys[0], (byte) -1);
    }

    @Benchmark
    public boolean containsKeyExisting() {
        return map.containsKey(keys[0]);
    }

    @Benchmark
    public int size() {
        return map.size();
    }

    @Benchmark
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Benchmark
    public int indexOfExisting() {
        return map.indexOf(keys[0]);
    }

    @Benchmark
    public byte indexGetExisting() {
        int idx = map.indexOf(keys[0]);
        return map.indexGet(idx);
    }

    @Benchmark
    public boolean keysContains() {
        return map.keys().contains(keys[0]);
    }

    @Benchmark
    public int keysIteratorCount() {
        int cnt = 0;
        Iterator<ObjectCursor<Integer>> it = map.keys().iterator();
        while (it.hasNext()) {
            it.next();
            cnt++;
        }
        return cnt;
    }

    @Benchmark
    public boolean valuesContains() {
        return map.values().contains((byte) 1);
    }

    @Benchmark
    public int valuesIteratorCount() {
        int cnt = 0;
        Iterator<ByteCursor> it = map.values().iterator();
        while (it.hasNext()) {
            it.next();
            cnt++;
        }
        return cnt;
    }

    @Benchmark
    public ObjectByteProcedure<Integer> forEachProcedure() {
        return map.forEach((ObjectByteProcedure<Integer>) (k, v) -> {
        });
    }

    @Benchmark
    public ObjectBytePredicate<Integer> forEachPredicate() {
        return map.forEach((ObjectBytePredicate<Integer>) (k, v) -> true);
    }

    @Benchmark
    public ObjectByteHashMap<Integer> cloneMap() {
        return map.clone();
    }

    @Benchmark
    public String toStringMap() {
        return map.toString();
    }
}
