package bench.generated.c044;

import org.openjdk.jmh.annotations.*;
import java.util.concurrent.TimeUnit;

import org.yaml.snakeyaml.tokens.AliasToken;
import org.yaml.snakeyaml.tokens.Token;
import org.yaml.snakeyaml.scanner.ScannerImpl;
import org.yaml.snakeyaml.reader.StreamReader;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.error.Mark;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1) @Warmup(iterations = 1, time = 1) @Measurement(iterations = 3, time = 1)
public class AliasTokenBenchmark {

  private final String aliasYaml = "a: *x\n";
  private final LoaderOptions loaderOptions = new LoaderOptions();

  private AliasToken token;
  private String value;
  private Mark startMark;
  private Mark endMark;

  @Setup(Level.Trial)
  public void setup() {
    token = scanForAlias(aliasYaml);
    value = token.getValue();
    startMark = token.getStartMark();
    endMark = token.getEndMark();
  }

  private AliasToken scanForAlias(String yaml) {
    StreamReader reader = new StreamReader(yaml);
    ScannerImpl scanner = new ScannerImpl(reader, loaderOptions);
    Token t;
    while ((t = scanner.getToken()) != null) {
      if (t instanceof AliasToken) {
        return (AliasToken) t;
      }
    }
    throw new IllegalStateException("No AliasToken found in input");
  }

  @Benchmark
  public String benchmarkGetValue() {
    return token.getValue();
  }

  @Benchmark
  public Token.ID benchmarkGetTokenId() {
    return token.getTokenId();
  }

  @Benchmark
  public AliasToken benchmarkConstructor() {
    return new AliasToken(value, startMark, endMark);
  }

  @Benchmark
  public AliasToken benchmarkParseAliasToken() {
    return scanForAlias(aliasYaml);
  }
}
