package bench.generated.c085;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import jodd.util.Util;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class UtilBenchmark {

    // Since all methods are static and stateless, we don't need instance fields.
    // We rely on creating fresh, non-final objects inside the benchmark methods
    // to avoid the FINAL anti-pattern, accepting the overhead for testing static methods.

    @Benchmark
    public void benchmarkToString(Blackhole bh) {
        // Test null
        bh.consume(Util.toString(null));

        // Test String
        bh.consume(Util.toString("hello"));

        // Test Integer
        bh.consume(Util.toString(123));
    }

    @Benchmark
    public void benchmarkLength(Blackhole bh) {
        // Test null
        bh.consume(Util.length(null));

        // Test String (CharSequence)
        bh.consume(Util.length("test"));

        // Test Collection (List)
        bh.consume(Util.length(List.of(1, 2, 3)));

        // Test Map
        bh.consume(Util.length(new HashMap<>()));

        // Test Array
        bh.consume(Util.length(new int[]{1, 2, 3}));
    }

    @Benchmark
    public void benchmarkContainsElement(Blackhole bh) {
        // Test String containment
        bh.consume(Util.containsElement("hello world", "world"));

        // Test Collection containment
        bh.consume(Util.containsElement(List.of(1, 2, 3), 2));

        // Test Map containment (checking values)
        bh.consume(Util.containsElement(Map.of("a", 1, "b", 2), 2));

        // Test Array containment
        bh.consume(Util.containsElement(new int[]{1, 2, 3}, 3));

        // Test failure case
        bh.consume(Util.containsElement(List.of(1, 2), 99));
    }

    @Benchmark
    public void benchmarkToPrettyString(Blackhole bh) {
        // Test null
        bh.consume(Util.toPrettyString(null));

        // Test simple String
        bh.consume(Util.toPrettyString("simple string"));

        // Test Array (Primitive)
        bh.consume(Util.toPrettyString(new int[]{1, 2, 3}));

        // Test Array (Object)
        bh.consume(Util.toPrettyString(new Object[]{1, "two", true}));

        // Test Iterable (List)
        bh.consume(Util.toPrettyString(List.of("a", "b", "c")));
    }
}
