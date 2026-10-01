package bench.generated.c037;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.representer.JsonRepresenter;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.nodes.Node;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class JsonRepresenterBenchmark {

    private JsonRepresenter jsonRepresenter;
    private byte[] sampleBytes;
    private Date sampleDate;
    private Map<String, Object> sampleMap;
    private String sampleString;

    @Setup
    public void setup() {
        DumperOptions options = new DumperOptions();
        options.setDefaultScalarStyle(DumperOptions.ScalarStyle.JSON_SCALAR_STYLE);
        options.setNonPrintableStyle(DumperOptions.NonPrintableStyle.ESCAPE);
        jsonRepresenter = new JsonRepresenter(options);

        sampleBytes = new byte[256];
        for (int i = 0; i < sampleBytes.length; i++) {
            sampleBytes[i] = (byte) i;
        }

        sampleDate = new Date(1_640_995_200_000L);

        sampleMap = new HashMap<>();
        sampleMap.put("int", 42);
        sampleMap.put("bool", true);
        sampleMap.put("string", "example");
        sampleMap.put("list", new int[] {1, 2, 3});

        sampleString = "The quick brown fox jumps over the lazy dog";
    }

    @Benchmark
    public Node benchmarkRepresentByteArray() {
        return jsonRepresenter.represent(sampleBytes);
    }

    @Benchmark
    public Node benchmarkRepresentDate() {
        return jsonRepresenter.represent(sampleDate);
    }

    @Benchmark
    public Node benchmarkRepresentMap() {
        return jsonRepresenter.represent(sampleMap);
    }

    @Benchmark
    public Node benchmarkRepresentString() {
        return jsonRepresenter.represent(sampleString);
    }
}
