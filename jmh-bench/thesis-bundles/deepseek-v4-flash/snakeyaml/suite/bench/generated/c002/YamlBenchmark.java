package bench.generated.c002;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.events.Event;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.Tag;
import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class YamlBenchmark {

    private Yaml yaml;
    private String yamlString;
    private byte[] yamlBytes;
    private Map<String, Object> dataMap;
    private Person person;
    private List<Object> dataList;
    private Node personNode;
    private Node mapNode;

    @Setup(Level.Trial)
    public void setup() {
        yaml = new Yaml();
        yamlString = buildYamlString();
        yamlBytes = yamlString.getBytes(StandardCharsets.UTF_8);
        dataMap = buildDataMap();
        person = buildPerson();
        dataList = new ArrayList<>();
        dataList.add(dataMap);
        dataList.add(person);
        personNode = yaml.represent(person);
        mapNode = yaml.represent(dataMap);
    }

    private String buildYamlString() {
        StringBuilder sb = new StringBuilder();
        sb.append("name: John Doe\n");
        sb.append("age: 30\n");
        sb.append("active: true\n");
        sb.append("tags:\n");
        sb.append("  - java\n");
        sb.append("  - yaml\n");
        sb.append("address:\n");
        sb.append("  city: New York\n");
        sb.append("  zip: \"10001\"\n");
        return sb.toString();
    }

    private Map<String, Object> buildDataMap() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("name", "John Doe");
        map.put("age", 30);
        map.put("active", true);
        map.put("tags", Arrays.asList("java", "yaml"));
        Map<String, Object> address = new LinkedHashMap<>();
        address.put("city", "New York");
        address.put("zip", "10001");
        map.put("address", address);
        return map;
    }

    private Person buildPerson() {
        Person p = new Person();
        p.setName("John Doe");
        p.setAge(30);
        p.setActive(true);
        p.setTags(Arrays.asList("java", "yaml"));
        Address a = new Address();
        a.setCity("New York");
        a.setZip("10001");
        p.setAddress(a);
        return p;
    }

    @Benchmark
    public Object loadString() {
        return yaml.load(yamlString);
    }

    @Benchmark
    public Object loadReader() {
        return yaml.load(new StringReader(yamlString));
    }

    @Benchmark
    public Object loadInputStream() {
        return yaml.load(new ByteArrayInputStream(yamlBytes));
    }

    @Benchmark
    public Person loadAsString() {
        return yaml.loadAs(yamlString, Person.class);
    }

    @Benchmark
    public Person loadAsReader() {
        return yaml.loadAs(new StringReader(yamlString), Person.class);
    }

    @Benchmark
    public Person loadAsInputStream() {
        return yaml.loadAs(new ByteArrayInputStream(yamlBytes), Person.class);
    }

    @Benchmark
    public void loadAllString(Blackhole bh) {
        for (Object o : yaml.loadAll(yamlString)) {
            bh.consume(o);
        }
    }

    @Benchmark
    public void loadAllReader(Blackhole bh) {
        for (Object o : yaml.loadAll(new StringReader(yamlString))) {
            bh.consume(o);
        }
    }

    @Benchmark
    public void loadAllInputStream(Blackhole bh) {
        for (Object o : yaml.loadAll(new ByteArrayInputStream(yamlBytes))) {
            bh.consume(o);
        }
    }

    @Benchmark
    public String dumpMap() {
        return yaml.dump(dataMap);
    }

    @Benchmark
    public String dumpPerson() {
        return yaml.dump(person);
    }

    @Benchmark
    public String dumpAll() {
        return yaml.dumpAll(dataList.iterator());
    }

    @Benchmark
    public void dumpToWriter(Blackhole bh) {
        StringWriter writer = new StringWriter();
        yaml.dump(person, writer);
        bh.consume(writer.toString());
    }

    @Benchmark
    public void dumpAllToWriter(Blackhole bh) {
        StringWriter writer = new StringWriter();
        yaml.dumpAll(dataList.iterator(), writer);
        bh.consume(writer.toString());
    }

    @Benchmark
    public String dumpAs() {
        return yaml.dumpAs(person, Tag.MAP, FlowStyle.BLOCK);
    }

    @Benchmark
    public String dumpAsMap() {
        return yaml.dumpAsMap(person);
    }

    @Benchmark
    public Node represent() {
        return yaml.represent(person);
    }

    @Benchmark
    public Node representMap() {
        return yaml.represent(dataMap);
    }

    @Benchmark
    public Node compose() {
        return yaml.compose(new StringReader(yamlString));
    }

    @Benchmark
    public void composeAll(Blackhole bh) {
        for (Node n : yaml.composeAll(new StringReader(yamlString))) {
            bh.consume(n);
        }
    }

    @Benchmark
    public void parse(Blackhole bh) {
        for (Event e : yaml.parse(new StringReader(yamlString))) {
            bh.consume(e);
        }
    }

    @Benchmark
    public void serializeNodeToWriter(Blackhole bh) {
        StringWriter writer = new StringWriter();
        yaml.serialize(personNode, writer);
        bh.consume(writer.toString());
    }

    @Benchmark
    public List<Event> serializeNodeToEvents() {
        return yaml.serialize(personNode);
    }

    @Benchmark
    public List<Event> serializeMapNodeToEvents() {
        return yaml.serialize(mapNode);
    }

    public static class Person {
        private String name;
        private int age;
        private boolean active;
        private List<String> tags;
        private Address address;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public int getAge() { return age; }
        public void setAge(int age) { this.age = age; }
        public boolean isActive() { return active; }
        public void setActive(boolean active) { this.active = active; }
        public List<String> getTags() { return tags; }
        public void setTags(List<String> tags) { this.tags = tags; }
        public Address getAddress() { return address; }
        public void setAddress(Address address) { this.address = address; }
    }

    public static class Address {
        private String city;
        private String zip;

        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
        public String getZip() { return zip; }
        public void setZip(String zip) { this.zip = zip; }
    }
}
