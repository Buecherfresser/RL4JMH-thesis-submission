package bench.generated.c002;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.events.Event;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class YamlBenchmark {

    private Yaml yaml;
    private Yaml yamlMap;
    private String simpleYamlInput;
    private String complexYamlInput;
    private Object simpleObject;
    private List<Object> objectList;
    private Node rootNode;

    // Simple POJO for testing
    private static class TestObject {
        public String name;
        public int id;
        public List<String> tags;

        public TestObject(String name, int id, List<String> tags) {
            this.name = name;
            this.id = id;
            this.tags = tags;
        }
    }

    @Setup(Level.Trial)
    public void setup() {
        // Initialize Yaml instances
        // Default configuration
        yaml = new Yaml();
        // Configuration optimized for map output (e.g., FlowStyle.BLOCK)
        DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setDefaultFlowStyle(org.yaml.snakeyaml.DumperOptions.FlowStyle.BLOCK);
        yamlMap = new Yaml(new org.yaml.snakeyaml.constructor.Constructor(new LoaderOptions()), new org.yaml.snakeyaml.representer.Representer(dumperOptions), dumperOptions);

        // --- Input Data Setup ---

        // 1. Simple object for dumping/loading
        simpleObject = new TestObject("TestItem", 101, Arrays.asList("A", "B"));

        // 2. List of objects for sequence dumping/loading
        objectList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            objectList.add(new TestObject("Item" + i, i, Arrays.asList("X")));
        }

        // 3. YAML string input (simple)
        simpleYamlInput = "name: TestItem\nid: 101\ntags:\n  - A\n  - B";

        // 4. YAML string input (complex/multi-document)
        complexYamlInput = "--- \nname: Doc1\nid: 1\ntags: [C]\n---\nname: Doc2\nid: 2\ntags: [D]";

        // 5. Node structure for serialization testing
        rootNode = yaml.represent(simpleObject);
    }

    // =========================================================================
    // DESERIALIZATION (LOADING/PARSING) BENCHMARKS
    // =========================================================================

    @Benchmark
    public Object loadStringSimple(Blackhole bh) {
        // load(String yaml)
        Object result = yaml.load(simpleYamlInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Object loadStringComplex(Blackhole bh) {
        // load(String yaml) - testing multi-document parsing (only loads first by default)
        Object result = yaml.load(complexYamlInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Object loadReaderSimple(Blackhole bh) {
        // load(Reader io)
        StringReader reader = new StringReader(simpleYamlInput);
        Object result = yaml.load(reader);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Object loadAsStringSimple(Blackhole bh) {
        // loadAs(String yaml, Class<? super T> type)
        Object result = yaml.loadAs(simpleYamlInput, TestObject.class);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Object loadAsReaderSimple(Blackhole bh) {
        // loadAs(Reader io, Class<? super T> type)
        StringReader reader = new StringReader(simpleYamlInput);
        Object result = yaml.loadAs(reader, TestObject.class);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Iterable<Object> loadAllString(Blackhole bh) {
        // loadAll(String yaml) - testing multi-document iteration
        Iterable<Object> result = yaml.loadAll(complexYamlInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Iterable<Object> loadAllReader(Blackhole bh) {
        // loadAll(Reader yaml) - testing multi-document iteration
        StringReader reader = new StringReader(complexYamlInput);
        Iterable<Object> result = yaml.loadAll(reader);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Node composeReader(Blackhole bh) {
        // compose(Reader yaml) - parsing to Node tree
        StringReader reader = new StringReader(simpleYamlInput);
        Node result = yaml.compose(reader);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Iterable<Node> composeAllReader(Blackhole bh) {
        // composeAll(Reader yaml) - parsing multiple documents to Node trees
        StringReader reader = new StringReader(complexYamlInput);
        Iterable<Node> result = yaml.composeAll(reader);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Iterable<Event> parseReader(Blackhole bh) {
        // parse(Reader yaml) - parsing to Events
        StringReader reader = new StringReader(simpleYamlInput);
        Iterable<Event> result = yaml.parse(reader);
        bh.consume(result);
        return result;
    }

    // =========================================================================
    // SERIALIZATION (DUMPING) BENCHMARKS
    // =========================================================================

    @Benchmark
    public String dumpSimple(Blackhole bh) {
        // dump(Object data)
        String result = yaml.dump(simpleObject);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public String dumpAllSequence(Blackhole bh) {
        // dumpAll(Iterator<? extends Object> data)
        Iterator<Object> iterator = objectList.iterator();
        String result = yaml.dumpAll(iterator);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public void dumpSimpleToWriter(Blackhole bh) {
        // dump(Object data, Writer output)
        StringWriter writer = new StringWriter();
        yaml.dump(simpleObject, writer);
        bh.consume(writer.toString());
    }

    @Benchmark
    public void dumpAllSequenceToWriter(Blackhole bh) {
        // dumpAll(Iterator<? extends Object> data, Writer output)
        StringWriter writer = new StringWriter();
        Iterator<Object> iterator = objectList.iterator();
        yaml.dumpAll(iterator, writer);
        bh.consume(writer.toString());
    }

    @Benchmark
    public String dumpAsMapSimple(Blackhole bh) {
        // dumpAsMap(Object data)
        String result = yamlMap.dumpAsMap(simpleObject);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public String dumpAsCustomTag(Blackhole bh) {
        // dumpAs(Object data, Tag rootTag, FlowStyle flowStyle)
        Tag customTag = new Tag("!!custom_type");
        String result = yaml.dumpAs(simpleObject, customTag, org.yaml.snakeyaml.DumperOptions.FlowStyle.FLOW);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public void serializeNodeToWriter(Blackhole bh) {
        // serialize(Node node, Writer output)
        StringWriter writer = new StringWriter();
        yaml.serialize(rootNode, writer);
        bh.consume(writer.toString());
    }

    @Benchmark
    public List<Event> serializeNodeToEvents(Blackhole bh) {
        // serialize(Node data)
        List<Event> result = yaml.serialize(rootNode);
        bh.consume(result);
        return result;
    }
}
