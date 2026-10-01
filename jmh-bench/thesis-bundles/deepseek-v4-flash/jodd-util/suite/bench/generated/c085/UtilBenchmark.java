package bench.generated.c085;

import jodd.util.Util;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.*;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class UtilBenchmark {

    // Shared immutable inputs (read-only for most methods)
    private String str = "Hello, World!";
    private List<String> list = Arrays.asList("a", "b", "c");
    private Map<String, String> map = new HashMap<>();
    private int[] intArray = {1, 2, 3, 4, 5};
    private String[] stringArray = {"a", "b", "c"};
    private Object[] objectArray = {1, "two", 3.0};

    @Setup
    public void setup() {
        map.put("key1", "value1");
        map.put("key2", "value2");
    }

    // ------------------------------------------------------------------ toString

    @Benchmark
    public String toStringNotNull() {
        return Util.toString(str);
    }

    @Benchmark
    public String toStringNull() {
        return Util.toString(null);
    }

    // ------------------------------------------------------------------ length

    @Benchmark
    public int lengthString() {
        return Util.length(str);
    }

    @Benchmark
    public int lengthList() {
        return Util.length(list);
    }

    @Benchmark
    public int lengthMap() {
        return Util.length(map);
    }

    @Benchmark
    public int lengthIntArray() {
        return Util.length(intArray);
    }

    // Iterator and Enumeration are consumed, so we need fresh instances per invocation
    @State(Scope.Thread)
    public static class IteratorState {
        private Iterator<String> iterator;
        private Enumeration<String> enumeration;

        @Setup(Level.Invocation)
        public void setup() {
            iterator = Arrays.asList("a", "b", "c").iterator();
            enumeration = Collections.enumeration(Arrays.asList("a", "b", "c"));
        }
    }

    @Benchmark
    public int lengthIterator(IteratorState state) {
        return Util.length(state.iterator);
    }

    @Benchmark
    public int lengthEnumeration(IteratorState state) {
        return Util.length(state.enumeration);
    }

    // ---------------------------------------------------------- containsElement

    @Benchmark
    public boolean containsElementString() {
        return Util.containsElement(str, "World");
    }

    @Benchmark
    public boolean containsElementList() {
        return Util.containsElement(list, "b");
    }

    @Benchmark
    public boolean containsElementMap() {
        return Util.containsElement(map, "value1");
    }

    @Benchmark
    public boolean containsElementIntArray() {
        return Util.containsElement(intArray, 3);
    }

    @Benchmark
    public boolean containsElementIterator(IteratorState state) {
        return Util.containsElement(state.iterator, "b");
    }

    @Benchmark
    public boolean containsElementEnumeration(IteratorState state) {
        return Util.containsElement(state.enumeration, "b");
    }

    // ------------------------------------------------------------ toPrettyString

    @Benchmark
    public String toPrettyStringIntArray() {
        return Util.toPrettyString(intArray);
    }

    @Benchmark
    public String toPrettyStringObjectArray() {
        return Util.toPrettyString(objectArray);
    }

    @Benchmark
    public String toPrettyStringIterable() {
        return Util.toPrettyString(list);
    }
}
