package bench.generated.c023;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.BitSet;
import java.util.Random;
import java.io.*;
import org.fastfilter.xorplus.Rank9;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Rank9Benchmark {

    @State(Scope.Benchmark)
    public static class SharedState {
        Rank9 rank9;
        BitSet bitset;
        long bitCount;
        long[] positions;
        byte[] serialized;

        @Setup(Level.Trial)
        public void setup() {
            bitCount = 1_000_000; // 1 million bits
            bitset = new BitSet((int) bitCount);
            Random r = new Random(12345);
            for (int i = 0; i < bitCount; i++) {
                if (r.nextBoolean()) {
                    bitset.set(i);
                }
            }
            rank9 = new Rank9(bitset, bitCount);

            // Generate random positions within the bit range
            positions = new long[1024];
            for (int i = 0; i < positions.length; i++) {
                positions[i] = r.nextInt((int) bitCount);
            }

            // Serialize for deserialization benchmark
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                DataOutputStream dos = new DataOutputStream(baos);
                rank9.write(dos);
                dos.flush();
                serialized = baos.toByteArray();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @State(Scope.Thread)
    public static class ThreadState {
        int index;
    }

    @Benchmark
    public long rankBenchmark(SharedState shared, ThreadState thread) {
        long pos = shared.positions[thread.index++];
        if (thread.index >= shared.positions.length) {
            thread.index = 0;
        }
        return shared.rank9.rank(pos);
    }

    @Benchmark
    public long getBenchmark(SharedState shared, ThreadState thread) {
        long pos = shared.positions[thread.index++];
        if (thread.index >= shared.positions.length) {
            thread.index = 0;
        }
        return shared.rank9.get(pos);
    }

    @Benchmark
    public long getAndPartialRankBenchmark(SharedState shared, ThreadState thread) {
        long pos = shared.positions[thread.index++];
        if (thread.index >= shared.positions.length) {
            thread.index = 0;
        }
        return shared.rank9.getAndPartialRank(pos);
    }

    @Benchmark
    public long remainingRankBenchmark(SharedState shared, ThreadState thread) {
        long pos = shared.positions[thread.index++];
        if (thread.index >= shared.positions.length) {
            thread.index = 0;
        }
        return shared.rank9.remainingRank(pos);
    }

    @Benchmark
    public long constructBenchmark(SharedState shared) {
        Rank9 r = new Rank9(shared.bitset, shared.bitCount);
        return r.getBitCount();
    }

    @Benchmark
    public int writeBenchmark(SharedState shared) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        shared.rank9.write(dos);
        dos.flush();
        return baos.size();
    }

    @Benchmark
    public long readBenchmark(SharedState shared) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(shared.serialized);
        DataInputStream dis = new DataInputStream(bais);
        Rank9 r = new Rank9(dis);
        return r.getBitCount();
    }
}
