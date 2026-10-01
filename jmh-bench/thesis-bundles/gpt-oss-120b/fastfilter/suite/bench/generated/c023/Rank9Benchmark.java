package bench.generated.c023;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.fastfilter.xorplus.Rank9;
import java.util.BitSet;
import java.util.Random;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Rank9Benchmark {

    private BitSet bitSet;
    private long bitCount;
    private Rank9 rank9;
    private long[] positions;
    private int positionIndex;
    private byte[] serialized;

    @Setup
    public void setup() throws IOException {
        // Fixed size bit set (1 Mi bits)
        bitCount = 1 << 20; // 1,048,576 bits
        bitSet = new BitSet((int) bitCount);
        Random rnd = new Random(0L);
        for (long i = 0; i < bitCount; i++) {
            if (rnd.nextBoolean()) {
                bitSet.set((int) i);
            }
        }

        rank9 = new Rank9(bitSet, bitCount);

        // Prepare random query positions
        int querySize = 1024;
        positions = new long[querySize];
        for (int i = 0; i < querySize; i++) {
            positions[i] = Math.abs(rnd.nextLong()) % bitCount;
        }

        // Serialize once for deserialization benchmark
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        rank9.write(dos);
        dos.flush();
        serialized = baos.toByteArray();
        dos.close();
        baos.close();
    }

    @Benchmark
    public long benchmarkRank() {
        long pos = positions[positionIndex & (positions.length - 1)];
        positionIndex++;
        return rank9.rank(pos);
    }

    @Benchmark
    public long benchmarkGet() {
        long pos = positions[positionIndex & (positions.length - 1)];
        positionIndex++;
        return rank9.get(pos);
    }

    @Benchmark
    public long benchmarkGetAndPartialRank() {
        long pos = positions[positionIndex & (positions.length - 1)];
        positionIndex++;
        return rank9.getAndPartialRank(pos);
    }

    @Benchmark
    public long benchmarkRemainingRank() {
        long pos = positions[positionIndex & (positions.length - 1)];
        positionIndex++;
        return rank9.remainingRank(pos);
    }

    @Benchmark
    public int benchmarkGetBitCount() {
        return rank9.getBitCount();
    }

    @Benchmark
    public int benchmarkWrite() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        rank9.write(dos);
        dos.flush();
        int size = baos.size();
        dos.close();
        baos.close();
        return size;
    }

    @Benchmark
    public int benchmarkRead() throws IOException {
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(serialized));
        Rank9 r = new Rank9(dis);
        int bc = r.getBitCount();
        dis.close();
        return bc;
    }

    @Benchmark
    public int benchmarkConstruct() {
        Rank9 r = new Rank9(bitSet, bitCount);
        return r.getBitCount();
    }
}
