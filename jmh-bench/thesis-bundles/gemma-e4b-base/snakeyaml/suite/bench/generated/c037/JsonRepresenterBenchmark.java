package bench.generated.c037;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.representer.JsonRepresenter;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.nodes.Node;
import java.util.Date;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class JsonRepresenterBenchmark {

    private JsonRepresenter jsonRepresenter;
    private TestObject testObject;
    private Date testDate;
    private byte[] testByteArray;

    // Simple POJO for general testing
    private static class TestObject {
        public String name;
        public int id;
        public Date timestamp;
        public byte[] data;

        public TestObject(String name, int id, Date timestamp, byte[] data) {
            this.name = name;
            this.id = id;
            this.timestamp = timestamp;
            this.data = data;
        }
    }

    @Setup(Level.Trial)
    public void setup() {
        // 1. Setup DumperOptions required by JsonRepresenter
        DumperOptions options = new DumperOptions();
        options.setDefaultScalarStyle(DumperOptions.ScalarStyle.JSON_SCALAR_STYLE);
        options.setNonPrintableStyle(DumperOptions.NonPrintableStyle.ESCAPE);

        // 2. Initialize the Subject Under Test
        jsonRepresenter = new JsonRepresenter(options);

        // 3. Setup Test Data
        testObject = new TestObject(
                "Test Item",
                123,
                new Date(),
                "Binary Data".getBytes()
        );
        testDate = new Date();
        testByteArray = "Some binary content".getBytes();
    }

    @Benchmark
    public Node benchmarkGeneralObjectRepresentation(Blackhole bh) {
        // Test representation of a complex POJO using the public represent method
        Node result = jsonRepresenter.represent(testObject);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Node benchmarkDateRepresentation(Blackhole bh) {
        // Test representation of Date object using the public represent method
        Node result = jsonRepresenter.represent(testDate);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Node benchmarkByteArrayRepresentation(Blackhole bh) {
        // Test representation of byte[] using the public represent method
        Node result = jsonRepresenter.represent(testByteArray);
        bh.consume(result);
        return result;
    }
}
