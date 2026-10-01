package bench.generated.c005;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import com.carrotsearch.hppc.CharArrayDeque;
import com.carrotsearch.hppc.cursors.CharCursor;
import com.carrotsearch.hppc.predicates.CharPredicate;
import com.carrotsearch.hppc.procedures.CharProcedure;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class CharArrayDequeBenchmark {

    private CharArrayDeque deque;
    private char[] testData;
    private final char[] testChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();

    @Setup
    public void setup() {
        // Initialize a large deque for testing
        int initialSize = 1024;
        testData = new char[initialSize];
        for (int i = 0; i < initialSize; i++) {
            testData[i] = testChars[i % testChars.length];
        }
        deque = CharArrayDeque.from(testData);
    }

    // --- Basic Mutating Operations ---

    @Benchmark
    public void addFirst_SingleChar(Blackhole bh) {
        char c = testChars[0];
        deque.addFirst(c);
        bh.consume(deque);
    }

    @Benchmark
    public void addLast_SingleChar(Blackhole bh) {
        char c = testChars[0];
        deque.addLast(c);
        bh.consume(deque);
    }

    @Benchmark
    public void removeFirst_Single(Blackhole bh) {
        bh.consume(deque.removeFirst());
    }

    @Benchmark
    public void removeLast_Single(Blackhole bh) {
        bh.consume(deque.removeLast());
    }

    @Benchmark
    public void contains_Present(Blackhole bh) {
        char presentChar = testChars[50];
        bh.consume(deque.contains(presentChar));
    }

    @Benchmark
    public void contains_Absent(Blackhole bh) {
        char absentChar = 'z';
        bh.consume(deque.contains(absentChar));
    }

    @Benchmark
    public void removeFirst_ByValue(Blackhole bh) {
        char charToRemove = testChars[10];
        bh.consume(deque.removeFirst(charToRemove));
    }

    @Benchmark
    public void removeLast_ByValue(Blackhole bh) {
        char charToRemove = testChars[100];
        bh.consume(deque.removeLast(charToRemove));
    }

    @Benchmark
    public void removeAll_ByValue(Blackhole bh) {
        char charToRemove = testChars[5];
        bh.consume(deque.removeAll(charToRemove));
    }

    // --- Bulk Mutating Operations ---

    @Benchmark
    public void addFirst_Bulk(Blackhole bh) {
        char[] elements = new char[100];
        for (int i = 0; i < elements.length; i++) {
            elements[i] = testChars[i % testChars.length];
        }
        deque.addFirst(elements);
        bh.consume(deque);
    }

    @Benchmark
    public void addLast_Bulk(Blackhole bh) {
        char[] elements = new char[100];
        for (int i = 0; i < elements.length; i++) {
            elements[i] = testChars[i % testChars.length];
        }
        deque.addLast(elements);
        bh.consume(deque);
    }

    // --- State and Utility Operations ---

    @Benchmark
    public void size(Blackhole bh) {
        bh.consume(deque.size());
    }

    @Benchmark
    public void isEmpty(Blackhole bh) {
        bh.consume(deque.isEmpty());
    }

    @Benchmark
    public void clear(Blackhole bh) {
        deque.clear();
        bh.consume(deque);
    }

    @Benchmark
    public void release(Blackhole bh) {
        deque.release();
        bh.consume(deque);
    }

    @Benchmark
    public void ensureCapacity(Blackhole bh) {
        // Ensure capacity for 10% growth
        deque.ensureCapacity(100);
        bh.consume(deque);
    }

    @Benchmark
    public void clone(Blackhole bh) {
        CharArrayDeque cloned = deque.clone();
        bh.consume(cloned);
    }

    @Benchmark
    public void toArray(Blackhole bh) {
        char[] result = deque.toArray(new char[deque.size()]);
        bh.consume(result);
    }

    @Benchmark
    public void hashCode(Blackhole bh) {
        bh.consume(deque.hashCode());
    }

    @Benchmark
    public void equals(Blackhole bh) {
        // Test against itself
        bh.consume(deque.equals(deque));
    }

    // --- Iteration Operations ---

    @Benchmark
    public void iterator_Forward(Blackhole bh) {
        Iterator<CharCursor> it = deque.iterator();
        // Consume the iterator to ensure it's used
        while (it.hasNext()) {
            bh.consume(it.next());
        }
    }

    @Benchmark
    public void descendingIterator_Forward(Blackhole bh) {
        Iterator<CharCursor> it = deque.descendingIterator();
        // Consume the iterator to ensure it's used
        while (it.hasNext()) {
            bh.consume(it.next());
        }
    }

    @Benchmark
    public void forEach_Procedure(Blackhole bh) {
        CharProcedure procedure = c -> {
            // Simulate some work
        };
        deque.forEach(procedure);
    }

    @Benchmark
    public void descendingForEach_Procedure(Blackhole bh) {
        CharProcedure procedure = c -> {
            // Simulate some work
        };
        deque.descendingForEach(procedure);
    }

    @Benchmark
    public void forEach_Predicate(Blackhole bh) {
        CharPredicate predicate = c -> c == 'a';
        deque.forEach(predicate);
    }

    @Benchmark
    public void descendingForEach_Predicate(Blackhole bh) {
        CharPredicate predicate = c -> c == 'z';
        deque.descendingForEach(predicate);
    }
}
