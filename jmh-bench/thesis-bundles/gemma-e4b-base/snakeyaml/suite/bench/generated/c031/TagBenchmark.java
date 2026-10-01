package bench.generated.c031;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.nodes.Tag;
import java.util.Set;
import java.util.HashSet;
import java.math.BigDecimal;
import java.util.Date;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class TagBenchmark {

    private Tag standardTag;
    private Tag customStringTag;
    private Tag customClassTag;
    private Tag secondaryTag;
    private Tag standardTagForCompatibility;
    private Class<?> compatibleClass;
    private Class<?> incompatibleClass;

    @Setup(Level.Trial)
    public void setup() {
        // 1. Standard Tag (YAML)
        standardTag = Tag.YAML;

        // 2. Custom Tag created from String
        String customTagValue = "tag:my.custom.type";
        customStringTag = new Tag(customTagValue);

        // 3. Custom Tag created from Class
        // Use a simple class name for testing
        try {
            Class<?> testClass = Class.forName("java.lang.String");
            customClassTag = new Tag(testClass);
        } catch (ClassNotFoundException e) {
            // Should not happen for java.lang.String
            throw new RuntimeException(e);
        }

        // 4. Secondary Tag (does not start with PREFIX)
        secondaryTag = new Tag("my.secondary.tag");

        // 5. Tag for compatibility checks (FLOAT)
        standardTagForCompatibility = Tag.FLOAT;
        compatibleClass = Double.class;
        incompatibleClass = Integer.class;
    }

    @Benchmark
    public void testGetValue(Blackhole bh) {
        bh.consume(standardTag.getValue());
    }

    @Benchmark
    public void testToString(Blackhole bh) {
        bh.consume(standardTag.toString());
    }

    @Benchmark
    public void testEqualsStandard(Blackhole bh) {
        bh.consume(standardTag.equals(standardTag));
    }

    @Benchmark
    public void testEqualsDifferent(Blackhole bh) {
        bh.consume(standardTag.equals(customStringTag));
    }

    @Benchmark
    public void testHashCode(Blackhole bh) {
        bh.consume(standardTag.hashCode());
    }

    @Benchmark
    public void testStartsWith(Blackhole bh) {
        // Check if standard tag starts with its own prefix
        bh.consume(standardTag.startsWith(Tag.PREFIX));
    }

    @Benchmark
    public void testStartsWithCustom(Blackhole bh) {
        // Check if custom tag starts with a specific substring
        bh.consume(customStringTag.startsWith("tag:my"));
    }

    @Benchmark
    public void testIsSecondary(Blackhole bh) {
        bh.consume(standardTag.isSecondary());
        bh.consume(secondaryTag.isSecondary());
    }

    @Benchmark
    public void testGetClassNameStandard(Blackhole bh) {
        // Should succeed for standard tag
        bh.consume(standardTag.getClassName());
    }

    @Benchmark
    public void testGetClassNameSecondary(Blackhole bh) {
        // Should throw YAMLException for secondary tag
        try {
            bh.consume(secondaryTag.getClassName());
        } catch (org.yaml.snakeyaml.error.YAMLException e) {
            // Expected exception
        }
    }

    @Benchmark
    public void testIsCompatible(Blackhole bh) {
        // Check compatibility for FLOAT tag
        bh.consume(standardTagForCompatibility.isCompatible(compatibleClass));
    }

    @Benchmark
    public void testIsCompatibleIncompatible(Blackhole bh) {
        // Check incompatibility
        bh.consume(standardTagForCompatibility.isCompatible(incompatibleClass));
    }

    @Benchmark
    public void testMatchesStandard(Blackhole bh) {
        // Check if standard tag matches its own type (YAML)
        bh.consume(standardTag.matches(java.util.Map.class));
    }

    @Benchmark
    public void testMatchesCustom(Blackhole bh) {
        // Check if custom class tag matches its class
        bh.consume(customClassTag.matches(java.lang.String.class));
    }

    @Benchmark
    public void testIsCustomGlobalStandard(Blackhole bh) {
        // Standard tags should not be custom global
        bh.consume(!standardTag.isCustomGlobal());
    }

    @Benchmark
    public void testIsCustomGlobalCustom(Blackhole bh) {
        // A custom tag not in standardTags should be custom global
        bh.consume(customStringTag.isCustomGlobal());
    }
}
