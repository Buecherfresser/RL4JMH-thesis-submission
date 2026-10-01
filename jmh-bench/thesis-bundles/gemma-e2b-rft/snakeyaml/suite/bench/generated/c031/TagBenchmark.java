package bench.generated.c031;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.nodes.Tag;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class TagBenchmark {

    // --- State Fields ---
    private Tag stringTag;
    private Tag classTag;
    private Tag compatibleTag;
    private Tag incompatibleTag;
    private Class<?> compatibleClass;
    private Class<?> incompatibleClass;

    // --- Setup ---
    @Setup
    public void setup() {
        // 1. Setup String Tag
        String inputTagString = "test_tag";
        this.stringTag = new Tag(inputTagString);

        // 2. Setup Class Tag
        Class<?> inputClass = String.class;
        this.classTag = new Tag(inputClass);

        // 3. Setup Compatible/Incompatible Tags for isCompatible check
        this.compatibleTag = Tag.FLOAT;
        this.compatibleClass = Double.class;

        this.incompatibleTag = Tag.MAP;
        this.incompatibleClass = String.class;
    }

    // --- Benchmarks for Construction ---

    @Benchmark
    public void constructTagFromString(Blackhole bh) {
        Tag t = new Tag("new_string_tag");
        bh.consume(t);
    }

    @Benchmark
    public void constructTagFromClass(Blackhole bh) {
        Class<?> clazz = Integer.class;
        Tag t = new Tag(clazz);
        bh.consume(t);
    }

    // --- Benchmarks for Value Retrieval ---

    @Benchmark
    public String getValue(Blackhole bh) {
        return stringTag.getValue();
    }

    // --- Benchmarks for String Manipulation ---

    @Benchmark
    public boolean startsWith(Blackhole bh) {
        return stringTag.startsWith("test");
    }

    @Benchmark
    public String getClassName(Blackhole bh) {
        return classTag.getClassName();
    }

    // --- Benchmarks for Compatibility Checks ---

    @Benchmark
    public boolean isCompatible(Blackhole bh) {
        return compatibleTag.isCompatible(compatibleClass);
    }

    @Benchmark
    public boolean isCompatible_False(Blackhole bh) {
        return incompatibleTag.isCompatible(incompatibleClass);
    }

    // --- Benchmarks for Global/Custom Checks ---

    @Benchmark
    public boolean matches(Blackhole bh) {
        // Test if Tag.YAML matches a specific Class, as Tag.matches expects a Class argument.
        return Tag.YAML.matches(String.class);
    }

    @Benchmark
    public boolean isCustomGlobal(Blackhole bh) {
        // Test a standard tag (YAML) - should be false
        return Tag.YAML.isCustomGlobal();
    }
}
