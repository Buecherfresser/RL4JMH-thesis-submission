package bench.generated.c015;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.DumperOptions.Version;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.events.DocumentStartEvent;
import org.yaml.snakeyaml.events.Event;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class DocumentStartEventBenchmark {

    private Mark startMark;
    private Mark endMark;
    private Map<String, String> tags;
    private Map<String, String> emptyTags;

    private DocumentStartEvent explicitEvent;
    private DocumentStartEvent nonExplicitEvent;
    private DocumentStartEvent versionedEvent;
    private DocumentStartEvent unversionedEvent;
    private DocumentStartEvent taggedEvent;
    private DocumentStartEvent emptyTagsEvent;

    @Setup(Level.Trial)
    public void setUp() {
        startMark = new Mark("bench", 0, 0, 0, new int[] {1}, 0);
        endMark = new Mark("bench", 1, 0, 1, new int[] {1}, 0);

        tags = new HashMap<>();
        tags.put("!e!", "tag:yaml.org,2002:");
        tags.put("!foo!", "tag:example.com,2020:");

        emptyTags = new HashMap<>();

        explicitEvent = new DocumentStartEvent(startMark, endMark, true, Version.V1_1, tags);
        nonExplicitEvent = new DocumentStartEvent(startMark, endMark, false, Version.V1_1, tags);
        versionedEvent = new DocumentStartEvent(startMark, endMark, true, Version.V1_1, tags);
        unversionedEvent = new DocumentStartEvent(startMark, endMark, true, null, tags);
        taggedEvent = new DocumentStartEvent(startMark, endMark, true, Version.V1_1, tags);
        emptyTagsEvent = new DocumentStartEvent(startMark, endMark, true, Version.V1_1, emptyTags);
    }

    @Benchmark
    public Mark getStartMark() {
        return explicitEvent.getStartMark();
    }

    @Benchmark
    public Mark getEndMark() {
        return explicitEvent.getEndMark();
    }

    @Benchmark
    public boolean getExplicitTrue() {
        return explicitEvent.getExplicit();
    }

    @Benchmark
    public boolean getExplicitFalse() {
        return nonExplicitEvent.getExplicit();
    }

    @Benchmark
    public Version getVersionSet() {
        return versionedEvent.getVersion();
    }

    @Benchmark
    public Version getVersionUnset() {
        return unversionedEvent.getVersion();
    }

    @Benchmark
    public Map<String, String> getTags() {
        return taggedEvent.getTags();
    }

    @Benchmark
    public Map<String, String> getTagsEmpty() {
        return emptyTagsEvent.getTags();
    }

    @Benchmark
    public Event.ID getEventId() {
        return explicitEvent.getEventId();
    }

    @Benchmark
    public DocumentStartEvent constructDocumentStartEvent() {
        return new DocumentStartEvent(startMark, endMark, true, Version.V1_1, tags);
    }
}
