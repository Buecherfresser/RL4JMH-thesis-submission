package bench.generated.c006;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.CharArrayList;
import com.carrotsearch.hppc.CharIndexedContainer;
import com.carrotsearch.hppc.CharContainer;
import com.carrotsearch.hppc.cursors.CharCursor;
import com.carrotsearch.hppc.predicates.CharPredicate;
import java.util.Arrays;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CharArrayListBenchmark {

    private CharArrayList list;
    private char[] initialData;
    private final int INITIAL_SIZE = 1000;
    private final char TEST_CHAR = 'A';
    private final char DIFFERENT_CHAR = 'B';

    @Setup(Level.Invocation)
    public void setup() {
        // Build initial data array
        initialData = new char[INITIAL_SIZE];
        for (int i = 0; i < INITIAL_SIZE; i++) {
            initialData[i] = (char) ('a' + (i % 26));
        }

        // Create a fresh list instance for each invocation
        list = CharArrayList.from(initialData);
    }

    // --- Read Operations ---

    @Benchmark
    public char getElement() {
        // Read operation, index 500
        return list.get(500);
    }

    @Benchmark
    public int getSize() {
        return list.size();
    }

    @Benchmark
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Benchmark
    public boolean containsElement() {
        return list.contains(TEST_CHAR);
    }

    @Benchmark
    public int indexOfElement() {
        return list.indexOf(TEST_CHAR);
    }

    @Benchmark
    public int lastIndexOfElement() {
        return list.lastIndexOf(TEST_CHAR);
    }

    // --- Write/Mutation Operations ---

    @Benchmark
    public void addSingleElement() {
        // Add one element to the end
        list.add(TEST_CHAR);
    }

    @Benchmark
    public void addTwoElements() {
        // Add two elements to the end
        list.add(TEST_CHAR, DIFFERENT_CHAR);
    }

    @Benchmark
    public void addArrayRange() {
        // Add a range of elements from the initial data
        char[] temp = new char[10];
        Arrays.fill(temp, 'Z');
        list.add(temp, 0, 10);
    }

    @Benchmark
    public void insertElement() {
        // Insert an element at the beginning
        list.insert(0, TEST_CHAR);
    }

    @Benchmark
    public char setElement() {
        // Set an element in the middle
        return list.set(500, DIFFERENT_CHAR);
    }

    @Benchmark
    public char removeAtElement() {
        // Remove an element from the middle
        return list.removeAt(500);
    }

    @Benchmark
    public char removeLastElement() {
        // Remove the last element
        return list.removeLast();
    }

    @Benchmark
    public void removeRange() {
        // Remove a range of elements
        list.removeRange(100, 200);
    }

    @Benchmark
    public int removeAllSingleElement() {
        // Remove all occurrences of a single element
        return list.removeAll(TEST_CHAR);
    }

    @Benchmark
    public int removeAllByPredicate() {
        // Define a predicate that removes elements greater than 'm'
        CharPredicate predicate = c -> c <= 'm';
        return list.removeAll(predicate);
    }

    // --- Utility Operations ---

    @Benchmark
    public void trimToSize() {
        // Truncate the internal buffer
        list.trimToSize();
    }

    @Benchmark
    public void clearList() {
        // Clear the list content
        list.clear();
    }

    @Benchmark
    public void releaseList() {
        // Release internal resources
        list.release();
    }

    @Benchmark
    public void resizeList() {
        // Resize the list to a larger size
        list.resize(INITIAL_SIZE * 2);
    }

    @Benchmark
    public char[] toArray() {
        // Convert list to array
        return list.toArray();
    }

    @Benchmark
    public CharIndexedContainer sortList() {
        // Sort the list in place
        return list.sort();
    }

    @Benchmark
    public CharIndexedContainer reverseList() {
        // Reverse the list in place
        return list.reverse();
    }

    @Benchmark
    public CharArrayList cloneList() {
        // Clone the list
        return list.clone();
    }
}
