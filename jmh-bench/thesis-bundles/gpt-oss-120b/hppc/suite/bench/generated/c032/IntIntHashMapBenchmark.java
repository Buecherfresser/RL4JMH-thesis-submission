package bench.generated.c032;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.IntIntHashMap;
import com.carrotsearch.hppc.cursors.IntIntCursor;
import com.carrotsearch.hppc.procedures.IntIntProcedure;
import com.carrotsearch.hppc.predicates.IntIntPredicate;
import java.util.Random;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class IntIntHashMapBenchmark {

    private static final int ELEMENT_COUNT = 1024;

    private IntIntHashMap mapRead;
    private IntIntHashMap mapPut;
    private IntIntHashMap mapPutOrAdd;
    private IntIntHashMap mapAddTo;
    private IntIntHashMap mapInsert;
    private IntIntHashMap mapForEach;

    private int[] keys;
    private int[] values;

    private int existingKey;
    private int missingKey;

    private int existingIndex;
    private int missingIndex;

    @Setup(Level.Trial)
    public void setup() {
        Random rnd = new Random(12345L);
        keys = new int[ELEMENT_COUNT];
        values = new int[ELEMENT_COUNT];
        for (int i = 0; i < ELEMENT_COUNT; i++) {
            // ensure non‑zero and unique keys
            keys[i] = i + 1;
            values[i] = (i + 1) * 2;
        }

        existingKey = keys[0];
        missingKey = -1; // not present in the map

        mapRead = new IntIntHashMap(ELEMENT_COUNT);
        for (int i = 0; i < ELEMENT_COUNT; i++) {
            mapRead.put(keys[i], values[i]);
        }

        mapPut = mapRead.clone();
        mapPutOrAdd = mapRead.clone();
        mapAddTo = mapRead.clone();
        mapInsert = new IntIntHashMap(ELEMENT_COUNT);
        for (int i = 0; i < ELEMENT_COUNT; i++) {
            mapInsert.put(keys[i], values[i]);
        }
        mapForEach = mapRead.clone();

        existingIndex = mapRead.indexOf(existingKey);
        missingIndex = mapRead.indexOf(missingKey);
    }

    @Benchmark
    public int benchmarkGetExisting() {
        return mapRead.get(existingKey);
    }

    @Benchmark
    public int benchmarkGetMissing() {
        return mapRead.get(missingKey);
    }

    @Benchmark
    public int benchmarkGetOrDefaultExisting() {
        return mapRead.getOrDefault(existingKey, -1);
    }

    @Benchmark
    public int benchmarkGetOrDefaultMissing() {
        return mapRead.getOrDefault(missingKey, -1);
    }

    @Benchmark
    public boolean benchmarkContainsKeyExisting() {
        return mapRead.containsKey(existingKey);
    }

    @Benchmark
    public boolean benchmarkContainsKeyMissing() {
        return mapRead.containsKey(missingKey);
    }

    @Benchmark
    public int benchmarkRemoveMissing() {
        return mapRead.remove(missingKey);
    }

    @Benchmark
    public int benchmarkPutExisting() {
        return mapPut.put(existingKey, values[0]);
    }

    @Benchmark
    public int benchmarkPutOrAddExisting() {
        return mapPutOrAdd.putOrAdd(existingKey, 1, 2);
    }

    @Benchmark
    public int benchmarkPutOrAddMissing() {
        return mapPutOrAdd.putOrAdd(missingKey, 5, 3);
    }

    @Benchmark
    public int benchmarkAddToExisting() {
        return mapAddTo.addTo(existingKey, 1);
    }

    @Benchmark
    public int benchmarkAddToMissing() {
        return mapAddTo.addTo(missingKey, 1);
    }

    @Benchmark
    public int benchmarkIndexOfExisting() {
        return mapRead.indexOf(existingKey);
    }

    @Benchmark
    public int benchmarkIndexOfMissing() {
        return mapRead.indexOf(missingKey);
    }

    @Benchmark
    public int benchmarkIndexGet() {
        return mapRead.indexGet(existingIndex);
    }

    @Benchmark
    public int benchmarkIndexReplace() {
        return mapRead.indexReplace(existingIndex, 999);
    }

    @Benchmark
    public void benchmarkIndexInsert(Blackhole bh) {
        mapInsert.indexInsert(missingIndex, missingKey, 555);
        bh.consume(mapInsert);
    }

    @Benchmark
    public void benchmarkForEachProcedure(Blackhole bh) {
        mapForEach.forEach((IntIntProcedure) (k, v) -> {
        });
        bh.consume(mapForEach);
    }

    @Benchmark
    public void benchmarkForEachPredicate(Blackhole bh) {
        mapForEach.forEach((IntIntPredicate) (k, v) -> true);
        bh.consume(mapForEach);
    }

    @Benchmark
    public void benchmarkIterator(Blackhole bh) {
        for (IntIntCursor c : mapRead) {
            bh.consume(c.key);
            bh.consume(c.value);
        }
    }
}
