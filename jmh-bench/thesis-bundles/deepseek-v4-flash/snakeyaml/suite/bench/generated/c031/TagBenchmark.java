package bench.generated.c031;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.error.YAMLException;

import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class TagBenchmark {

    @State(Scope.Benchmark)
    public static class BenchmarkState {
        Tag standardTag;
        Tag secondaryTag;
        Tag customTag;
        Tag intTag;
        Tag floatTag;
        Tag timestampTag;
        Tag commentTag;
        Tag sameAsStandardTag;
        Tag classTag;
        Class<?> intClass;
        Class<?> floatClass;
        Class<?> dateClass;
        Class<?> stringClass;

        @Setup(Level.Trial)
        public void setup() {
            standardTag = new Tag("tag:yaml.org,2002:str");
            secondaryTag = new Tag("!custom");
            customTag = new Tag("tag:example.com:custom");
            intTag = Tag.INT;
            floatTag = Tag.FLOAT;
            timestampTag = Tag.TIMESTAMP;
            commentTag = Tag.COMMENT;
            sameAsStandardTag = new Tag("tag:yaml.org,2002:str");
            classTag = new Tag(String.class);
            intClass = Integer.class;
            floatClass = Float.class;
            dateClass = java.util.Date.class;
            stringClass = String.class;
        }
    }

    @Benchmark
    public Tag constructorWithString() {
        return new Tag("tag:yaml.org,2002:str");
    }

    @Benchmark
    public Tag constructorWithClass() {
        return new Tag(String.class);
    }

    @Benchmark
    public String getValue(BenchmarkState state) {
        return state.standardTag.getValue();
    }

    @Benchmark
    public boolean isSecondaryOnPrimary(BenchmarkState state) {
        return state.standardTag.isSecondary();
    }

    @Benchmark
    public boolean isSecondaryOnSecondary(BenchmarkState state) {
        return state.secondaryTag.isSecondary();
    }

    @Benchmark
    public boolean startsWith(BenchmarkState state) {
        return state.standardTag.startsWith("tag:yaml");
    }

    @Benchmark
    public String getClassName(BenchmarkState state) {
        return state.standardTag.getClassName();
    }

    @Benchmark
    public boolean equalsTrue(BenchmarkState state) {
        return state.standardTag.equals(state.sameAsStandardTag);
    }

    @Benchmark
    public boolean equalsFalse(BenchmarkState state) {
        return state.standardTag.equals(state.secondaryTag);
    }

    @Benchmark
    public int hashCode(BenchmarkState state) {
        return state.standardTag.hashCode();
    }

    @Benchmark
    public boolean isCompatibleInt(BenchmarkState state) {
        return state.intTag.isCompatible(state.intClass);
    }

    @Benchmark
    public boolean isCompatibleFloat(BenchmarkState state) {
        return state.floatTag.isCompatible(state.floatClass);
    }

    @Benchmark
    public boolean isCompatibleTimestamp(BenchmarkState state) {
        return state.timestampTag.isCompatible(state.dateClass);
    }

    @Benchmark
    public boolean isCompatibleNonExistent(BenchmarkState state) {
        return state.standardTag.isCompatible(state.stringClass);
    }

    @Benchmark
    public boolean matchesTrue(BenchmarkState state) {
        return state.classTag.matches(String.class);
    }

    @Benchmark
    public boolean matchesFalse(BenchmarkState state) {
        return state.classTag.matches(Integer.class);
    }

    @Benchmark
    public boolean isCustomGlobalOnStandard(BenchmarkState state) {
        return state.standardTag.isCustomGlobal();
    }

    @Benchmark
    public boolean isCustomGlobalOnCustom(BenchmarkState state) {
        return state.customTag.isCustomGlobal();
    }

    @Benchmark
    public boolean isCustomGlobalOnSecondary(BenchmarkState state) {
        return state.secondaryTag.isCustomGlobal();
    }

    @Benchmark
    public String toString(BenchmarkState state) {
        return state.standardTag.toString();
    }
}
