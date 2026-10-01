package bench.generated.c026;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import com.carrotsearch.hppc.IntArrayList;
import com.carrotsearch.hppc.IntContainer;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class IntArrayListBenchmark {

    private IntArrayList list;
    private int[] initialData;
    private final int INITIAL_SIZE = 10000;
    private final int MAX_ADDITIONS = 5000;
    private final Random random = new Random(42);

    @Setup
    public void setup() {
        // 1. Create initial data
        initialData = new int[INITIAL_SIZE];
        for (int i = 0; i < INITIAL_SIZE; i++) {
            initialData[i] = random.nextInt(100000);
        }

        // 2. Initialize the list using the array data
        list = IntArrayList.from(initialData);
    }

    // --- ADD OPERATIONS ---

    @Benchmark
    public void addSingleElement(Blackhole bh) {
        int value = random.nextInt(100000);
        list.add(value);
        bh.consume(list.size());
    }

    @Benchmark
    public void addTwoElements(Blackhole bh) {
        int v1 = random.nextInt(100000);
        int v2 = random.nextInt(100000);
        list.add(v1, v2);
        bh.consume(list.size());
    }

    @Benchmark
    public void addBulkElements(Blackhole bh) {
        int[] elements = new int[MAX_ADDITIONS];
        for (int i = 0; i < MAX_ADDITIONS; i++) {
            elements[i] = random.nextInt(100000);
        }
        list.add(elements);
        bh.consume(list.size());
    }

    @Benchmark
    public void addAllFromContainer(Blackhole bh) {
        // Create a temporary container from a list of random ints
        List<Integer> temp = new ArrayList<>();
        for (int i = 0; i < INITIAL_SIZE / 10; i++) {
            temp.add(random.nextInt(100000));
        }
        // FIX: Use IntArrayList.from since IntArrayList implements IntContainer
        IntContainer container = IntArrayList.from(temp.stream().mapToInt(i -> i).toArray());
        
        int addedCount = list.addAll(container);
        bh.consume(addedCount);
    }

    // --- ACCESS OPERATIONS ---

    @Benchmark
    public void getElement(Blackhole bh) {
        int index = list.size() / 2;
        int value = list.get(index);
        bh.consume(value);
    }

    @Benchmark
    public void setElement(Blackhole bh) {
        int index = list.size() / 4;
        int newValue = random.nextInt(1000000);
        int oldValue = list.set(index, newValue);
        bh.consume(oldValue);
    }

    @Benchmark
    public void containsElement(Blackhole bh) {
        int valueToFind = list.get(list.size() / 2);
        boolean found = list.contains(valueToFind);
        bh.consume(found);
    }

    @Benchmark
    public void indexOfElement(Blackhole bh) {
        int valueToFind = list.get(list.size() / 4);
        int index = list.indexOf(valueToFind);
        bh.consume(index);
    }

    @Benchmark
    public void lastIndexOfElement(Blackhole bh) {
        int valueToFind = list.get(list.size() / 2);
        int index = list.lastIndexOf(valueToFind);
        bh.consume(index);
    }

    // --- REMOVAL OPERATIONS ---

    @Benchmark
    public void removeAtMiddle(Blackhole bh) {
        int index = list.size() / 2;
        int removedValue = list.removeAt(index);
        bh.consume(removedValue);
        bh.consume(list.size());
    }

    @Benchmark
    public void removeLast(Blackhole bh) {
        int removedValue = list.removeLast();
        bh.consume(removedValue);
        bh.consume(list.size());
    }

    @Benchmark
    public void removeElement(Blackhole bh) {
        int valueToRemove = list.get(list.size() / 3);
        boolean removed = list.removeElement(valueToRemove);
        bh.consume(removed);
    }

    @Benchmark
    public void removeAllElement(Blackhole bh) {
        int valueToRemove = list.get(list.size() / 4);
        int removedCount = list.removeAll(valueToRemove);
        bh.consume(removedCount);
        bh.consume(list.size());
    }

    @Benchmark
    public void removeRange(Blackhole bh) {
        int fromIndex = list.size() / 4;
        int toIndex = list.size() / 2;
        list.removeRange(fromIndex, toIndex);
        bh.consume(list.size());
    }

    // --- STRUCTURAL & UTILITY OPERATIONS ---

    @Benchmark
    public void checkSize(Blackhole bh) {
        bh.consume(list.size());
    }

    @Benchmark
    public void checkIsEmpty(Blackhole bh) {
        boolean empty = list.isEmpty();
        bh.consume(empty);
    }

    @Benchmark
    public void clearList(Blackhole bh) {
        list.clear();
        bh.consume(list.size());
    }

    @Benchmark
    public void releaseList(Blackhole bh) {
        list.release();
        bh.consume(list.size());
    }

    @Benchmark
    public void toArray(Blackhole bh) {
        int[] array = list.toArray();
        bh.consume(array.length);
    }

    @Benchmark
    public void sortList(Blackhole bh) {
        // Sort modifies the list in place
        list.sort();
        bh.consume(list.size());
    }

    @Benchmark
    public void reverseList(Blackhole bh) {
        // Reverse modifies the list in place
        list.reverse();
        bh.consume(list.size());
    }

    @Benchmark
    public void cloneList(Blackhole bh) {
        IntArrayList cloned = list.clone();
        bh.consume(cloned.size());
    }
}
