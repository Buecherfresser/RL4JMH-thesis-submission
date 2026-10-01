package bench.generated.c004;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.composer.Composer;
import org.yaml.snakeyaml.parser.Parser;
import org.yaml.snakeyaml.parser.ParserImpl;
import org.yaml.snakeyaml.reader.StreamReader;
import org.yaml.snakeyaml.resolver.Resolver;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.nodes.Node;
import java.io.StringReader;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ComposerBenchmark {

    private String multiDocYaml;
    private String singleDocYaml;
    private LoaderOptions loaderOptions;
    private Resolver resolver;

    @Setup
    public void setup() {
        loaderOptions = new LoaderOptions();
        resolver = new Resolver();
        multiDocYaml = "a: 1\n---\nb: 2\n";
        singleDocYaml = "c: 3\n";
    }

    private Composer createComposer(String yaml) {
        Parser parser = new ParserImpl(new StreamReader(new StringReader(yaml)), loaderOptions);
        return new Composer(parser, resolver, loaderOptions);
    }

    @Benchmark
    public boolean benchmarkCheckNode() {
        Composer composer = createComposer(multiDocYaml);
        return composer.checkNode();
    }

    @Benchmark
    public Node benchmarkGetNode() {
        Composer composer = createComposer(multiDocYaml);
        return composer.getNode();
    }

    @Benchmark
    public Node benchmarkGetSingleNode() {
        Composer composer = createComposer(singleDocYaml);
        return composer.getSingleNode();
    }
}
