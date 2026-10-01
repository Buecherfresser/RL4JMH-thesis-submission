package bench.generated.c019;

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
import org.yaml.snakeyaml.events.SequenceEndEvent;
import org.yaml.snakeyaml.events.Event;
import org.yaml.snakeyaml.error.Mark;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SequenceEndEventBenchmark {

    private Mark startMark;
    private Mark endMark;
    private SequenceEndEvent event;

    @Setup
    public void setup() {
        char[] buffer = new char[0];
        this.startMark = new Mark("start", 0, 0, 0, buffer, 0);
        this.endMark = new Mark("end", 0, 0, 0, buffer, 0);
        this.event = new SequenceEndEvent(startMark, endMark);
    }

    @Benchmark
    public SequenceEndEvent benchmarkConstructor() {
        return new SequenceEndEvent(startMark, endMark);
    }

    @Benchmark
    public Event.ID benchmarkGetEventId() {
        return event.getEventId();
    }

    @Benchmark
    public Mark benchmarkGetStartMark() {
        return event.getStartMark();
    }

    @Benchmark
    public Mark benchmarkGetEndMark() {
        return event.getEndMark();
    }
}
