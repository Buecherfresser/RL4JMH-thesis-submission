package bench.generated.c022;

import org.openjdk.jmh.annotations.*;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.FloatArrayList;
import com.carrotsearch.hppc.cursors.FloatCursor;
import com.carrotsearch.hppc.procedures.FloatProcedure;
import java.util.Random;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class FloatArrayListBenchmark {

    @State(Scope.Benchmark)
    public static abstract class BaseState {
        protected static final int SIZE = 1000;
        protected FloatArrayList template;
        protected FloatArrayList list;

        @Setup(Level.Trial)
        public void setupTrial() {
            template = new FloatArrayList(SIZE);
            Random r = new Random(1234);
            for (int i = 0; i < SIZE; i++) {
                template.add(r.nextFloat());
            }
            list = new FloatArrayList(template);
        }

        protected void resetList(int newSize) {
            list.clear();
            list.addAll(template);
            list.elementsCount = newSize;
        }
    }

    @State(Scope.Benchmark)
    public static class ReadOnlyState extends BaseState {
        // list is ready from trial setup
    }

    @State(Scope.Benchmark)
    public static class AddState extends BaseState {
        @Setup(Level.Invocation)
        public void setupInvocation() {
            resetList(SIZE - 1);
        }
    }

    @State(Scope.Benchmark)
    public static class RemoveLastState extends BaseState {
        @Setup(Level.Invocation)
        public void setupInvocation() {
            resetList(SIZE);
        }
    }

    @State(Scope.Benchmark)
    public static class RemoveAtState extends BaseState {
        @Setup(Level.Invocation)
        public void setupInvocation() {
            resetList(SIZE);
        }
    }

    @State(Scope.Benchmark)
    public static class InsertState extends BaseState {
        @Setup(Level.Invocation)
        public void setupInvocation() {
            resetList(SIZE - 1);
        }
    }

    @State(Scope.Benchmark)
    public static class SetState extends BaseState {
        @Setup(Level.Invocation)
        public void setupInvocation() {
            resetList(SIZE);
        }
    }

    @State(Scope.Benchmark)
    public static class ClearState extends BaseState {
        @Setup(Level.Invocation)
        public void setupInvocation() {
            resetList(SIZE);
        }
    }

    @State(Scope.Benchmark)
    public static class ReleaseState extends BaseState {
        @Setup(Level.Invocation)
        public void setupInvocation() {
            resetList(SIZE);
        }
    }

    @State(Scope.Benchmark)
    public static class SortState extends BaseState {
        @Setup(Level.Invocation)
        public void setupInvocation() {
            resetList(SIZE);
        }
    }

    @State(Scope.Benchmark)
    public static class ReverseState extends BaseState {
        @Setup(Level.Invocation)
        public void setupInvocation() {
            resetList(SIZE);
        }
    }

    @State(Scope.Benchmark)
    public static class ForEachState extends BaseState {
        public float sum;
        public FloatProcedure procedure = new FloatProcedure() {
            @Override
            public void apply(float value) {
                sum += value;
            }
        };

        @Setup(Level.Invocation)
        public void setupInvocation() {
            sum = 0;
        }
    }

    @State(Scope.Benchmark)
    public static class IteratorState extends BaseState {
        public float sum;

        @Setup(Level.Invocation)
        public void setupInvocation() {
            sum = 0;
        }
    }

    @State(Scope.Benchmark)
    public static class EqualsState extends BaseState {
        protected FloatArrayList other;

        @Override
        @Setup(Level.Trial)
        public void setupTrial() {
            super.setupTrial();
            other = new FloatArrayList(template);
        }
    }

    @Benchmark
    public float add(AddState state) {
        state.list.add(1.0f);
        return state.list.get(state.list.size() - 1);
    }

    @Benchmark
    public float get(ReadOnlyState state) {
        return state.list.get(BaseState.SIZE / 2);
    }

    @Benchmark
    public float set(SetState state) {
        return state.list.set(BaseState.SIZE / 2, 1.0f);
    }

    @Benchmark
    public boolean contains(ReadOnlyState state) {
        return state.list.contains(state.list.get(0));
    }

    @Benchmark
    public int indexOf(ReadOnlyState state) {
        return state.list.indexOf(state.list.get(0));
    }

    @Benchmark
    public float removeAt(RemoveAtState state) {
        return state.list.removeAt(BaseState.SIZE / 2);
    }

    @Benchmark
    public float removeLast(RemoveLastState state) {
        return state.list.removeLast();
    }

    @Benchmark
    public float insert(InsertState state) {
        state.list.insert(BaseState.SIZE / 2, 1.0f);
        return state.list.get(BaseState.SIZE / 2);
    }

    @Benchmark
    public int clear(ClearState state) {
        state.list.clear();
        return state.list.size();
    }

    @Benchmark
    public int release(ReleaseState state) {
        state.list.release();
        return state.list.size();
    }

    @Benchmark
    public int toArray(ReadOnlyState state) {
        return state.list.toArray().length;
    }

    @Benchmark
    public float sort(SortState state) {
        state.list.sort();
        return state.list.get(0);
    }

    @Benchmark
    public float reverse(ReverseState state) {
        state.list.reverse();
        return state.list.get(0);
    }

    @Benchmark
    public float forEach(ForEachState state) {
        state.list.forEach(state.procedure);
        return state.sum;
    }

    @Benchmark
    public float iterator(IteratorState state) {
        for (FloatCursor c : state.list) {
            state.sum += c.value;
        }
        return state.sum;
    }

    @Benchmark
    public int hashCode(ReadOnlyState state) {
        return state.list.hashCode();
    }

    @Benchmark
    public boolean equals(EqualsState state) {
        return state.list.equals(state.other);
    }
}
