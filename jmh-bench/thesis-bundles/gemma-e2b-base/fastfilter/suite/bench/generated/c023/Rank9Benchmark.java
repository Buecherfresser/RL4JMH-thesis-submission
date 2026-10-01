package bench.generated.c023;

import org.fastfilter.xorplus.Rank9;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.BitSet;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.Arrays;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Rank9Benchmark {

    private Rank9 rank9;
    private long[] testKeys;
    private int bitCount;
    private final Random random = new Random(42);

    @Setup
    public void setup() throws IOException {
        // 1. Generate a large BitSet for the Rank9 structure
        int numLongs = 1024; // 1024 long keys
        BitSet set = new BitSet(numLongs);
        
        // Populate the BitSet randomly
        for (int i = 0; i < numLongs; i++) {
            if (random.nextBoolean()) {
                set.set(i);
            }
        }
        
        this.bitCount = set.cardinality();
        this.testKeys = set.toLongArray();

        // 2. Construct the Rank9 object
        this.rank9 = new Rank9(set, bitCount);
    }

    @Benchmark
    public void rank_operation(Blackhole bh) {
        // Pick a random position within the bounds of the structure
        long pos = random.nextLong(bitCount);
        long result = rank9.rank(pos);
        bh.consume(result);
    }

    @Benchmark
    public void get_operation(Blackhole bh) {
        long pos = random.nextLong(bitCount);
        long result = rank9.get(pos);
        bh.consume(result);
    }

    @Benchmark
    public void getAndPartialRank_operation(Blackhole bh) {
        long pos = random.nextLong(bitCount);
        long result = rank9.getAndPartialRank(pos);
        bh.consume(result);
    }

    @Benchmark
    public void remainingRank_operation(Blackhole bh) {
        long pos = random.nextLong(bitCount);
        long result = rank9.remainingRank(pos);
        bh.consume(result);
    }

    @Benchmark
    public void getBitCount_operation(Blackhole bh) {
        long result = rank9.getBitCount();
        bh.consume(result);
    }

    @Benchmark
    public void write_operation(Blackhole bh) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        
        rank9.write(dos);
        
        dos.flush();
        bh.consume(baos.toByteArray());
    }

    @Benchmark
    public void read_operation(Blackhole bh) throws IOException {
        // Estimate size based on Rank9 write implementation: 4 bytes (bits.length) + bits.length * 8 bytes + 4 bytes (counts.length) + counts.length * 8 bytes
        // Since bits.length is around 1024, this is roughly 4 + 8192 + 4 + 8192 = 16392 bytes.
        byte[] data = new byte[16392]; 
        try (ByteArrayInputStream bais = new ByteArrayInputStream(data);
             DataInputStream dis = new DataInputStream(bais)) {
            
            Rank9 loadedRank9 = new Rank9(dis);
            
            // Test a read operation on the loaded structure
            long pos = random.nextLong(bitCount);
            long result = loadedRank9.rank(pos);
            bh.consume(result);
        }
    }
}
