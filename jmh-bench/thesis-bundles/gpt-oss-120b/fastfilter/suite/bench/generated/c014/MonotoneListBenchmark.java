package bench.generated.c014;

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
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.SplittableRandom;
import org.fastfilter.gcs.MonotoneList;
import org.fastfilter.gcs.BitBuffer;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class MonotoneListBenchmark {

    private static final int ELEMENT_COUNT = 10_000;

    private int[] data;
    private MonotoneList list;
    private BitBuffer buffer;
    private SplittableRandom random;

    @Setup(Level.Trial)
    public void setUp() {
        random = new SplittableRandom(12345L);
        data = new int[ELEMENT_COUNT];
        int value = 0;
        for (int i = 0; i < ELEMENT_COUNT; i++) {
            value += random.nextInt(5) + 1; // ensure monotone increasing
            data[i] = value;
        }
        // allocate a buffer large enough to hold the encoded list
        int bitsNeeded = MonotoneList.getSize(data) + 64;
        buffer = new BitBuffer(bitsNeeded);
        // generate the list once for read‑only benchmarks
        list = MonotoneList.generate(data, buffer);
        // after generation the buffer position is at the start of the encoded data
        buffer.seek(0);
    }

    @Benchmark
    public MonotoneList benchmarkGenerate() {
        int bits = MonotoneList.getSize(data) + 64;
        BitBuffer buf = new BitBuffer(bits);
        return MonotoneList.generate(data, buf);
    }

    @Benchmark
    public MonotoneList benchmarkLoad() {
        // reset buffer to the beginning before each load
        buffer.seek(0);
        return MonotoneList.load(buffer);
    }

    @Benchmark
    public int benchmarkGet() {
        int idx = random.nextInt(data.length);
        return list.get(idx);
    }

    @Benchmark
    public long benchmarkGetPair() {
        int idx = random.nextInt(data.length - 1);
        return list.getPair(idx);
    }
}
