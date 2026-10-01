package bench.generated.c014;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.yaml.snakeyaml.events.DocumentEndEvent;
import org.yaml.snakeyaml.events.Event;
import org.yaml.snakeyaml.error.Mark;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class DocumentEndEventBenchmark {

    private Mark startMark;
    private Mark endMark;
    private boolean explicitTrue;
    private boolean explicitFalse;
    private DocumentEndEvent explicitTrueEvent;
    private DocumentEndEvent explicitFalseEvent;

    @Setup(Level.Trial)
    public void setUp() {
        int[] buffer = new int[] {'f', 'o', 'o', '\n'};
        startMark = new Mark("bench", 0, 0, 0, buffer, 0);
        endMark = new Mark("bench", buffer.length, 0, buffer.length, buffer, buffer.length);
        explicitTrue = true;
        explicitFalse = false;
        explicitTrueEvent = new DocumentEndEvent(startMark, endMark, explicitTrue);
        explicitFalseEvent = new DocumentEndEvent(startMark, endMark, explicitFalse);
    }

    @Benchmark
    public DocumentEndEvent constructExplicit() {
        return new DocumentEndEvent(startMark, endMark, explicitTrue);
    }

    @Benchmark
    public DocumentEndEvent constructImplicit() {
        return new DocumentEndEvent(startMark, endMark, explicitFalse);
    }

    @Benchmark
    public boolean getExplicitTrue() {
        return explicitTrueEvent.getExplicit();
    }

    @Benchmark
    public boolean getExplicitFalse() {
        return explicitFalseEvent.getExplicit();
    }

    @Benchmark
    public Event.ID getEventId() {
        return explicitTrueEvent.getEventId();
    }

    @Benchmark
    public Mark getStartMark() {
        return explicitTrueEvent.getStartMark();
    }

    @Benchmark
    public Mark getEndMark() {
        return explicitFalseEvent.getEndMark();
    }
}
