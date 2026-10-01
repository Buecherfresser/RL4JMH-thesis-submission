package bench.generated.c050;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Iterator;
import java.util.stream.Stream;
import com.carrotsearch.hppc.ObjectArrayList;
import com.carrotsearch.hppc.cursors.ObjectCursor;
import com.carrotsearch.hppc.procedures.ObjectProcedure;
import com.carrotsearch.hppc.predicates.ObjectPredicate;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ObjectArrayListBenchmark {

    private int listSize;
    private int poolSize;
    private String elementPrefix;
    private String otherPrefix;

    private ObjectArrayList<String>[] listPool;
    private int poolIndex = 0;

    private ObjectArrayList<String> readOnlyList;

    private String[] addArray;
    private ObjectArrayList<String> containerForAddAll;

    @Setup(Level.Trial)
    public void setUp() {
        listSize = 1024;
        poolSize = 8;
        elementPrefix = "elem";
        otherPrefix = "other";

        @SuppressWarnings("unchecked")
        ObjectArrayList<String>[] tmp = new ObjectArrayList[poolSize];
        for (int i = 0; i < poolSize; i++) {
            tmp[i] = new ObjectArrayList<>(listSize);
            for (int j = 0; j < listSize; j++) {
                tmp[i].add(elementPrefix + j);
            }
        }
        listPool = tmp;

        readOnlyList = new ObjectArrayList<>(listSize);
        for (int i = 0; i < listSize; i++) {
            readOnlyList.add(elementPrefix + i);
        }

        addArray = new String[10];
        for (int i = 0; i < addArray.length; i++) {
            addArray[i] = otherPrefix + i;
        }

        containerForAddAll = new ObjectArrayList<>(addArray.length);
        for (String s : addArray) {
            containerForAddAll.add(s);
        }
    }

    private ObjectArrayList<String> nextList() {
        int idx = poolIndex;
        poolIndex = (poolIndex + 1) & (poolSize - 1);
        return listPool[idx];
    }

    @Benchmark
    public int benchmarkAdd() {
        ObjectArrayList<String> list = nextList();
        list.add(elementPrefix);
        return list.size();
    }

    @Benchmark
    public int benchmarkAddTwo() {
        ObjectArrayList<String> list = nextList();
        list.add(elementPrefix, otherPrefix);
        return list.size();
    }

    @Benchmark
    public int benchmarkAddArray() {
        ObjectArrayList<String> list = nextList();
        list.add(addArray, 0, addArray.length);
        return list.size();
    }

    @Benchmark
    public int benchmarkAddAllContainer() {
        ObjectArrayList<String> list = nextList();
        list.addAll(containerForAddAll);
        return list.size();
    }

    @Benchmark
    public int benchmarkInsert() {
        ObjectArrayList<String> list = nextList();
        int idx = list.size() / 2;
        list.insert(idx, otherPrefix);
        return list.size();
    }

    @Benchmark
    public String benchmarkGet() {
        int idx = readOnlyList.size() / 2;
        return readOnlyList.get(idx);
    }

    @Benchmark
    public String benchmarkSet() {
        ObjectArrayList<String> list = nextList();
        int idx = list.size() / 2;
        return list.set(idx, otherPrefix);
    }

    @Benchmark
    public String benchmarkRemoveAt() {
        ObjectArrayList<String> list = nextList();
        int idx = list.size() / 2;
        return list.removeAt(idx);
    }

    @Benchmark
    public String benchmarkRemoveLast() {
        ObjectArrayList<String> list = nextList();
        return list.removeLast();
    }

    @Benchmark
    public int benchmarkRemoveRange() {
        ObjectArrayList<String> list = nextList();
        int half = list.size() / 2;
        list.removeRange(0, half);
        return list.size();
    }

    @Benchmark
    public boolean benchmarkRemoveElement() {
        ObjectArrayList<String> list = nextList();
        return list.removeElement(elementPrefix);
    }

    @Benchmark
    public int benchmarkRemoveFirst() {
        ObjectArrayList<String> list = nextList();
        return list.removeFirst(elementPrefix);
    }

    @Benchmark
    public int benchmarkRemoveLastElement() {
        ObjectArrayList<String> list = nextList();
        return list.removeLast(elementPrefix);
    }

    @Benchmark
    public int benchmarkRemoveAllElement() {
        ObjectArrayList<String> list = nextList();
        return list.removeAll(elementPrefix);
    }

    @Benchmark
    public boolean benchmarkContains() {
        return readOnlyList.contains(elementPrefix);
    }

    @Benchmark
    public int benchmarkIndexOf() {
        return readOnlyList.indexOf(elementPrefix);
    }

    @Benchmark
    public int benchmarkLastIndexOf() {
        return readOnlyList.lastIndexOf(elementPrefix);
    }

    @Benchmark
    public int benchmarkEnsureCapacity() {
        ObjectArrayList<String> list = nextList();
        list.ensureCapacity(list.size() + 10);
        return list.size();
    }

    @Benchmark
    public int benchmarkResize() {
        ObjectArrayList<String> list = nextList();
        int newSize = list.size() / 2;
        list.resize(newSize);
        return list.size();
    }

    @Benchmark
    public int benchmarkTrimToSize() {
        ObjectArrayList<String> list = nextList();
        list.trimToSize();
        return list.size();
    }

    @Benchmark
    public boolean benchmarkClear() {
        ObjectArrayList<String> list = nextList();
        list.clear();
        return list.isEmpty();
    }

    @Benchmark
    public int benchmarkRelease() {
        ObjectArrayList<String> list = nextList();
        list.release();
        return list.size();
    }

    @Benchmark
    public int benchmarkToArray() {
        Object[] arr = readOnlyList.toArray();
        return arr.length;
    }

    @Benchmark
    public long benchmarkStreamCount() {
        Stream<String> s = readOnlyList.stream();
        return s.count();
    }

    @Benchmark
    public String benchmarkSort() {
        ObjectArrayList<String> list = nextList();
        list.sort();
        return list.get(0);
    }

    @Benchmark
    public String benchmarkReverse() {
        ObjectArrayList<String> list = nextList();
        list.reverse();
        return list.get(0);
    }

    @Benchmark
    public int benchmarkClone() {
        ObjectArrayList<String> cloned = readOnlyList.clone();
        return cloned.size();
    }

    @Benchmark
    public int benchmarkHashCode() {
        return readOnlyList.hashCode();
    }

    @Benchmark
    public boolean benchmarkEquals() {
        ObjectArrayList<String> cloned = readOnlyList.clone();
        return readOnlyList.equals(cloned);
    }

    @Benchmark
    public long benchmarkRamBytesAllocated() {
        return readOnlyList.ramBytesAllocated();
    }

    @Benchmark
    public long benchmarkRamBytesUsed() {
        return readOnlyList.ramBytesUsed();
    }

    @Benchmark
    public String benchmarkIterator() {
        Iterator<ObjectCursor<String>> it = readOnlyList.iterator();
        if (it.hasNext()) {
            return it.next().value;
        }
        return null;
    }

    @Benchmark
    public int benchmarkForEachProcedure() {
        ObjectProcedure<String> proc = new ObjectProcedure<String>() {
            @Override
            public void apply(String value) {
                // no‑op
            }
        };
        readOnlyList.forEach(proc);
        return readOnlyList.size();
    }

    @Benchmark
    public int benchmarkForEachPredicate() {
        ObjectPredicate<String> pred = new ObjectPredicate<String>() {
            @Override
            public boolean apply(String value) {
                return true;
            }
        };
        readOnlyList.forEach(pred);
        return readOnlyList.size();
    }
}
