package bench.generated.c031;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.Date;
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
    private Tag standardYamlTag;
    private Tag customTag;
    private Tag classBasedTag;
    private Class<?> integerClass;
    private Class<?> dateClass;
    private Class<?> doubleClass;

    // --- Setup ---
    @Setup
    public void setup() {
        // 1. Standard Tag setup (Static final instance)
        this.standardYamlTag = Tag.YAML;

        // 2. Custom Tag setup (String based)
        String customTagString = "tag:mycustom";
        this.customTag = new Tag(customTagString);

        // 3. Class-based Tag setup
        this.integerClass = Integer.class;
        this.dateClass = Date.class;
        this.doubleClass = Double.class;
        this.classBasedTag = new Tag(integerClass);
    }

    // --- Benchmarks for String/Value Operations ---

    @Benchmark
    public String benchmarkGetValue(Blackhole bh) {
        return standardYamlTag.getValue();
    }

    @Benchmark
    public String benchmarkGetValueCustom(Blackhole bh) {
        return customTag.getValue();
    }

    @Benchmark
    public boolean benchmarkIsSecondary(Blackhole bh) {
        return customTag.isSecondary();
    }

    @Benchmark
    public boolean benchmarkStartsWith(Blackhole bh) {
        return standardYamlTag.startsWith("tag:yaml.org,2002:");
    }

    // --- Benchmarks for Class/Compatibility Operations ---

    @Benchmark
    public String benchmarkGetClassName(Blackhole bh) {
        // This method relies on the internal logic of Tag.getClassName(), which decodes the value.
        return classBasedTag.getClassName();
    }

    @Benchmark
    public boolean benchmarkIsCompatibleInteger(Blackhole bh) {
        return classBasedTag.isCompatible(integerClass);
    }

    @Benchmark
    public boolean benchmarkIsCompatibleDouble(Blackhole bh) {
        return classBasedTag.isCompatible(doubleClass);
    }

    @Benchmark
    public boolean benchmarkMatchesClass(Blackhole bh) {
        // Check if the class-based tag matches the class it was created from.
        return classBasedTag.matches(integerClass);
    }

    @Benchmark
    public boolean benchmarkMatchesNonMatchingClass(Blackhole bh) {
        // Check if the class-based tag matches a different class.
        return classBasedTag.matches(dateClass);
    }
}
