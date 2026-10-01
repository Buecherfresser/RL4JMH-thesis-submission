package bench.generated.c002;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.events.Event;
import org.yaml.snakeyaml.introspector.BeanAccess;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class YamlBenchmark {

    private Yaml yaml;
    private String yamlDocument;
    private String yamlMultiDoc;
    private Map<String, Object> sampleMap;
    private List<Object> dumpAllList;
    private Node sampleNode;
    private Tag mapTag;
    private FlowStyle flowStyle;

    @Setup(Level.Trial)
    public void setup() {
        DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        LoaderOptions loaderOptions = new LoaderOptions();
        yaml = new Yaml(loaderOptions, dumperOptions);

        yamlDocument = "name: John Doe\nage: 30\nlanguages:\n  - Java\n  - Python\n";

        yamlMultiDoc = "---\nname: Alice\n---\nname: Bob\n";

        sampleMap = new HashMap<>();
        sampleMap.put("name", "Charlie");
        sampleMap.put("age", 25);
        sampleMap.put("active", true);
        sampleMap.put("scores", new int[]{90, 80, 70});

        dumpAllList = new ArrayList<>();
        dumpAllList.add(sampleMap);
        dumpAllList.add(sampleMap);

        sampleNode = yaml.represent(sampleMap);

        mapTag = Tag.MAP;
        flowStyle = FlowStyle.FLOW;
    }

    @Benchmark
    public Object benchmarkLoadString() {
        return yaml.load(yamlDocument);
    }

    @Benchmark
    public Object benchmarkLoadStringAsMap() {
        return yaml.loadAs(yamlDocument, Map.class);
    }

    @Benchmark
    public Object benchmarkLoadAllString() {
        return yaml.loadAll(yamlMultiDoc);
    }

    @Benchmark
    public Object benchmarkDumpString() {
        return yaml.dump(sampleMap);
    }

    @Benchmark
    public Object benchmarkDumpAsString() {
        return yaml.dumpAs(sampleMap, mapTag, flowStyle);
    }

    @Benchmark
    public Object benchmarkDumpAsMapString() {
        return yaml.dumpAsMap(sampleMap);
    }

    @Benchmark
    public Object benchmarkDumpAllString() {
        return yaml.dumpAll(dumpAllList.iterator());
    }

    @Benchmark
    public void benchmarkDumpObjectToWriter(Blackhole bh) {
        StringWriter writer = new StringWriter();
        yaml.dump(sampleMap, writer);
        bh.consume(writer.toString());
    }

    @Benchmark
    public void benchmarkDumpAllToWriter(Blackhole bh) {
        StringWriter writer = new StringWriter();
        yaml.dumpAll(dumpAllList.iterator(), writer);
        bh.consume(writer.toString());
    }

    @Benchmark
    public Object benchmarkSerializeNodeToString() {
        StringWriter writer = new StringWriter();
        yaml.serialize(sampleNode, writer);
        return writer.toString();
    }

    @Benchmark
    public List<Event> benchmarkSerializeNodeToEvents() {
        return yaml.serialize(sampleNode);
    }

    @Benchmark
    public Object benchmarkCompose() {
        return yaml.compose(new StringReader(yamlDocument));
    }

    @Benchmark
    public Object benchmarkComposeAll() {
        return yaml.composeAll(new StringReader(yamlMultiDoc));
    }

    @Benchmark
    public Object benchmarkParse() {
        return yaml.parse(new StringReader(yamlDocument));
    }

    @Benchmark
    public void benchmarkAddImplicitResolver(Blackhole bh) {
        yaml.addImplicitResolver(Tag.STR, java.util.regex.Pattern.compile(".*"), null);
        bh.consume(true);
    }

    @Benchmark
    public String benchmarkToString() {
        return yaml.toString();
    }

    @Benchmark
    public String benchmarkGetName() {
        return yaml.getName();
    }

    @Benchmark
    public void benchmarkSetName(Blackhole bh) {
        yaml.setName("benchmark");
        bh.consume(yaml.getName());
    }

    @Benchmark
    public void benchmarkSetBeanAccess(Blackhole bh) {
        yaml.setBeanAccess(BeanAccess.FIELD);
        bh.consume(yaml.getName());
    }
}
