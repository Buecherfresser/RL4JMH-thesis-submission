package bench.generated.c030;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import com.carrotsearch.hppc.IntFloatHashMap;
import com.carrotsearch.hppc.cursors.IntFloatCursor;
import com.carrotsearch.hppc.HashContainers;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class IntFloatHashMapBenchmark {

    private IntFloatHashMap map;
    private int[] keys;
    private float[] values;
    private List<IntFloatCursor> cursorList;
    private Random random;

    // Constants for setup
    private static final int INITIAL_SIZE = 10000;
    private static final int NUM_OPERATIONS = 100000;

    @Setup(Level.Trial)
    public void setupTrial() {
        random = new Random(42);
        
        // 1. Generate initial keys and values
        keys = new int[INITIAL_SIZE];
        values = new float[INITIAL_SIZE];
        
        for (int i = 0; i < INITIAL_SIZE; i++) {
            keys[i] = random.nextInt(100000); // Keys up to 100k
            values[i] = random.nextFloat();
        }

        // 2. Build the map using the static factory method
        map = IntFloatHashMap.from(keys, values);

        // 3. Prepare a list of cursors for bulk operations
        cursorList = new ArrayList<>(INITIAL_SIZE);
        for (int i = 0; i < INITIAL_SIZE; i++) {
            cursorList.add(new IntFloatCursor());
            cursorList.get(i).index = i;
            cursorList.get(i).key = keys[i];
            cursorList.get(i).value = values[i];
        }
    }

    @Benchmark
    public void testPut(Blackhole bh) {
        int key = random.nextInt(100000);
        float value = random.nextFloat();
        bh.consume(map.put(key, value));
    }

    @Benchmark
    public void testGet(Blackhole bh) {
        int key = random.nextInt(100000);
        bh.consume(map.get(key));
    }

    @Benchmark
    public void testGetOrDefault(Blackhole bh) {
        int key = random.nextInt(100000);
        float defaultValue = 1.0f;
        bh.consume(map.getOrDefault(key, defaultValue));
    }

    @Benchmark
    public void testContainsKey(Blackhole bh) {
        int key = random.nextInt(100000);
        bh.consume(map.containsKey(key));
    }

    @Benchmark
    public void testPutOrAdd(Blackhole bh) {
        int key = random.nextInt(100000);
        float putValue = random.nextFloat();
        float incrementValue = random.nextFloat() * 0.1f;
        bh.consume(map.putOrAdd(key, putValue, incrementValue));
    }

    @Benchmark
    public void testAddTo(Blackhole bh) {
        int key = random.nextInt(100000);
        float incrementValue = random.nextFloat() * 0.1f;
        bh.consume(map.addTo(key, incrementValue));
    }

    @Benchmark
    public void testPutAllFromCursorList(Blackhole bh) {
        // Use the pre-built list of cursors
        bh.consume(map.putAll(cursorList));
    }

    @Benchmark
    public void testSize(Blackhole bh) {
        bh.consume(map.size());
    }

    @Benchmark
    public void testClear(Blackhole bh) {
        map.clear();
        bh.consume(map.size());
    }
}
