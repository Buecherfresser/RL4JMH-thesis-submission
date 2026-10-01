package bench.generated.c000;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.bean.BeanCopy;
import java.util.Map;
import java.util.HashMap;
import java.util.function.Predicate;
import java.util.function.BiPredicate;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class BeanCopyBenchmark {

    // Simple POJO used for bean copying
    public static class Person {
        private int id;
        private String name;

        public Person() {
        }

        public Person(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    // Fixtures
    private Person sourceBean;
    private Person destBean;
    private Map<String, Object> sourceMap;
    private Map<String, Object> destMap;

    @Setup(Level.Trial)
    public void setUp() {
        sourceBean = new Person(42, "John Doe");
        destBean = new Person();

        sourceMap = new HashMap<>();
        sourceMap.put("id", 42);
        sourceMap.put("name", "John Doe");

        destMap = new HashMap<>();
    }

    @Benchmark
    public void copyPojoToPojoDefault(Blackhole bh) {
        BeanCopy.from(sourceBean).to(destBean).copy();
        bh.consume(destBean);
    }

    @Benchmark
    public void copyPojoToPojoDeclared(Blackhole bh) {
        BeanCopy.from(sourceBean).to(destBean).declared(true).copy();
        bh.consume(destBean);
    }

    @Benchmark
    public void copyPojoToPojoForced(Blackhole bh) {
        BeanCopy.from(sourceBean).to(destBean).forced(true).copy();
        bh.consume(destBean);
    }

    @Benchmark
    public void copyPojoToPojoIncludeFields(Blackhole bh) {
        BeanCopy.from(sourceBean).to(destBean).includeFields(true).copy();
        bh.consume(destBean);
    }

    @Benchmark
    public void copyPojoToPojoFilterPredicate(Blackhole bh) {
        Predicate<String> filter = name -> !name.equals("id");
        BeanCopy.from(sourceBean).to(destBean).filter(filter).copy();
        bh.consume(destBean);
    }

    @Benchmark
    public void copyPojoToPojoFilterBiPredicate(Blackhole bh) {
        BiPredicate<String, Object> filter = (name, value) -> !(value instanceof String);
        BeanCopy.from(sourceBean).to(destBean).filter(filter).copy();
        bh.consume(destBean);
    }

    @Benchmark
    public void copyPojoToMap(Blackhole bh) {
        BeanCopy.from(sourceBean).to(destMap).copy();
        bh.consume(destMap);
    }

    @Benchmark
    public void copyMapToPojo(Blackhole bh) {
        BeanCopy.from(sourceMap).to(destBean).copy();
        bh.consume(destBean);
    }

    @Benchmark
    public void copyMapToMap(Blackhole bh) {
        BeanCopy.from(sourceMap).to(destMap).copy();
        bh.consume(destMap);
    }
}
