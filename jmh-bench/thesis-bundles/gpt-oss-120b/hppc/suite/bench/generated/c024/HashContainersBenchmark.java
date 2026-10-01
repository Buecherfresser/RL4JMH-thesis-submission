package bench.generated.c024;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.HashContainers;
import java.util.Random;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class HashContainersBenchmark {

  // Load factors prepared in @Setup
  double defaultLoadFactor;
  double minLoadFactor;
  double maxLoadFactor;
  double randomLoadFactor;

  @Setup(Level.Trial)
  public void setUp() {
    defaultLoadFactor = HashContainers.DEFAULT_LOAD_FACTOR;
    minLoadFactor = HashContainers.MIN_LOAD_FACTOR;
    maxLoadFactor = HashContainers.MAX_LOAD_FACTOR;
    Random rnd = new Random(0x1234ABCDL);
    // Generate a random load factor within the allowed range (exclusive of bounds to avoid exceptions)
    double range = maxLoadFactor - minLoadFactor;
    randomLoadFactor = minLoadFactor + rnd.nextDouble() * range;
  }

  @Benchmark
  public int maxElementsDefault() {
    return HashContainers.maxElements(defaultLoadFactor);
  }

  @Benchmark
  public int maxElementsMin() {
    return HashContainers.maxElements(minLoadFactor);
  }

  @Benchmark
  public int maxElementsMax() {
    return HashContainers.maxElements(maxLoadFactor);
  }

  @Benchmark
  public int maxElementsRandom() {
    return HashContainers.maxElements(randomLoadFactor);
  }
}
