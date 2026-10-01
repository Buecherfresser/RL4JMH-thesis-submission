package bench.generated.c000;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.Map;
import java.util.HashMap;
import java.util.function.Predicate;
import java.util.function.BiPredicate;
import java.util.concurrent.TimeUnit;
import jodd.bean.BeanCopy;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class BeanCopyBenchmark {

    private Map<String, Object> sourceMap;
    private Map<String, Object> destinationMap;

    // Pre-configured BeanCopy instances for different scenarios
    private BeanCopy defaultCopy;
    private BeanCopy declaredCopy;
    private BeanCopy forcedCopy;
    private BeanCopy filteredByNameCopy;
    private BeanCopy filteredByValueCopy;
    private BeanCopy includeFieldsCopy;

    @Setup(Level.Trial)
    public void setup() {
        // 1. Setup Source Data (Map)
        sourceMap = new HashMap<>();
        sourceMap.put("name", "Alice");
        sourceMap.put("age", 30);
        sourceMap.put("city", "New York");
        sourceMap.put("active", true);
        sourceMap.put("score", 99.5);
        sourceMap.put("secret", "hidden");

        // 2. Setup Destination Data (Map)
        destinationMap = new HashMap<>();

        // 3. Setup BeanCopy instances

        // Default (public access, no filters, no field inclusion)
        defaultCopy = BeanCopy.from(sourceMap).to(destinationMap);

        // Declared access
        declaredCopy = BeanCopy.from(sourceMap).to(destinationMap).declared(true);

        // Forced access
        forcedCopy = BeanCopy.from(sourceMap).to(destinationMap).forced(true);

        // Filtered by name (only copy fields starting with 'a')
        Predicate<String> nameFilter = name -> name.startsWith("a");
        filteredByNameCopy = BeanCopy.from(sourceMap).to(destinationMap).filter(nameFilter);

        // Filtered by value (only copy fields where value is true)
        BiPredicate<String, Object> valueFilter = (name, value) -> value instanceof Boolean && (Boolean) value;
        filteredByValueCopy = BeanCopy.from(sourceMap).to(destinationMap).filter(valueFilter);

        // Include Fields (copy all fields regardless of visibility/default rules)
        includeFieldsCopy = BeanCopy.from(sourceMap).to(destinationMap).includeFields(true);
    }

    /**
     * Resets the destination map before each invocation to ensure a clean state.
     */
    @Setup(Level.Invocation)
    public void resetDestination() {
        destinationMap.clear();
    }

    @Benchmark
    public void copy_DefaultAccess(Blackhole bh) {
        // The copy operation mutates destinationMap
        defaultCopy.copy();
        bh.consume(destinationMap);
    }

    @Benchmark
    public void copy_DeclaredAccess(Blackhole bh) {
        declaredCopy.copy();
        bh.consume(destinationMap);
    }

    @Benchmark
    public void copy_ForcedAccess(Blackhole bh) {
        forcedCopy.copy();
        bh.consume(destinationMap);
    }

    @Benchmark
    public void copy_FilteredByName(Blackhole bh) {
        filteredByNameCopy.copy();
        bh.consume(destinationMap);
    }

    @Benchmark
    public void copy_FilteredByValue(Blackhole bh) {
        filteredByValueCopy.copy();
        bh.consume(destinationMap);
    }

    @Benchmark
    public void copy_IncludeFields(Blackhole bh) {
        includeFieldsCopy.copy();
        bh.consume(destinationMap);
    }
}
