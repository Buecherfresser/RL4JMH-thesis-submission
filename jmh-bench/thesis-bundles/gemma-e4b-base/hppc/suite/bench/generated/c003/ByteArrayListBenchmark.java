package bench.generated.c003;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.ByteArrayList;
import com.carrotsearch.hppc.ByteContainer;
import com.carrotsearch.hppc.ByteIndexedContainer;
import com.carrotsearch.hppc.cursors.ByteCursor;
import com.carrotsearch.hppc.predicates.BytePredicate;
import com.carrotsearch.hppc.procedures.ByteProcedure;
import java.util.Iterator;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ByteArrayListBenchmark {

    private static final int INITIAL_SIZE = 1024;
    private static final int ADDITION_SIZE = 1;

    // State for read/write operations on a stable list
    private ByteArrayList list;
    private byte[] testData;
    private byte targetByte;

    // State for mutation operations that require a fresh list every time
    private ByteArrayList mutableList;

    @Setup(Level.Trial)
    public void setupTrial() {
        // 1. Setup stable list for read/write benchmarks
        testData = new byte[INITIAL_SIZE];
        for (int i = 0; i < INITIAL_SIZE; i++) {
            testData[i] = (byte) (i % 256);
        }
        list = ByteArrayList.from(testData);
        targetByte = (byte) 42;

        // 2. Setup mutable list for destructive benchmarks
        mutableList = ByteArrayList.from(testData);
    }

    // --- Read Operations ---

    @Benchmark
    public byte getElement() {
        // Read operation on stable list
        return list.get(INITIAL_SIZE / 2);
    }

    @Benchmark
    public boolean containsElement() {
        // Read operation on stable list
        return list.contains(targetByte);
    }

    @Benchmark
    public int indexOfElement() {
        // Read operation on stable list
        return list.indexOf(targetByte);
    }

    @Benchmark
    public int lastIndexOfElement() {
        // Read operation on stable list
        return list.lastIndexOf(targetByte);
    }

    @Benchmark
    public int size() {
        // Read operation
        return list.size();
    }

    @Benchmark
    public boolean isEmpty() {
        // Read operation
        return list.isEmpty();
    }

    @Benchmark
    public byte[] toArray() {
        // Copy operation
        return list.toArray();
    }

    // --- Write/Mutation Operations (Single Unit of Work) ---

    @Benchmark
    public void setElement() {
        // Write operation on stable list
        list.set(INITIAL_SIZE / 2, (byte) 99);
    }

    @Benchmark
    public byte removeAtElement() {
        // Destructive operation: must use a fresh list state
        mutableList = ByteArrayList.from(testData);
        return mutableList.removeAt(INITIAL_SIZE / 2);
    }

    @Benchmark
    public void insertElement() {
        // Destructive operation: must use a fresh list state
        mutableList = ByteArrayList.from(testData);
        mutableList.insert(INITIAL_SIZE / 2, (byte) 1);
    }

    @Benchmark
    public void removeRange() {
        // Destructive operation: must use a fresh list state
        mutableList = ByteArrayList.from(testData);
        mutableList.removeRange(INITIAL_SIZE / 4, INITIAL_SIZE / 4 + 10);
    }

    @Benchmark
    public void removeLast() {
        // Destructive operation: must use a fresh list state
        mutableList = ByteArrayList.from(testData);
        mutableList.removeLast();
    }

    @Benchmark
    public void clearList() {
        // Mutation operation: reset state
        list.clear();
    }

    @Benchmark
    public void releaseList() {
        // Mutation operation: reset state
        list.release();
    }

    @Benchmark
    public void trimToSize() {
        // Mutation operation: reset state
        list.trimToSize();
    }

    // --- Add Operations ---

    @Benchmark
    public void addSingleElement() {
        // Mutation operation: must use a fresh list state
        mutableList = ByteArrayList.from(testData);
        mutableList.add((byte) 1);
    }

    @Benchmark
    public void addTwoElements() {
        // Mutation operation: must use a fresh list state
        mutableList = ByteArrayList.from(testData);
        mutableList.add((byte) 1, (byte) 2);
    }

    @Benchmark
    public void addArrayRange() {
        // Mutation operation: must use a fresh list state
        mutableList = ByteArrayList.from(testData);
        mutableList.add(testData, 0, 10);
    }

    @Benchmark
    public void addAllContainer() {
        // Mutation operation: must use a fresh list state
        ByteArrayList container = ByteArrayList.from(testData);
        mutableList = ByteArrayList.from(testData);
        mutableList.addAll(container);
    }

    @Benchmark
    public void addAllIterable() {
        // Mutation operation: must use a fresh list state
        ByteArrayList container = ByteArrayList.from(testData);
        mutableList = ByteArrayList.from(testData);
        mutableList.addAll(container);
    }

    // --- Functional/Iteration Operations ---

    @Benchmark
    public void iterate() {
        // Read operation
        Iterator<ByteCursor> it = list.iterator();
        while (it.hasNext()) {
            it.next();
        }
    }

    @Benchmark
    public void forEachProcedure() {
        // Functional operation
        list.forEach(new ByteProcedure() {
            @Override
            public void apply(byte e1) {
                // Do nothing, just execute
            }
        });
    }

    @Benchmark
    public void forEachPredicate() {
        // Functional operation
        list.forEach(new BytePredicate() {
            @Override
            public boolean apply(byte e1) {
                return true; // Always true to iterate fully
            }
        });
    }

    @Benchmark
    public void forEachSliceProcedure() {
        // Functional operation on slice
        list.forEach(new ByteProcedure() {
            @Override
            public void apply(byte e1) {
                // Do nothing
            }
        }, 0, 10);
    }

    @Benchmark
    public void forEachSlicePredicate() {
        // Functional operation on slice
        list.forEach(new BytePredicate() {
            @Override
            public boolean apply(byte e1) {
                return true; // Always true to iterate fully
            }
        }, 0, 10);
    }

    @Benchmark
    public void removeAllByElement() {
        // Mutation operation: must use a fresh list state
        mutableList = ByteArrayList.from(testData);
        mutableList.removeAll((byte) 0);
    }

    @Benchmark
    public void removeAllByPredicate() {
        // Mutation operation: must use a fresh list state
        mutableList = ByteArrayList.from(testData);
        mutableList.removeAll(new BytePredicate() {
            @Override
            public boolean apply(byte e1) {
                return e1 == (byte) 0;
            }
        });
    }

    // --- Sorting/Reversing ---

    @Benchmark
    public ByteIndexedContainer sort() {
        // Mutation operation: must use a fresh list state
        mutableList = ByteArrayList.from(testData);
        return mutableList.sort();
    }

    @Benchmark
    public ByteIndexedContainer reverse() {
        // Mutation operation: must use a fresh list state
        mutableList = ByteArrayList.from(testData);
        return mutableList.reverse();
    }
}
