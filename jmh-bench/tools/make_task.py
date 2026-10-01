"""Internal scaffolder: emit a task's directory structure from a single inline spec.

This is a development helper for batch-authoring tasks. Each `Task` produces
five files (task.yaml + SUT + reference benchmark + 1 regression patch +
optional test). The spec object holds raw Java source strings.

Run as ``python tools/make_task.py`` to create all tasks declared at the
bottom of this file.
"""

from __future__ import annotations

import argparse
import sys
import textwrap
from dataclasses import dataclass, field
from pathlib import Path

REPO = Path(__file__).resolve().parent.parent
TASKS = REPO / "dataset" / "tasks"


@dataclass
class TaskSpec:
    instance_id: str
    target_class: str
    sut_class_name: str
    sut_source: str
    regression_id: str
    regression_patch: str
    regression_description: str
    bench_source: str
    tags: list[str] = field(default_factory=list)
    expected_input_shapes: list[str] = field(default_factory=list)
    junit_source: str | None = None

    @property
    def task_dir(self) -> Path:
        return TASKS / self.instance_id


def emit(spec: TaskSpec) -> None:
    d = spec.task_dir
    if d.exists():
        print(f"skip (exists): {spec.instance_id}")
        return
    (d / "src" / "main" / "java" / "bench").mkdir(parents=True)
    (d / "regressions").mkdir()
    (d / "reference").mkdir()

    yaml_lines = [
        f"instance_id: {spec.instance_id}",
        f"target_class: {spec.target_class}",
        f"tags: [{', '.join(spec.tags)}]",
    ]
    if spec.expected_input_shapes:
        yaml_lines.append("expected_input_shapes:")
        for s in spec.expected_input_shapes:
            yaml_lines.append(f"  - {s!r}")
    yaml_lines += [
        "regressions:",
        f"  - id: {spec.regression_id}",
        f"    patch: regressions/{spec.regression_id}.patch",
        f"    description: {spec.regression_description}",
    ]
    if spec.junit_source:
        (d / "tests").mkdir(exist_ok=True)
        yaml_lines.insert(
            -len(yaml_lines) + 5,
            f"junit_test_path: tests/{spec.sut_class_name}Test.java",
        )
        (d / "tests" / f"{spec.sut_class_name}Test.java").write_text(spec.junit_source)

    (d / "task.yaml").write_text("\n".join(yaml_lines) + "\n")
    (d / "src" / "main" / "java" / "bench" / f"{spec.sut_class_name}.java").write_text(spec.sut_source)
    (d / "regressions" / f"{spec.regression_id}.patch").write_text(spec.regression_patch)
    (d / "reference" / f"{spec.sut_class_name}Benchmark.java").write_text(spec.bench_source)
    print(f"wrote: {spec.instance_id}")


def _bench_template(
    sut_class: str,
    *,
    state_decls: str = "",
    setup_body: str = "",
    bench_body: str = "",
    bench_return: str = "long",
    bench_name: str = "run",
    extra_imports: str = "",
    output_unit: str = "MICROSECONDS",
) -> str:
    return f"""package bench.generated;

import bench.{sut_class};
{extra_imports}
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.{output_unit})
@Fork(1)
@Warmup(iterations = 2, time = 1)
@Measurement(iterations = 3, time = 1)
@State(Scope.Benchmark)
public class {sut_class}Benchmark {{

{textwrap.indent(state_decls, '    ')}

    @Setup
    public void setup() {{
{textwrap.indent(setup_body, '        ')}
    }}

    @Benchmark
    public {bench_return} {bench_name}() {{
{textwrap.indent(bench_body, '        ')}
    }}
}}
"""


SPECS: list[TaskSpec] = []


def define(spec: TaskSpec) -> None:
    SPECS.append(spec)


# ---------------------------------------------------------------------------
# 17. linkedlist_random_access_001
# ---------------------------------------------------------------------------
define(TaskSpec(
    instance_id="linkedlist_random_access_001",
    target_class="bench.IndexedSum",
    sut_class_name="IndexedSum",
    tags=["collections", "iterators"],
    expected_input_shapes=["LinkedList size = 5 000"],
    sut_source=(
        "package bench;\n\n"
        "import java.util.Iterator;\n"
        "import java.util.List;\n\n"
        "public final class IndexedSum {\n\n"
        "    private IndexedSum() {}\n\n"
        "    /** Sum the list via Iterator (O(n) regardless of list implementation). */\n"
        "    public static long sum(List<Integer> list) {\n"
        "        long total = 0L;\n"
        "        Iterator<Integer> it = list.iterator();\n"
        "        while (it.hasNext()) {\n"
        "            total += it.next().longValue();\n"
        "        }\n"
        "        return total;\n"
        "    }\n"
        "}\n"
    ),
    regression_id="index_based_get",
    regression_description="switches Iterator walk to index-based list.get(i), which is O(n) on LinkedList",
    regression_patch=(
        "--- a/bench/IndexedSum.java\n"
        "+++ b/bench/IndexedSum.java\n"
        "@@ -1,16 +1,15 @@\n"
        " package bench;\n"
        " \n"
        "-import java.util.Iterator;\n"
        " import java.util.List;\n"
        " \n"
        " public final class IndexedSum {\n"
        " \n"
        "     private IndexedSum() {}\n"
        " \n"
        "     /** Sum the list via Iterator (O(n) regardless of list implementation). */\n"
        "     public static long sum(List<Integer> list) {\n"
        "         long total = 0L;\n"
        "-        Iterator<Integer> it = list.iterator();\n"
        "-        while (it.hasNext()) {\n"
        "-            total += it.next().longValue();\n"
        "+        for (int i = 0, n = list.size(); i < n; i++) {\n"
        "+            total += list.get(i).longValue();\n"
        "         }\n"
        "         return total;\n"
        "     }\n"
        " }\n"
    ),
    bench_source=_bench_template(
        "IndexedSum",
        extra_imports="import java.util.LinkedList;\nimport java.util.List;\nimport java.util.Random;",
        state_decls="@Param({\"5000\"})\npublic int size;\n\nprivate List<Integer> list;",
        setup_body=(
            "Random rng = new Random(31);\n"
            "list = new LinkedList<>();\n"
            "for (int i = 0; i < size; i++) {\n"
            "    list.add(rng.nextInt());\n"
            "}"
        ),
        bench_body="return IndexedSum.sum(list);",
        bench_return="long",
        bench_name="sum",
        output_unit="MILLISECONDS",
    ),
))

# ---------------------------------------------------------------------------
# 18. treemap_lookup_001
# ---------------------------------------------------------------------------
define(TaskSpec(
    instance_id="treemap_lookup_001",
    target_class="bench.MapLookup",
    sut_class_name="MapLookup",
    tags=["collections", "maps"],
    expected_input_shapes=["map size = 50 000, lookups = 5 000"],
    sut_source=(
        "package bench;\n\n"
        "import java.util.HashMap;\n"
        "import java.util.List;\n"
        "import java.util.Map;\n\n"
        "public final class MapLookup {\n\n"
        "    private MapLookup() {}\n\n"
        "    /** Sum values returned by map.get(key) for each requested key. */\n"
        "    public static long sumGet(Map<String, Long> map, List<String> keys) {\n"
        "        long total = 0L;\n"
        "        for (int i = 0, n = keys.size(); i < n; i++) {\n"
        "            Long v = map.get(keys.get(i));\n"
        "            if (v != null) total += v.longValue();\n"
        "        }\n"
        "        return total;\n"
        "    }\n\n"
        "    public static Map<String, Long> newFastMap(int capacity) {\n"
        "        return new HashMap<>(capacity);\n"
        "    }\n"
        "}\n"
    ),
    regression_id="treemap_factory",
    regression_description="replaces HashMap with TreeMap in newFastMap, turning O(1) lookups into O(log n)",
    regression_patch=(
        "--- a/bench/MapLookup.java\n"
        "+++ b/bench/MapLookup.java\n"
        "@@ -1,8 +1,8 @@\n"
        " package bench;\n"
        " \n"
        "-import java.util.HashMap;\n"
        " import java.util.List;\n"
        " import java.util.Map;\n"
        "+import java.util.TreeMap;\n"
        " \n"
        " public final class MapLookup {\n"
        " \n"
        "     private MapLookup() {}\n"
        "@@ -16,7 +16,7 @@ public final class MapLookup {\n"
        "         return total;\n"
        "     }\n"
        " \n"
        "     public static Map<String, Long> newFastMap(int capacity) {\n"
        "-        return new HashMap<>(capacity);\n"
        "+        return new TreeMap<>();\n"
        "     }\n"
        " }\n"
    ),
    bench_source=_bench_template(
        "MapLookup",
        extra_imports="import java.util.ArrayList;\nimport java.util.List;\nimport java.util.Map;\nimport java.util.Random;",
        state_decls=(
            "@Param({\"50000\"})\npublic int mapSize;\n\n"
            "@Param({\"5000\"})\npublic int probes;\n\n"
            "private Map<String, Long> map;\n"
            "private List<String> keys;"
        ),
        setup_body=(
            "Random rng = new Random(37);\n"
            "map = MapLookup.newFastMap(mapSize * 2);\n"
            "List<String> all = new ArrayList<>(mapSize);\n"
            "for (int i = 0; i < mapSize; i++) {\n"
            "    String k = \"k-\" + i;\n"
            "    map.put(k, (long) i);\n"
            "    all.add(k);\n"
            "}\n"
            "keys = new ArrayList<>(probes);\n"
            "for (int i = 0; i < probes; i++) {\n"
            "    keys.add(all.get(rng.nextInt(mapSize)));\n"
            "}"
        ),
        bench_body="return MapLookup.sumGet(map, keys);",
        bench_return="long",
        bench_name="sumGet",
        output_unit="MICROSECONDS",
    ),
))

# ---------------------------------------------------------------------------
# 19. string_split_001
# ---------------------------------------------------------------------------
define(TaskSpec(
    instance_id="string_split_001",
    target_class="bench.Splitter",
    sut_class_name="Splitter",
    tags=["strings"],
    expected_input_shapes=["string length = 100 000"],
    sut_source=(
        "package bench;\n\n"
        "public final class Splitter {\n\n"
        "    private Splitter() {}\n\n"
        "    /** Count tokens delimited by `sep` using a single pass. */\n"
        "    public static int count(String s, char sep) {\n"
        "        if (s.isEmpty()) return 0;\n"
        "        int count = 1;\n"
        "        for (int i = 0, n = s.length(); i < n; i++) {\n"
        "            if (s.charAt(i) == sep) count++;\n"
        "        }\n"
        "        return count;\n"
        "    }\n"
        "}\n"
    ),
    regression_id="regex_split",
    regression_description="replaces single-pass char scan with String.split (compiles a regex, allocates String[])",
    regression_patch=(
        "--- a/bench/Splitter.java\n"
        "+++ b/bench/Splitter.java\n"
        "@@ -4,11 +4,7 @@ public final class Splitter {\n"
        "     private Splitter() {}\n"
        " \n"
        "     /** Count tokens delimited by `sep` using a single pass. */\n"
        "     public static int count(String s, char sep) {\n"
        "         if (s.isEmpty()) return 0;\n"
        "-        int count = 1;\n"
        "-        for (int i = 0, n = s.length(); i < n; i++) {\n"
        "-            if (s.charAt(i) == sep) count++;\n"
        "-        }\n"
        "-        return count;\n"
        "+        return s.split(String.valueOf(sep), -1).length;\n"
        "     }\n"
        " }\n"
    ),
    bench_source=_bench_template(
        "Splitter",
        extra_imports="import java.util.Random;",
        state_decls=(
            "@Param({\"100000\"})\npublic int length;\n\n"
            "private String text;"
        ),
        setup_body=(
            "Random rng = new Random(41);\n"
            "StringBuilder sb = new StringBuilder(length);\n"
            "for (int i = 0; i < length; i++) {\n"
            "    sb.append(rng.nextInt(10) == 0 ? ',' : (char) ('a' + rng.nextInt(26)));\n"
            "}\n"
            "text = sb.toString();"
        ),
        bench_body="return Splitter.count(text, ',');",
        bench_return="int",
        bench_name="count",
        output_unit="MICROSECONDS",
    ),
))

# ---------------------------------------------------------------------------
# 20. byte_buffer_wrap_001
# ---------------------------------------------------------------------------
define(TaskSpec(
    instance_id="byte_buffer_wrap_001",
    target_class="bench.IntsReader",
    sut_class_name="IntsReader",
    tags=["memory", "nio"],
    expected_input_shapes=["byte[] length = 4 000 000 (1M ints)"],
    sut_source=(
        "package bench;\n\n"
        "import java.nio.ByteBuffer;\n\n"
        "public final class IntsReader {\n\n"
        "    private IntsReader() {}\n\n"
        "    /** Sum BE-encoded ints from {@code buf} using a single ByteBuffer view. */\n"
        "    public static long sum(byte[] buf) {\n"
        "        ByteBuffer bb = ByteBuffer.wrap(buf);\n"
        "        long total = 0L;\n"
        "        int n = buf.length / 4;\n"
        "        for (int i = 0; i < n; i++) {\n"
        "            total += bb.getInt();\n"
        "        }\n"
        "        return total;\n"
        "    }\n"
        "}\n"
    ),
    regression_id="rewrap_each_element",
    regression_description="wraps the byte[] in a fresh ByteBuffer on every iteration",
    regression_patch=(
        "--- a/bench/IntsReader.java\n"
        "+++ b/bench/IntsReader.java\n"
        "@@ -5,12 +5,11 @@ import java.nio.ByteBuffer;\n"
        " public final class IntsReader {\n"
        " \n"
        "     private IntsReader() {}\n"
        " \n"
        "     /** Sum BE-encoded ints from {@code buf} using a single ByteBuffer view. */\n"
        "     public static long sum(byte[] buf) {\n"
        "-        ByteBuffer bb = ByteBuffer.wrap(buf);\n"
        "         long total = 0L;\n"
        "         int n = buf.length / 4;\n"
        "         for (int i = 0; i < n; i++) {\n"
        "-            total += bb.getInt();\n"
        "+            total += ByteBuffer.wrap(buf, i * 4, 4).getInt();\n"
        "         }\n"
        "         return total;\n"
        "     }\n"
        " }\n"
    ),
    bench_source=_bench_template(
        "IntsReader",
        extra_imports="import java.util.Random;",
        state_decls=(
            "@Param({\"4000000\"})\npublic int size;\n\n"
            "private byte[] data;"
        ),
        setup_body=(
            "Random rng = new Random(43);\n"
            "data = new byte[size];\n"
            "rng.nextBytes(data);"
        ),
        bench_body="return IntsReader.sum(data);",
        bench_return="long",
        bench_name="sum",
        output_unit="MILLISECONDS",
    ),
))

# ---------------------------------------------------------------------------
# 21. integer_log2_001
# ---------------------------------------------------------------------------
define(TaskSpec(
    instance_id="integer_log2_001",
    target_class="bench.Log2",
    sut_class_name="Log2",
    tags=["numerics"],
    expected_input_shapes=["n = 1 000 000"],
    sut_source=(
        "package bench;\n\n"
        "public final class Log2 {\n\n"
        "    private Log2() {}\n\n"
        "    /** Sum floor(log2(i)) for i in [1, n) using bit tricks. */\n"
        "    public static long sumLog2(int n) {\n"
        "        long acc = 0L;\n"
        "        for (int i = 1; i < n; i++) {\n"
        "            acc += 31 - Integer.numberOfLeadingZeros(i);\n"
        "        }\n"
        "        return acc;\n"
        "    }\n"
        "}\n"
    ),
    regression_id="math_log_divide",
    regression_description="replaces bit-trick log2 with Math.log(i)/Math.log(2)",
    regression_patch=(
        "--- a/bench/Log2.java\n"
        "+++ b/bench/Log2.java\n"
        "@@ -6,7 +6,7 @@ public final class Log2 {\n"
        "     public static long sumLog2(int n) {\n"
        "         long acc = 0L;\n"
        "         for (int i = 1; i < n; i++) {\n"
        "-            acc += 31 - Integer.numberOfLeadingZeros(i);\n"
        "+            acc += (long) (Math.log(i) / Math.log(2));\n"
        "         }\n"
        "         return acc;\n"
        "     }\n"
        " }\n"
    ),
    bench_source=_bench_template(
        "Log2",
        state_decls="@Param({\"1000000\"})\npublic int n;",
        setup_body="// no-op",
        bench_body="return Log2.sumLog2(n);",
        bench_return="long",
        bench_name="sumLog2",
        output_unit="MILLISECONDS",
    ),
))

# ---------------------------------------------------------------------------
# 22. modexp_001
# ---------------------------------------------------------------------------
define(TaskSpec(
    instance_id="modexp_001",
    target_class="bench.ModExp",
    sut_class_name="ModExp",
    tags=["algorithms", "numerics"],
    expected_input_shapes=["exp = 100 000"],
    sut_source=(
        "package bench;\n\n"
        "public final class ModExp {\n\n"
        "    private ModExp() {}\n\n"
        "    /** Modular exponentiation via fast doubling. */\n"
        "    public static long pow(long base, long exp, long mod) {\n"
        "        long result = 1L;\n"
        "        long b = base % mod;\n"
        "        while (exp > 0) {\n"
        "            if ((exp & 1L) == 1L) {\n"
        "                result = (result * b) % mod;\n"
        "            }\n"
        "            b = (b * b) % mod;\n"
        "            exp >>>= 1;\n"
        "        }\n"
        "        return result;\n"
        "    }\n"
        "}\n"
    ),
    regression_id="linear_pow",
    regression_description="replaces fast-doubling exponent walk with linear multiply",
    regression_patch=(
        "--- a/bench/ModExp.java\n"
        "+++ b/bench/ModExp.java\n"
        "@@ -4,15 +4,11 @@ public final class ModExp {\n"
        "     private ModExp() {}\n"
        " \n"
        "     /** Modular exponentiation via fast doubling. */\n"
        "     public static long pow(long base, long exp, long mod) {\n"
        "         long result = 1L;\n"
        "         long b = base % mod;\n"
        "-        while (exp > 0) {\n"
        "-            if ((exp & 1L) == 1L) {\n"
        "-                result = (result * b) % mod;\n"
        "-            }\n"
        "-            b = (b * b) % mod;\n"
        "-            exp >>>= 1;\n"
        "+        for (long i = 0; i < exp; i++) {\n"
        "+            result = (result * b) % mod;\n"
        "         }\n"
        "         return result;\n"
        "     }\n"
        " }\n"
    ),
    bench_source=_bench_template(
        "ModExp",
        state_decls=(
            "@Param({\"100000\"})\npublic long exp;\n\n"
            "private long base;\n"
            "private long mod;"
        ),
        setup_body="base = 1_234_567L;\nmod = 1_000_000_007L;",
        bench_body="return ModExp.pow(base, exp, mod);",
        bench_return="long",
        bench_name="pow",
        output_unit="MICROSECONDS",
    ),
))

# ---------------------------------------------------------------------------
# 23. enum_switch_001
# ---------------------------------------------------------------------------
define(TaskSpec(
    instance_id="enum_switch_001",
    target_class="bench.Tag",
    sut_class_name="Tag",
    tags=["enums", "branch"],
    expected_input_shapes=["array length = 500 000"],
    sut_source=(
        "package bench;\n\n"
        "public final class Tag {\n\n"
        "    public enum Kind { ALPHA, BETA, GAMMA, DELTA }\n\n"
        "    private Tag() {}\n\n"
        "    /** Score an array of tags via a dense switch. */\n"
        "    public static long score(Kind[] arr) {\n"
        "        long total = 0L;\n"
        "        for (int i = 0, n = arr.length; i < n; i++) {\n"
        "            switch (arr[i]) {\n"
        "                case ALPHA: total += 1; break;\n"
        "                case BETA:  total += 2; break;\n"
        "                case GAMMA: total += 3; break;\n"
        "                case DELTA: total += 4; break;\n"
        "            }\n"
        "        }\n"
        "        return total;\n"
        "    }\n"
        "}\n"
    ),
    regression_id="hashmap_table",
    regression_description="replaces dense switch with a HashMap<Kind, Integer> lookup",
    regression_patch=(
        "--- a/bench/Tag.java\n"
        "+++ b/bench/Tag.java\n"
        "@@ -1,21 +1,26 @@\n"
        " package bench;\n"
        " \n"
        "+import java.util.EnumMap;\n"
        "+import java.util.HashMap;\n"
        "+import java.util.Map;\n"
        "+\n"
        " public final class Tag {\n"
        " \n"
        "     public enum Kind { ALPHA, BETA, GAMMA, DELTA }\n"
        " \n"
        "+    private static final Map<Kind, Integer> TABLE = new HashMap<>();\n"
        "+    static {\n"
        "+        TABLE.put(Kind.ALPHA, 1);\n"
        "+        TABLE.put(Kind.BETA, 2);\n"
        "+        TABLE.put(Kind.GAMMA, 3);\n"
        "+        TABLE.put(Kind.DELTA, 4);\n"
        "+    }\n"
        "+\n"
        "     private Tag() {}\n"
        " \n"
        "     /** Score an array of tags via a dense switch. */\n"
        "     public static long score(Kind[] arr) {\n"
        "         long total = 0L;\n"
        "         for (int i = 0, n = arr.length; i < n; i++) {\n"
        "-            switch (arr[i]) {\n"
        "-                case ALPHA: total += 1; break;\n"
        "-                case BETA:  total += 2; break;\n"
        "-                case GAMMA: total += 3; break;\n"
        "-                case DELTA: total += 4; break;\n"
        "-            }\n"
        "+            total += TABLE.get(arr[i]).longValue();\n"
        "         }\n"
        "         return total;\n"
        "     }\n"
        " }\n"
    ),
    bench_source=_bench_template(
        "Tag",
        extra_imports="import java.util.Random;",
        state_decls=(
            "@Param({\"500000\"})\npublic int size;\n\n"
            "private Tag.Kind[] tags;"
        ),
        setup_body=(
            "Random rng = new Random(47);\n"
            "Tag.Kind[] all = Tag.Kind.values();\n"
            "tags = new Tag.Kind[size];\n"
            "for (int i = 0; i < size; i++) tags[i] = all[rng.nextInt(all.length)];"
        ),
        bench_body="return Tag.score(tags);",
        bench_return="long",
        bench_name="score",
        output_unit="MICROSECONDS",
    ),
))

# ---------------------------------------------------------------------------
# 24. levenshtein_dp_001
# ---------------------------------------------------------------------------
define(TaskSpec(
    instance_id="levenshtein_dp_001",
    target_class="bench.EditDistance",
    sut_class_name="EditDistance",
    tags=["algorithms", "dp"],
    expected_input_shapes=["string length = 20"],
    sut_source=(
        "package bench;\n\n"
        "public final class EditDistance {\n\n"
        "    private EditDistance() {}\n\n"
        "    /** Levenshtein distance via 1D rolling-row DP. */\n"
        "    public static int distance(String a, String b) {\n"
        "        int n = a.length();\n"
        "        int m = b.length();\n"
        "        if (n == 0) return m;\n"
        "        if (m == 0) return n;\n"
        "        int[] prev = new int[m + 1];\n"
        "        int[] cur = new int[m + 1];\n"
        "        for (int j = 0; j <= m; j++) prev[j] = j;\n"
        "        for (int i = 1; i <= n; i++) {\n"
        "            cur[0] = i;\n"
        "            char ca = a.charAt(i - 1);\n"
        "            for (int j = 1; j <= m; j++) {\n"
        "                int cost = ca == b.charAt(j - 1) ? 0 : 1;\n"
        "                cur[j] = Math.min(Math.min(cur[j - 1] + 1, prev[j] + 1), prev[j - 1] + cost);\n"
        "            }\n"
        "            int[] tmp = prev; prev = cur; cur = tmp;\n"
        "        }\n"
        "        return prev[m];\n"
        "    }\n"
        "}\n"
    ),
    regression_id="naive_recursion",
    regression_description="replaces DP with naive recursion (exponential time)",
    regression_patch=(
        "--- a/bench/EditDistance.java\n"
        "+++ b/bench/EditDistance.java\n"
        "@@ -2,22 +2,18 @@ package bench;\n"
        " \n"
        " public final class EditDistance {\n"
        " \n"
        "     private EditDistance() {}\n"
        " \n"
        "     /** Levenshtein distance via 1D rolling-row DP. */\n"
        "     public static int distance(String a, String b) {\n"
        "+        return rec(a, b, a.length(), b.length());\n"
        "+    }\n"
        "+\n"
        "+    private static int rec(String a, String b, int n, int m) {\n"
        "         if (n == 0) return m;\n"
        "         if (m == 0) return n;\n"
        "-        int[] prev = new int[m + 1];\n"
        "-        int[] cur = new int[m + 1];\n"
        "-        for (int j = 0; j <= m; j++) prev[j] = j;\n"
        "-        for (int i = 1; i <= n; i++) {\n"
        "-            cur[0] = i;\n"
        "-            char ca = a.charAt(i - 1);\n"
        "-            for (int j = 1; j <= m; j++) {\n"
        "-                int cost = ca == b.charAt(j - 1) ? 0 : 1;\n"
        "-                cur[j] = Math.min(Math.min(cur[j - 1] + 1, prev[j] + 1), prev[j - 1] + cost);\n"
        "-            }\n"
        "-            int[] tmp = prev; prev = cur; cur = tmp;\n"
        "-        }\n"
        "-        return prev[m];\n"
        "+        int cost = a.charAt(n - 1) == b.charAt(m - 1) ? 0 : 1;\n"
        "+        return Math.min(Math.min(rec(a, b, n - 1, m) + 1, rec(a, b, n, m - 1) + 1),\n"
        "+                        rec(a, b, n - 1, m - 1) + cost);\n"
        "     }\n"
        " }\n"
    ),
    bench_source=_bench_template(
        "EditDistance",
        state_decls=(
            "@Param({\"kitten\"})\npublic String a;\n\n"
            "@Param({\"sitting\"})\npublic String b;"
        ),
        setup_body="// inputs supplied via @Param",
        bench_body="return EditDistance.distance(a, b);",
        bench_return="int",
        bench_name="distance",
        output_unit="MICROSECONDS",
    ),
))

# ---------------------------------------------------------------------------
# 25. prime_sieve_001
# ---------------------------------------------------------------------------
define(TaskSpec(
    instance_id="prime_sieve_001",
    target_class="bench.Primes",
    sut_class_name="Primes",
    tags=["algorithms", "numerics"],
    expected_input_shapes=["limit = 100 000"],
    sut_source=(
        "package bench;\n\n"
        "public final class Primes {\n\n"
        "    private Primes() {}\n\n"
        "    /** Count primes below {@code limit} via Sieve of Eratosthenes. */\n"
        "    public static int count(int limit) {\n"
        "        if (limit < 3) return limit == 2 ? 1 : 0;\n"
        "        boolean[] composite = new boolean[limit];\n"
        "        int count = 0;\n"
        "        for (int i = 2; i < limit; i++) {\n"
        "            if (!composite[i]) {\n"
        "                count++;\n"
        "                for (long j = (long) i * i; j < limit; j += i) {\n"
        "                    composite[(int) j] = true;\n"
        "                }\n"
        "            }\n"
        "        }\n"
        "        return count;\n"
        "    }\n"
        "}\n"
    ),
    regression_id="trial_division",
    regression_description="replaces sieve with trial-division primality testing",
    regression_patch=(
        "--- a/bench/Primes.java\n"
        "+++ b/bench/Primes.java\n"
        "@@ -4,18 +4,15 @@ public final class Primes {\n"
        "     private Primes() {}\n"
        " \n"
        "     /** Count primes below {@code limit} via Sieve of Eratosthenes. */\n"
        "     public static int count(int limit) {\n"
        "         if (limit < 3) return limit == 2 ? 1 : 0;\n"
        "-        boolean[] composite = new boolean[limit];\n"
        "         int count = 0;\n"
        "         for (int i = 2; i < limit; i++) {\n"
        "-            if (!composite[i]) {\n"
        "-                count++;\n"
        "-                for (long j = (long) i * i; j < limit; j += i) {\n"
        "-                    composite[(int) j] = true;\n"
        "-                }\n"
        "+            boolean prime = true;\n"
        "+            for (int d = 2; (long) d * d <= i; d++) {\n"
        "+                if (i % d == 0) { prime = false; break; }\n"
        "             }\n"
        "+            if (prime) count++;\n"
        "         }\n"
        "         return count;\n"
        "     }\n"
        " }\n"
    ),
    bench_source=_bench_template(
        "Primes",
        state_decls="@Param({\"100000\"})\npublic int limit;",
        setup_body="// no-op",
        bench_body="return Primes.count(limit);",
        bench_return="int",
        bench_name="count",
        output_unit="MILLISECONDS",
    ),
))

# ---------------------------------------------------------------------------
# 26. set_contains_linear_001
# ---------------------------------------------------------------------------
define(TaskSpec(
    instance_id="set_contains_linear_001",
    target_class="bench.Intersect",
    sut_class_name="Intersect",
    tags=["collections", "sets"],
    expected_input_shapes=["|a| = 200, |b| = 20 000"],
    sut_source=(
        "package bench;\n\n"
        "import java.util.Set;\n\n"
        "public final class Intersect {\n\n"
        "    private Intersect() {}\n\n"
        "    /** Count common elements; iterates the smaller set. */\n"
        "    public static int sizeOfIntersection(Set<Integer> a, Set<Integer> b) {\n"
        "        Set<Integer> smaller = a.size() <= b.size() ? a : b;\n"
        "        Set<Integer> larger  = smaller == a ? b : a;\n"
        "        int hits = 0;\n"
        "        for (Integer v : smaller) {\n"
        "            if (larger.contains(v)) hits++;\n"
        "        }\n"
        "        return hits;\n"
        "    }\n"
        "}\n"
    ),
    regression_id="iterate_larger",
    regression_description="iterates the larger set instead of the smaller one",
    regression_patch=(
        "--- a/bench/Intersect.java\n"
        "+++ b/bench/Intersect.java\n"
        "@@ -6,12 +6,11 @@ public final class Intersect {\n"
        "     private Intersect() {}\n"
        " \n"
        "     /** Count common elements; iterates the smaller set. */\n"
        "     public static int sizeOfIntersection(Set<Integer> a, Set<Integer> b) {\n"
        "-        Set<Integer> smaller = a.size() <= b.size() ? a : b;\n"
        "-        Set<Integer> larger  = smaller == a ? b : a;\n"
        "+        Set<Integer> outer = a.size() >= b.size() ? a : b;\n"
        "+        Set<Integer> inner = outer == a ? b : a;\n"
        "         int hits = 0;\n"
        "-        for (Integer v : smaller) {\n"
        "-            if (larger.contains(v)) hits++;\n"
        "+        for (Integer v : outer) {\n"
        "+            if (inner.contains(v)) hits++;\n"
        "         }\n"
        "         return hits;\n"
        "     }\n"
        " }\n"
    ),
    bench_source=_bench_template(
        "Intersect",
        extra_imports="import java.util.HashSet;\nimport java.util.Random;\nimport java.util.Set;",
        state_decls=(
            "@Param({\"200\"})\npublic int small;\n\n"
            "@Param({\"20000\"})\npublic int big;\n\n"
            "private Set<Integer> a;\n"
            "private Set<Integer> b;"
        ),
        setup_body=(
            "Random rng = new Random(53);\n"
            "a = new HashSet<>(small * 2);\n"
            "for (int i = 0; i < small; i++) a.add(rng.nextInt());\n"
            "b = new HashSet<>(big * 2);\n"
            "for (int i = 0; i < big; i++) b.add(rng.nextInt());"
        ),
        bench_body="return Intersect.sizeOfIntersection(a, b);",
        bench_return="int",
        bench_name="size",
        output_unit="MICROSECONDS",
    ),
))

# ---------------------------------------------------------------------------
# 27. string_replace_001
# ---------------------------------------------------------------------------
define(TaskSpec(
    instance_id="string_replace_001",
    target_class="bench.Replacer",
    sut_class_name="Replacer",
    tags=["strings", "regex"],
    expected_input_shapes=["string length = 50 000"],
    sut_source=(
        "package bench;\n\n"
        "public final class Replacer {\n\n"
        "    private Replacer() {}\n\n"
        "    /** Remove ASCII spaces via a single-pass scan. */\n"
        "    public static String stripSpaces(String s) {\n"
        "        int n = s.length();\n"
        "        StringBuilder sb = new StringBuilder(n);\n"
        "        for (int i = 0; i < n; i++) {\n"
        "            char c = s.charAt(i);\n"
        "            if (c != ' ') sb.append(c);\n"
        "        }\n"
        "        return sb.toString();\n"
        "    }\n"
        "}\n"
    ),
    regression_id="replace_all_regex",
    regression_description="replaces single-pass scan with replaceAll regex",
    regression_patch=(
        "--- a/bench/Replacer.java\n"
        "+++ b/bench/Replacer.java\n"
        "@@ -4,12 +4,7 @@ public final class Replacer {\n"
        "     private Replacer() {}\n"
        " \n"
        "     /** Remove ASCII spaces via a single-pass scan. */\n"
        "     public static String stripSpaces(String s) {\n"
        "-        int n = s.length();\n"
        "-        StringBuilder sb = new StringBuilder(n);\n"
        "-        for (int i = 0; i < n; i++) {\n"
        "-            char c = s.charAt(i);\n"
        "-            if (c != ' ') sb.append(c);\n"
        "-        }\n"
        "-        return sb.toString();\n"
        "+        return s.replaceAll(\" \", \"\");\n"
        "     }\n"
        " }\n"
    ),
    bench_source=_bench_template(
        "Replacer",
        extra_imports="import java.util.Random;",
        state_decls=(
            "@Param({\"50000\"})\npublic int length;\n\n"
            "private String text;"
        ),
        setup_body=(
            "Random rng = new Random(59);\n"
            "StringBuilder sb = new StringBuilder(length);\n"
            "for (int i = 0; i < length; i++) {\n"
            "    sb.append(rng.nextInt(5) == 0 ? ' ' : (char) ('a' + rng.nextInt(26)));\n"
            "}\n"
            "text = sb.toString();"
        ),
        bench_body="return Replacer.stripSpaces(text);",
        bench_return="String",
        bench_name="stripSpaces",
        output_unit="MICROSECONDS",
    ),
))

# ---------------------------------------------------------------------------
# 28. sort_stability_001
# ---------------------------------------------------------------------------
define(TaskSpec(
    instance_id="sort_stability_001",
    target_class="bench.SortShort",
    sut_class_name="SortShort",
    tags=["algorithms", "boxing"],
    expected_input_shapes=["1024 small arrays, length 32 each"],
    sut_source=(
        "package bench;\n\n"
        "import java.util.Arrays;\n\n"
        "public final class SortShort {\n\n"
        "    private SortShort() {}\n\n"
        "    /** Sort each small int[] and return their summed first elements. */\n"
        "    public static long sortAndSum(int[][] arrays) {\n"
        "        long total = 0L;\n"
        "        for (int i = 0; i < arrays.length; i++) {\n"
        "            int[] a = arrays[i].clone();\n"
        "            Arrays.sort(a);\n"
        "            total += a[0];\n"
        "        }\n"
        "        return total;\n"
        "    }\n"
        "}\n"
    ),
    regression_id="boxed_sort",
    regression_description="boxes each int[] to Integer[] before sorting, using the object-sort path",
    regression_patch=(
        "--- a/bench/SortShort.java\n"
        "+++ b/bench/SortShort.java\n"
        "@@ -6,12 +6,16 @@ public final class SortShort {\n"
        "     private SortShort() {}\n"
        " \n"
        "     /** Sort each small int[] and return their summed first elements. */\n"
        "     public static long sortAndSum(int[][] arrays) {\n"
        "         long total = 0L;\n"
        "         for (int i = 0; i < arrays.length; i++) {\n"
        "-            int[] a = arrays[i].clone();\n"
        "-            Arrays.sort(a);\n"
        "-            total += a[0];\n"
        "+            int[] src = arrays[i];\n"
        "+            Integer[] boxed = new Integer[src.length];\n"
        "+            for (int j = 0; j < src.length; j++) boxed[j] = src[j];\n"
        "+            Arrays.sort(boxed);\n"
        "+            total += boxed[0].intValue();\n"
        "         }\n"
        "         return total;\n"
        "     }\n"
        " }\n"
    ),
    bench_source=_bench_template(
        "SortShort",
        extra_imports="import java.util.Random;",
        state_decls=(
            "@Param({\"1024\"})\npublic int batches;\n\n"
            "@Param({\"32\"})\npublic int len;\n\n"
            "private int[][] data;"
        ),
        setup_body=(
            "Random rng = new Random(61);\n"
            "data = new int[batches][len];\n"
            "for (int i = 0; i < batches; i++) {\n"
            "    for (int j = 0; j < len; j++) data[i][j] = rng.nextInt(10_000);\n"
            "}"
        ),
        bench_body="return SortShort.sortAndSum(data);",
        bench_return="long",
        bench_name="sort",
        output_unit="MICROSECONDS",
    ),
))

# ---------------------------------------------------------------------------
# 29. array_sum_unroll_001
# ---------------------------------------------------------------------------
define(TaskSpec(
    instance_id="array_sum_unroll_001",
    target_class="bench.SumLong",
    sut_class_name="SumLong",
    tags=["streams", "numerics"],
    expected_input_shapes=["array size = 2 000 000, stride = 8"],
    sut_source=(
        "package bench;\n\n"
        "public final class SumLong {\n\n"
        "    private SumLong() {}\n\n"
        "    /** Sum every {@code stride}-th element of {@code arr}. */\n"
        "    public static long sumStride(long[] arr, int stride) {\n"
        "        long total = 0L;\n"
        "        for (int i = 0, n = arr.length; i < n; i += stride) {\n"
        "            total += arr[i];\n"
        "        }\n"
        "        return total;\n"
        "    }\n"
        "}\n"
    ),
    regression_id="stream_filter",
    regression_description="rewrites strided sum as Arrays.stream + filter (extra pipeline overhead)",
    regression_patch=(
        "--- a/bench/SumLong.java\n"
        "+++ b/bench/SumLong.java\n"
        "@@ -1,12 +1,15 @@\n"
        " package bench;\n"
        " \n"
        "+import java.util.stream.IntStream;\n"
        "+\n"
        " public final class SumLong {\n"
        " \n"
        "     private SumLong() {}\n"
        " \n"
        "     /** Sum every {@code stride}-th element of {@code arr}. */\n"
        "     public static long sumStride(long[] arr, int stride) {\n"
        "-        long total = 0L;\n"
        "-        for (int i = 0, n = arr.length; i < n; i += stride) {\n"
        "-            total += arr[i];\n"
        "-        }\n"
        "-        return total;\n"
        "+        return IntStream.range(0, arr.length)\n"
        "+                .filter(i -> i % stride == 0)\n"
        "+                .mapToLong(i -> arr[i])\n"
        "+                .sum();\n"
        "     }\n"
        " }\n"
    ),
    bench_source=_bench_template(
        "SumLong",
        extra_imports="import java.util.Random;",
        state_decls=(
            "@Param({\"2000000\"})\npublic int size;\n\n"
            "@Param({\"8\"})\npublic int stride;\n\n"
            "private long[] data;"
        ),
        setup_body=(
            "Random rng = new Random(67);\n"
            "data = new long[size];\n"
            "for (int i = 0; i < size; i++) data[i] = rng.nextLong();"
        ),
        bench_body="return SumLong.sumStride(data, stride);",
        bench_return="long",
        bench_name="sumStride",
        output_unit="MILLISECONDS",
    ),
))

# ---------------------------------------------------------------------------
# 30. enum_switch already done as 23 above; add charset_decode_001
# ---------------------------------------------------------------------------
define(TaskSpec(
    instance_id="charset_decode_001",
    target_class="bench.Decode",
    sut_class_name="Decode",
    tags=["strings", "io"],
    expected_input_shapes=["byte[] length = 4 000"],
    sut_source=(
        "package bench;\n\n"
        "import java.nio.charset.StandardCharsets;\n\n"
        "public final class Decode {\n\n"
        "    private Decode() {}\n\n"
        "    /** Decode a UTF-8 byte[] using the cached charset constant. */\n"
        "    public static String fromUtf8(byte[] buf) {\n"
        "        return new String(buf, StandardCharsets.UTF_8);\n"
        "    }\n"
        "}\n"
    ),
    regression_id="charset_for_name",
    regression_description="looks up the charset by name on every call instead of using the cached constant",
    regression_patch=(
        "--- a/bench/Decode.java\n"
        "+++ b/bench/Decode.java\n"
        "@@ -1,13 +1,13 @@\n"
        " package bench;\n"
        " \n"
        "-import java.nio.charset.StandardCharsets;\n"
        "+import java.nio.charset.Charset;\n"
        " \n"
        " public final class Decode {\n"
        " \n"
        "     private Decode() {}\n"
        " \n"
        "     /** Decode a UTF-8 byte[] using the cached charset constant. */\n"
        "     public static String fromUtf8(byte[] buf) {\n"
        "-        return new String(buf, StandardCharsets.UTF_8);\n"
        "+        return new String(buf, Charset.forName(\"UTF-8\"));\n"
        "     }\n"
        " }\n"
    ),
    bench_source=_bench_template(
        "Decode",
        extra_imports="import java.util.Random;",
        state_decls=(
            "@Param({\"4000\"})\npublic int size;\n\n"
            "private byte[] data;"
        ),
        setup_body=(
            "Random rng = new Random(71);\n"
            "data = new byte[size];\n"
            "for (int i = 0; i < size; i++) data[i] = (byte) (' ' + rng.nextInt(90));"
        ),
        bench_body="return Decode.fromUtf8(data);",
        bench_return="String",
        bench_name="fromUtf8",
        output_unit="MICROSECONDS",
    ),
))


def main() -> None:
    parser = argparse.ArgumentParser()
    parser.add_argument("--only", action="append", help="Restrict to these instance_ids")
    parser.add_argument("--list", action="store_true", help="Just list defined specs")
    args = parser.parse_args()
    if args.list:
        for s in SPECS:
            print(s.instance_id)
        return
    only = set(args.only or [])
    for spec in SPECS:
        if only and spec.instance_id not in only:
            continue
        emit(spec)
    print(f"done: {sum(1 for s in SPECS if (not only or s.instance_id in only))} specs")


if __name__ == "__main__":
    main()
    sys.exit(0)
