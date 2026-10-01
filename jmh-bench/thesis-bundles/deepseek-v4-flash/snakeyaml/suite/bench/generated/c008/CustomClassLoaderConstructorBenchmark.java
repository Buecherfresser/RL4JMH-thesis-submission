package bench.generated.c008;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.CustomClassLoaderConstructor;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.inspector.UnTrustedTagInspector;
import java.util.List;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CustomClassLoaderConstructorBenchmark {

    public static class Person {
        public String name;
        public int age;
    }

    private Yaml yaml;
    private String personYaml;
    private String listYaml;

    @Setup(Level.Trial)
    public void setup() {
        LoaderOptions options = new LoaderOptions();
        options.setTagInspector(new UnTrustedTagInspector());
        CustomClassLoaderConstructor constructor = new CustomClassLoaderConstructor(
                CustomClassLoaderConstructorBenchmark.class.getClassLoader(), options);
        yaml = new Yaml(constructor);

        personYaml = "!bench.generated.CustomClassLoaderConstructorBenchmark$Person\n" +
                     "name: John\n" +
                     "age: 30\n";
        listYaml = "- !bench.generated.CustomClassLoaderConstructorBenchmark$Person\n" +
                   "  name: Alice\n" +
                   "  age: 25\n" +
                   "- !bench.generated.CustomClassLoaderConstructorBenchmark$Person\n" +
                   "  name: Bob\n" +
                   "  age: 35\n";
    }

    @Benchmark
    public Person loadPerson() {
        return yaml.load(personYaml);
    }

    @Benchmark
    public List<Person> loadPersonList() {
        return yaml.load(listYaml);
    }
}
