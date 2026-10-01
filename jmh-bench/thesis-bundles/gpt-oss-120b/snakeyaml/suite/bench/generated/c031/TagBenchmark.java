package bench.generated.c031;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.nodes.Tag;
import java.util.Date;
import java.lang.Integer;
import java.lang.Double;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class TagBenchmark {

    private Tag yamlTag;
    private Tag intTag;
    private Tag floatTag;
    private Tag timestampTag;
    private Tag customGlobalTag;
    private Tag secondaryTag;
    private Tag primaryTag;
    private Tag matchingTag;
    private String simpleTagString;
    private String testPrefix;
    private Class<Integer> intClass;
    private Class<Double> doubleClass;
    private Class<Date> dateClass;

    @Setup(Level.Trial)
    public void setUp() {
        yamlTag = new Tag(Tag.PREFIX + "yaml");
        intTag = Tag.INT;
        floatTag = Tag.FLOAT;
        timestampTag = Tag.TIMESTAMP;
        customGlobalTag = new Tag(Tag.PREFIX + "mycustom");
        secondaryTag = new Tag("!my!secondary");
        primaryTag = new Tag(Tag.PREFIX + "myprimary");
        matchingTag = new Tag(Tag.PREFIX + String.class.getName());
        simpleTagString = Tag.PREFIX + "simple";
        testPrefix = Tag.PREFIX;
        intClass = Integer.class;
        doubleClass = Double.class;
        dateClass = Date.class;
    }

    @Benchmark
    public Tag benchmarkTagConstructorFromString() {
        return new Tag(simpleTagString);
    }

    @Benchmark
    public Tag benchmarkTagConstructorFromClass() {
        return new Tag(Integer.class);
    }

    @Benchmark
    public boolean benchmarkIsSecondary() {
        return secondaryTag.isSecondary();
    }

    @Benchmark
    public String benchmarkGetValue() {
        return primaryTag.getValue();
    }

    @Benchmark
    public boolean benchmarkStartsWith() {
        return primaryTag.startsWith(testPrefix);
    }

    @Benchmark
    public String benchmarkGetClassName() {
        return intTag.getClassName();
    }

    @Benchmark
    public String benchmarkToString() {
        return primaryTag.toString();
    }

    @Benchmark
    public boolean benchmarkEquals() {
        return primaryTag.equals(customGlobalTag);
    }

    @Benchmark
    public int benchmarkHashCode() {
        return primaryTag.hashCode();
    }

    @Benchmark
    public boolean benchmarkIsCompatibleFloat() {
        return floatTag.isCompatible(doubleClass);
    }

    @Benchmark
    public boolean benchmarkIsCompatibleInt() {
        return intTag.isCompatible(intClass);
    }

    @Benchmark
    public boolean benchmarkIsCompatibleTimestamp() {
        return timestampTag.isCompatible(dateClass);
    }

    @Benchmark
    public boolean benchmarkMatches() {
        return matchingTag.matches(String.class);
    }

    @Benchmark
    public boolean benchmarkIsCustomGlobal() {
        return customGlobalTag.isCustomGlobal();
    }
}
