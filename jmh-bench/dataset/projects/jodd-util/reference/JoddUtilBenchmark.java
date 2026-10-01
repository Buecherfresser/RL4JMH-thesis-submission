package bench.generated;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import jodd.bean.BeanCopy;
import jodd.bean.BeanUtil;
import jodd.exception.ExceptionUtil;
import jodd.exception.UncheckedException;
import jodd.introspector.CachingIntrospector;
import jodd.introspector.ClassDescriptor;
import jodd.introspector.CtorDescriptor;
import jodd.introspector.FieldDescriptor;
import jodd.introspector.MethodDescriptor;
import jodd.introspector.PropertyDescriptor;
import jodd.io.AppendableWriter;
import jodd.io.CharBufferReader;
import jodd.io.FileNameUtil;
import jodd.io.FileUtil;
import jodd.io.IOUtil;
import jodd.io.NetUtil;
import jodd.io.PathUtil;
import jodd.io.StreamGobbler;
import jodd.io.UnicodeInputStream;
import jodd.net.HtmlDecoder;
import jodd.net.HtmlEncoder;
import jodd.net.HttpMethod;
import jodd.net.MimeTypes;
import jodd.net.URLCoder;
import jodd.net.URLDecoder;
import jodd.time.JulianDate;
import jodd.time.TimeUtil;
import jodd.typeconverter.Converter;
import jodd.typeconverter.TypeConverterManager;
import jodd.util.ArraysUtil;
import jodd.util.Base64;
import jodd.util.ClassLoaderUtil;
import jodd.util.CollectionUtil;
import jodd.util.PropertiesUtil;
import jodd.util.StringTemplateMatcher;
import jodd.util.SystemUtil;
import jodd.util.TypeCache;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.infra.Blackhole;

/**
 * Gold reference JMH suite for the Jodd Util mutation track.
 *
 * Covers a broad cross-section of the library: bean property get/set and bean
 * copying, class introspection and its descriptor tree, the type-converter
 * manager and every primitive array converter, the string/array/collection
 * helpers, Base64, the IO helpers (in-memory readers and writers plus a
 * temporary file for the filesystem ones), URL/HTML/MIME encoding and decoding,
 * the date-time conversions and the exception utilities.
 *
 * Input data is built once per trial. The filesystem benchmarks work against a
 * single temporary file created in {@link Setup} and removed in
 * {@link TearDown}, so no benchmark pays for directory creation on every
 * invocation.
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
public class JoddUtilBenchmark {

    private Person source;
    private Person destination;
    private Map<String, Object> beanMap;

    private String text;
    private char[] chars;
    private byte[] bytes;
    private String base64;
    private String htmlSource;
    private String encodedQuery;
    private String[] words;

    private Properties properties;
    private Path tempDir;
    private File propertiesFile;
    private File textFile;

    private LocalDate localDate;
    private LocalDateTime localDateTime;

    @Setup(Level.Trial)
    public void setUp() throws IOException {
        source = new Person();
        source.setName("Ada Lovelace");
        source.setAge(36);
        source.setScore(99.5);
        source.setActive(true);
        source.setTags(new ArrayList<>(Arrays.asList("mathematician", "pioneer")));
        source.setAddress(new Address("Ockham Road", 12, "London"));
        destination = new Person();

        beanMap = new LinkedHashMap<>();
        beanMap.put("name", "Grace Hopper");
        beanMap.put("age", 45);
        beanMap.put("score", 88.25);
        beanMap.put("active", false);

        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 256; i++) {
            sb.append("segment-").append(i).append(" value=").append(i * 3).append('\n');
        }
        text = sb.toString();
        chars = text.toCharArray();
        bytes = text.getBytes(StandardCharsets.UTF_8);
        base64 = Base64.encodeToString(bytes);
        htmlSource = "<p class=\"lead\">Tom &amp; Jerry &lt;b&gt; &quot;quoted&quot; &#65;</p>";
        encodedQuery = "name=Ada+Lovelace&city=Great%20Britain&sym=%26%3D%3F";
        words = new String[] {"alpha", "beta", "gamma", "delta", "epsilon", "zeta"};

        properties = new Properties();
        for (int i = 0; i < 64; i++) {
            properties.setProperty("key." + i, "value-" + i);
        }

        tempDir = Files.createTempDirectory("jmhbench-jodd");
        propertiesFile = tempDir.resolve("reference.properties").toFile();
        PropertiesUtil.writeToFile(properties, propertiesFile, "jmh-bench reference");
        textFile = tempDir.resolve("reference.txt").toFile();
        FileUtil.writeString(textFile, text);

        localDate = LocalDate.of(2024, 1, 15);
        localDateTime = LocalDateTime.of(2024, 1, 15, 13, 45, 30, 500_000_000);
    }

    @TearDown(Level.Trial)
    public void tearDown() throws IOException {
        Files.deleteIfExists(propertiesFile.toPath());
        Files.deleteIfExists(textFile.toPath());
        Files.deleteIfExists(tempDir);
    }

    // ---- bean access -------------------------------------------------------

    @Benchmark
    public void readBeanProperties(final Blackhole bh) {
        bh.consume(BeanUtil.pojo.getProperty(source, "name"));
        bh.consume(BeanUtil.pojo.getProperty(source, "age"));
        bh.consume(BeanUtil.pojo.getProperty(source, "address.city"));
        bh.consume(BeanUtil.pojo.getProperty(source, "tags[0]"));
        bh.consume(BeanUtil.pojo.hasProperty(source, "score"));
        bh.consume(BeanUtil.pojo.getPropertyType(source, "active"));
    }

    @Benchmark
    public Person writeBeanProperties() {
        final Person person = new Person();
        BeanUtil.pojo.setProperty(person, "name", "Katherine Johnson");
        BeanUtil.pojo.setProperty(person, "age", 101);
        BeanUtil.pojo.setProperty(person, "score", 100.0);
        BeanUtil.pojo.setProperty(person, "active", true);
        return person;
    }

    /** Forced writes create the missing intermediate bean and grow the list. */
    @Benchmark
    public Person writeNestedPropertiesForced() {
        final Person person = new Person();
        person.setTags(new ArrayList<>());
        BeanUtil.declaredForced.setProperty(person, "address.street", "Ockham Road");
        BeanUtil.declaredForced.setProperty(person, "address.number", 12);
        BeanUtil.declaredForced.setProperty(person, "tags[3]", "late");
        return person;
    }

    /** Reading through a null intermediate bean is the reported-error path. */
    @Benchmark
    public String readMissingNestedProperty() {
        final Person person = new Person();
        try {
            return String.valueOf(BeanUtil.pojo.getProperty(person, "address.city"));
        } catch (final jodd.bean.BeanException e) {
            return e.getMessage();
        }
    }

    @Benchmark
    public Person copyBean() {
        BeanCopy.from(source).to(destination).declared(true).forced(true)
                .includeFields(true).copy();
        return destination;
    }

    @Benchmark
    public Person copyBeanFromMap() {
        final Person person = new Person();
        BeanCopy.from(beanMap).to(person).forced(true).copy();
        return person;
    }

    // ---- introspection -----------------------------------------------------

    @Benchmark
    public void describeClass(final Blackhole bh) {
        final CachingIntrospector introspector =
                new CachingIntrospector(true, true, true, new String[] {"_"});
        final ClassDescriptor descriptor = introspector.lookup(Person.class);
        bh.consume(descriptor.getType());
        bh.consume(descriptor.isCollection());
        for (final FieldDescriptor field : descriptor.getAllFieldDescriptors()) {
            bh.consume(field.getRawType());
        }
        for (final MethodDescriptor method : descriptor.getAllMethodDescriptors()) {
            bh.consume(method.getRawReturnComponentType());
        }
        for (final PropertyDescriptor property : descriptor.getAllPropertyDescriptors()) {
            bh.consume(property.isGetterOnly());
            if (property.getGetter(true) != null) {
                bh.consume(property.getGetter(true).getGetterRawKeyComponentType());
            }
        }
        for (final CtorDescriptor ctor : descriptor.getAllCtorDescriptors()) {
            bh.consume(ctor.getConstructor());
        }
        bh.consume(descriptor.getMethodDescriptor("setAge", new Class[] {int.class}, true));
        introspector.reset();
    }

    // ---- type conversion ---------------------------------------------------

    @Benchmark
    public void convertScalars(final Blackhole bh) {
        final Converter converter = Converter.get();
        bh.consume(converter.toShortValue("512", (short) 0));
        bh.consume(converter.toIntValue("123456", 0));
        bh.consume(converter.toLongValue("9876543210", 0L));
        bh.consume(converter.toDoubleValue("3.14159", 0d));
        bh.consume(converter.toBooleanValue("true", false));
        bh.consume(converter.toString(42));
        bh.consume(converter.toBigDecimal("1234.5678"));
    }

    @Benchmark
    public void convertArrays(final Blackhole bh) {
        final TypeConverterManager manager = TypeConverterManager.get();
        bh.consume(manager.convertType("1,2,3,4,5", int[].class));
        bh.consume(manager.convertType("1,2,3,4,5", long[].class));
        bh.consume(manager.convertType("1,2,3,4,5", short[].class));
        bh.consume(manager.convertType("1,2,3,4,5", byte[].class));
        bh.consume(manager.convertType("1.5,2.5,3.5", double[].class));
        bh.consume(manager.convertType("1.5,2.5,3.5", float[].class));
        bh.consume(manager.convertType("true,false,true", boolean[].class));
        bh.consume(manager.convertType("a,b,c", char[].class));
        bh.consume(manager.convertType("a,b,c", String[].class));
        bh.consume(manager.convertType("java.lang.String,java.lang.Integer", Class[].class));
        bh.consume(manager.convertType(Arrays.asList("1", "2", "3"), Float[].class));
    }

    @Benchmark
    public Object convertToCollection() {
        return TypeConverterManager.get()
                .convertToCollection("10,20,30,40", List.class, Integer.class);
    }

    // ---- strings, arrays, collections --------------------------------------

    @Benchmark
    public char[] insertIntoArray() {
        char[] result = chars.clone();
        for (int i = 0; i < 32; i++) {
            result = ArraysUtil.insert(result, new char[] {'x', 'y'}, i * 8);
        }
        return result;
    }

    @Benchmark
    public void arrayHelpers(final Blackhole bh) {
        bh.consume(ArraysUtil.join(words, new String[] {"eta", "theta"}));
        bh.consume(ArraysUtil.subarray(words, 1, 3));
        bh.consume(ArraysUtil.indexOf(words, "gamma"));
        bh.consume(ArraysUtil.append(words, "eta"));
    }

    @Benchmark
    public String base64RoundTrip() {
        final byte[] encoded = Base64.encodeToByte(bytes);
        return Base64.decodeToString(encoded);
    }

    @Benchmark
    public String base64Decode() {
        return new String(Base64.decode(base64), StandardCharsets.UTF_8);
    }

    @Benchmark
    public Object collectionFromIterator() {
        return CollectionUtil.collectionOf(Arrays.asList(words).iterator());
    }

    @Benchmark
    public void matchTemplate(final Blackhole bh) {
        final StringTemplateMatcher matcher =
                StringTemplateMatcher.of("/users/{id:[0-9]+}/posts/{slug}").useRegexMatch();
        bh.consume(matcher.match("/users/42/posts/hello-world"));
        bh.consume(matcher.match("/users/7/posts/another"));
    }

    @Benchmark
    public Object typeCacheLookup() {
        final TypeCache<String> cache = TypeCache.<String>create().weak(true).threadsafe(true).get();
        for (int i = 0; i < 64; i++) {
            cache.get(Person.class, type -> type.getName());
            cache.get(Address.class, type -> type.getSimpleName());
        }
        return cache.get(Person.class);
    }

    @Benchmark
    public long systemProperties() {
        return SystemUtil.getInt("jmhbench.absent.property", 7);
    }

    // ---- IO ----------------------------------------------------------------

    @Benchmark
    public byte[] readBytesFromReader() throws IOException {
        return IOUtil.readBytes(new StringReader(text), 4096);
    }

    /**
     * Reads a bounded number of characters rather than draining to EOF:
     * {@code CharBufferReader.read(char[], int, int)} returns 0 (not -1) once the
     * buffer is exhausted, so a read-until-negative loop never terminates.
     */
    @Benchmark
    public int readCharsFromCharBuffer() throws IOException {
        final char[] sink = new char[1024];
        int total = 0;
        try (CharBufferReader reader = new CharBufferReader(CharBuffer.wrap(chars))) {
            for (int i = 0; i < chars.length / sink.length; i++) {
                total += reader.read(sink, 0, sink.length);
            }
        }
        return total;
    }

    @Benchmark
    public String writeThroughAppendable() throws IOException {
        final StringBuilder sink = new StringBuilder(text.length());
        try (AppendableWriter writer = new AppendableWriter(sink)) {
            writer.write(text);
        }
        return sink.toString();
    }

    /** UnicodeInputStream sniffs the byte-order mark before the first read. */
    @Benchmark
    public String readWithBomDetection() throws IOException {
        try (UnicodeInputStream in =
                new UnicodeInputStream(new ByteArrayInputStream(bytes), StandardCharsets.UTF_8)) {
            final byte[] read = IOUtil.readBytes(in);
            return in.getDetectedEncoding() + "/" + read.length;
        }
    }

    @Benchmark
    public long drainWithStreamGobbler() throws Exception {
        final StreamGobbler gobbler = new StreamGobbler(new ByteArrayInputStream(bytes));
        final Thread thread = new Thread(gobbler);
        thread.start();
        thread.join();
        return bytes.length;
    }

    @Benchmark
    public String readStringFromFile() throws IOException {
        return FileUtil.readString(textFile);
    }

    @Benchmark
    public String readStringFromPath() throws IOException {
        return PathUtil.readString(textFile.toPath());
    }

    @Benchmark
    public Properties writeAndLoadProperties() throws IOException {
        PropertiesUtil.writeToFile(properties, propertiesFile, "jmh-bench reference");
        return PropertiesUtil.createFromFile(propertiesFile);
    }

    @Benchmark
    public void fileNames(final Blackhole bh) {
        bh.consume(FileNameUtil.hasExtension("/var/log/reference.tar.gz"));
        bh.consume(FileNameUtil.getExtension("/var/log/reference.tar.gz"));
        bh.consume(FileNameUtil.getBaseName("/var/log/reference.tar.gz"));
        bh.consume(FileNameUtil.normalize("/var/log/../log/./reference.txt"));
        bh.consume(FileNameUtil.separatorsToUnix("C:\\var\\log\\reference.txt"));
    }

    @Benchmark
    public String resolveLoopbackHostName() {
        return NetUtil.resolveHostName(new byte[] {127, 0, 0, 1});
    }

    @Benchmark
    public java.io.InputStream classResourceStream() throws IOException {
        return ClassLoaderUtil.getClassAsStream(Person.class);
    }

    // ---- net / html / mime -------------------------------------------------

    @Benchmark
    public void mimeTypes(final Blackhole bh) {
        for (final String extension : new String[] {"json", "html", "png", "tar", "txt", "zip"}) {
            bh.consume(MimeTypes.lookupMimeType(extension));
        }
    }

    @Benchmark
    public void encodeUrl(final Blackhole bh) {
        bh.consume(URLCoder.encodeQuery("name=Ada Lovelace&city=Great Britain"));
        bh.consume(URLCoder.encodePath("/a b/c d/e f"));
        bh.consume(URLCoder.encodeHost("example host"));
        bh.consume(URLCoder.encodeUri("https://example.com:8443/a b/c?q=jodd util#frag"));
        bh.consume(URLCoder.build("https://example.com/search")
                .queryParam("q", "jodd util")
                .toString());
    }

    @Benchmark
    public void decodeUrl(final Blackhole bh) {
        bh.consume(URLDecoder.decodeQuery(encodedQuery));
        bh.consume(URLDecoder.decode(encodedQuery));
    }

    @Benchmark
    public void htmlCodec(final Blackhole bh) {
        bh.consume(HtmlEncoder.text(htmlSource));
        bh.consume(HtmlEncoder.attributeSingleQuoted(htmlSource));
        bh.consume(HtmlEncoder.attributeDoubleQuoted(htmlSource));
        bh.consume(HtmlDecoder.decode(htmlSource));
        bh.consume(HtmlDecoder.detectName(htmlSource.toCharArray(), htmlSource.indexOf('&') + 1));
    }

    @Benchmark
    public void httpMethods(final Blackhole bh) {
        for (final HttpMethod method : HttpMethod.values()) {
            bh.consume(method.equalsName("get"));
            bh.consume(method.equalsName(method.name()));
        }
    }

    // ---- time --------------------------------------------------------------

    @Benchmark
    public void timeConversions(final Blackhole bh) {
        bh.consume(TimeUtil.toMilliseconds(localDate));
        bh.consume(TimeUtil.toMilliseconds(localDateTime));
        bh.consume(TimeUtil.fromMilliseconds(1_700_000_000_000L));
        bh.consume(TimeUtil.toCalendar(localDateTime));
        bh.consume(TimeUtil.fromDate(TimeUtil.toDate(localDate)));
    }

    @Benchmark
    public double julianDate() {
        final JulianDate julian = JulianDate.of(localDateTime);
        return julian.toDouble() + julian.add(1.5).toDouble();
    }

    // ---- exceptions --------------------------------------------------------

    @Benchmark
    public Throwable rootCause() {
        final Throwable root = new IllegalStateException("root");
        return ExceptionUtil.getRootCause(
                new RuntimeException("outer", new IllegalArgumentException("middle", root)));
    }

    @Benchmark
    public String wrapCheckedException() {
        return UncheckedException.callAndWrapException(() -> {
            if (text.isEmpty()) {
                throw new IOException("unreachable");
            }
            return text.substring(0, 16);
        });
    }

    // ---- fixtures ----------------------------------------------------------

    /** A plain bean: scalars, a list and a nested bean. */
    public static class Person {

        private String name;
        private int age;
        private double score;
        private boolean active;
        private List<String> tags;
        private Address address;

        public String getName() {
            return name;
        }

        public void setName(final String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(final int age) {
            this.age = age;
        }

        public double getScore() {
            return score;
        }

        public void setScore(final double score) {
            this.score = score;
        }

        public boolean isActive() {
            return active;
        }

        public void setActive(final boolean active) {
            this.active = active;
        }

        public List<String> getTags() {
            return tags;
        }

        public void setTags(final List<String> tags) {
            this.tags = tags;
        }

        public Address getAddress() {
            return address;
        }

        public void setAddress(final Address address) {
            this.address = address;
        }
    }

    /** Nested bean, reached through dotted property names. */
    public static class Address {

        private String street;
        private int number;
        private String city;

        public Address() {
        }

        public Address(final String street, final int number, final String city) {
            this.street = street;
            this.number = number;
            this.city = city;
        }

        public String getStreet() {
            return street;
        }

        public void setStreet(final String street) {
            this.street = street;
        }

        public int getNumber() {
            return number;
        }

        public void setNumber(final int number) {
            this.number = number;
        }

        public String getCity() {
            return city;
        }

        public void setCity(final String city) {
            this.city = city;
        }
    }
}
