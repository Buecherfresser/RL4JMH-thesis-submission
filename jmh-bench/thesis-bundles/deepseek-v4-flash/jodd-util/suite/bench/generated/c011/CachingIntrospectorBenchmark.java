package bench.generated.c011;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import jodd.introspector.CachingIntrospector;
import jodd.introspector.ClassDescriptor;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CachingIntrospectorBenchmark {

    public static class SimpleBean {
        private int id;
        private String name;
        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    public static class ComplexBean {
        private int field1;
        private String field2;
        private double field3;
        private boolean field4;
        private long field5;
        private Object field6;
        private int[] field7;
        private String[] field8;
        public int getField1() { return field1; }
        public void setField1(int field1) { this.field1 = field1; }
        public String getField2() { return field2; }
        public void setField2(String field2) { this.field2 = field2; }
        public double getField3() { return field3; }
        public void setField3(double field3) { this.field3 = field3; }
        public boolean isField4() { return field4; }
        public void setField4(boolean field4) { this.field4 = field4; }
        public long getField5() { return field5; }
        public void setField5(long field5) { this.field5 = field5; }
        public Object getField6() { return field6; }
        public void setField6(Object field6) { this.field6 = field6; }
        public int[] getField7() { return field7; }
        public void setField7(int[] field7) { this.field7 = field7; }
        public String[] getField8() { return field8; }
        public void setField8(String[] field8) { this.field8 = field8; }
        public void doSomething() {}
        public String doSomethingElse(int x) { return ""; }
    }

    @State(Scope.Benchmark)
    public static class WarmState {
        CachingIntrospector introspector;
        Class<?> simpleClass;
        Class<?> complexClass;
        Class<?> stringClass;
        Class<?> arrayListClass;

        @Setup(Level.Trial)
        public void setup() {
            introspector = new CachingIntrospector();
            simpleClass = SimpleBean.class;
            complexClass = ComplexBean.class;
            stringClass = String.class;
            arrayListClass = ArrayList.class;
            introspector.lookup(simpleClass);
            introspector.lookup(complexClass);
            introspector.lookup(stringClass);
            introspector.lookup(arrayListClass);
        }
    }

    @State(Scope.Benchmark)
    public static class ColdState {
        Class<?> simpleClass;
        Class<?> complexClass;

        @Setup(Level.Trial)
        public void setup() {
            simpleClass = SimpleBean.class;
            complexClass = ComplexBean.class;
        }
    }

    @Benchmark
    public ClassDescriptor lookupSimpleWarm(WarmState state) {
        return state.introspector.lookup(state.simpleClass);
    }

    @Benchmark
    public ClassDescriptor lookupComplexWarm(WarmState state) {
        return state.introspector.lookup(state.complexClass);
    }

    @Benchmark
    public ClassDescriptor lookupStringWarm(WarmState state) {
        return state.introspector.lookup(state.stringClass);
    }

    @Benchmark
    public ClassDescriptor lookupSimpleCold(ColdState state) {
        CachingIntrospector introspector = new CachingIntrospector();
        return introspector.lookup(state.simpleClass);
    }

    @Benchmark
    public ClassDescriptor lookupComplexCold(ColdState state) {
        CachingIntrospector introspector = new CachingIntrospector();
        return introspector.lookup(state.complexClass);
    }

    @Benchmark
    public void reset(WarmState state, Blackhole bh) {
        state.introspector.reset();
        bh.consume(state.introspector);
    }
}
