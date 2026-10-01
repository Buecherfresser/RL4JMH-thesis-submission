package bench.generated.c019;

import org.fastfilter.Filter;
import org.fastfilter.xor.Xor8;
import org.fastfilter.utils.Hash;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class Xor8Benchmark {

    private Xor8 xor8Filter;
    private long[] keys;
    private List<Long> positiveKeys;
    private List<Long> negativeKeys;

    private static final int KEY_SET_SIZE = 100_000;
    private static final Random RANDOM = new Random(42);

    @Setup
    public void setup() throws IOException {
        // 1. Generate a large set of unique keys
        keys = new long[KEY_SET_SIZE];
        positiveKeys = new ArrayList<>(KEY_SET_SIZE);
        negativeKeys = new ArrayList<>(KEY_SET_SIZE);

        for (int i = 0; i < KEY_SET_SIZE; i++) {
            long key = RANDOM.nextLong();
            keys[i] = key;
            positiveKeys.add(key);
        }

        // 2. Construct the Xor8 filter once
        xor8Filter = Xor8.construct(keys);
    }

    @Benchmark
    public void mayContain_PositiveHit(Blackhole bh) {
        // Test a key known to be in the set
        long key = positiveKeys.get(RANDOM.nextInt(KEY_SET_SIZE));
        boolean result = xor8Filter.mayContain(key);
        bh.consume(result);
    }

    @Benchmark
    public void mayContain_NegativeMiss(Blackhole bh) {
        // Test a key guaranteed not to be in the set (generate a random large number)
        long key = RANDOM.nextLong() | 0x1000000000000000L; // Ensure it's unlikely to be in the initial set
        boolean result = xor8Filter.mayContain(key);
        bh.consume(result);
    }

    @Benchmark
    public void getData_Serialization(Blackhole bh) throws IOException {
        // Test serialization of the filter state
        byte[] data = xor8Filter.getData();
        bh.consume(data);
    }

    @Benchmark
    public void Xor8_Deserialization(Blackhole bh) throws IOException {
        // Test deserialization using the InputStream constructor
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(keys.length);
        dos.writeLong(Hash.randomSeed()); // Mocking seed for simplicity, though real deserialization reads the stored seed
        dos.write(xor8Filter.getData());
        dos.flush();

        try (ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray())) {
            Xor8 deserializedFilter = new Xor8(bais);
            bh.consume(deserializedFilter);
        }
    }
}
