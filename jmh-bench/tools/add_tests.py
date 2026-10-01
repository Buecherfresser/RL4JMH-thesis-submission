"""Backfill JUnit 4 tests for the tasks that lack them, and stamp
`junit_test_path:` into their `task.yaml`.

Each entry below pairs a task ID with a JUnit 4 source file. The test is
written under ``dataset/tasks/<id>/tests/<SutClass>Test.java`` and the
yaml gains a ``junit_test_path: tests/...`` line if it wasn't already there.
"""

from __future__ import annotations

import sys
from pathlib import Path

REPO = Path(__file__).resolve().parent.parent
TASKS = REPO / "dataset" / "tasks"


def ensure_junit(task_id: str, sut_class: str, source: str) -> None:
    task_dir = TASKS / task_id
    if not task_dir.exists():
        print(f"!! missing task: {task_id}")
        return
    tests_dir = task_dir / "tests"
    tests_dir.mkdir(exist_ok=True)
    test_path = tests_dir / f"{sut_class}Test.java"
    test_path.write_text(source)

    yaml_path = task_dir / "task.yaml"
    text = yaml_path.read_text()
    rel = f"tests/{sut_class}Test.java"
    if "junit_test_path:" in text:
        # already declared
        print(f"  (kept) {task_id}")
        return
    # Insert before `regressions:` so the layout stays readable.
    lines = text.splitlines()
    new_lines: list[str] = []
    inserted = False
    for ln in lines:
        if not inserted and ln.startswith("regressions:"):
            new_lines.append(f"junit_test_path: {rel}")
            inserted = True
        new_lines.append(ln)
    if not inserted:
        new_lines.append(f"junit_test_path: {rel}")
    yaml_path.write_text("\n".join(new_lines) + ("\n" if text.endswith("\n") else ""))
    print(f"  added: {task_id}")


# ---------------------------------------------------------------------------
# Tests — one per task, JUnit 4, small but meaningful (covers branches).
# ---------------------------------------------------------------------------

ensure_junit("stream_filter_001", "StreamFilter", """package bench;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class StreamFilterTest {
    @Test public void empty() { assertEquals(0, StreamFilter.countOver(new int[0], 5)); }
    @Test public void allOver() { assertEquals(3, StreamFilter.countOver(new int[]{6, 7, 8}, 5)); }
    @Test public void noneOver() { assertEquals(0, StreamFilter.countOver(new int[]{1, 2, 3}, 5)); }
    @Test public void mixed() { assertEquals(2, StreamFilter.countOver(new int[]{1, 6, 2, 7}, 5)); }
}
""")

ensure_junit("fib_memoised_001", "Fib", """package bench;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class FibTest {
    @Test public void zero() { assertEquals(0L, Fib.fib(0)); }
    @Test public void one() { assertEquals(1L, Fib.fib(1)); }
    @Test public void ten() { assertEquals(55L, Fib.fib(10)); }
    @Test public void twenty() { assertEquals(6765L, Fib.fib(20)); }
}
""")

ensure_junit("binary_search_001", "Search", """package bench;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class SearchTest {
    @Test public void found() { assertEquals(2, Search.indexOf(new int[]{0, 2, 4, 6, 8}, 4)); }
    @Test public void firstElement() { assertEquals(0, Search.indexOf(new int[]{0, 2, 4}, 0)); }
    @Test public void lastElement() { assertEquals(2, Search.indexOf(new int[]{0, 2, 4}, 4)); }
    @Test public void absent() { assertEquals(-1, Search.indexOf(new int[]{0, 2, 4}, 3)); }
    @Test public void empty() { assertEquals(-1, Search.indexOf(new int[0], 0)); }
}
""")

ensure_junit("regex_compile_001", "RegexCount", """package bench;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class RegexCountTest {
    @Test public void emptyString() { assertEquals(0, RegexCount.countMatches("")); }
    @Test public void singleWord() { assertEquals(1, RegexCount.countMatches("hello")); }
    @Test public void threeWords() { assertEquals(3, RegexCount.countMatches("a b c")); }
    @Test public void withPunctuation() { assertEquals(2, RegexCount.countMatches("hi, there!")); }
}
""")

ensure_junit("quicksort_pivot_001", "Quicksort", """package bench;

import static org.junit.Assert.assertArrayEquals;
import org.junit.Test;

public class QuicksortTest {
    @Test public void empty() {
        int[] a = new int[0];
        Quicksort.sort(a);
        assertArrayEquals(new int[0], a);
    }
    @Test public void single() {
        int[] a = {42};
        Quicksort.sort(a);
        assertArrayEquals(new int[]{42}, a);
    }
    @Test public void mixed() {
        int[] a = {3, 1, 4, 1, 5, 9, 2, 6};
        Quicksort.sort(a);
        assertArrayEquals(new int[]{1, 1, 2, 3, 4, 5, 6, 9}, a);
    }
    @Test public void alreadySorted() {
        int[] a = {1, 2, 3, 4, 5};
        Quicksort.sort(a);
        assertArrayEquals(new int[]{1, 2, 3, 4, 5}, a);
    }
}
""")

ensure_junit("matrix_traversal_001", "Matrix", """package bench;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class MatrixTest {
    @Test public void smallMatrix() {
        int[][] m = {{1, 2}, {3, 4}};
        assertEquals(10L, Matrix.sum(m));
    }
    @Test public void rowOf3() {
        int[][] m = {{1, 2, 3}, {4, 5, 6}};
        assertEquals(21L, Matrix.sum(m));
    }
    @Test public void zeros() {
        int[][] m = {{0, 0}, {0, 0}};
        assertEquals(0L, Matrix.sum(m));
    }
}
""")

ensure_junit("allocation_in_loop_001", "Buffer", """package bench;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class BufferTest {
    @Test public void zero() { assertEquals(0L, Buffer.process(0)); }
    @Test public void deterministic() {
        // Idempotent: the same input must produce the same output.
        assertEquals(Buffer.process(8), Buffer.process(8));
        assertEquals(Buffer.process(32), Buffer.process(32));
    }
}
""")

ensure_junit("atomiclong_counter_001", "Counter", """package bench;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class CounterTest {
    @Test public void zeroBumps() {
        Counter c = new Counter();
        assertEquals(0L, c.bumpAll(0));
    }
    @Test public void tenBumps() {
        Counter c = new Counter();
        assertEquals(10L, c.bumpAll(10));
    }
    @Test public void chained() {
        Counter c = new Counter();
        c.bumpAll(5);
        assertEquals(8L, c.bumpAll(3));
    }
}
""")

ensure_junit("math_pow_int_001", "PowInt", """package bench;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class PowIntTest {
    @Test public void zero() { assertEquals(0L, PowInt.sumOfSquares(0)); }
    @Test public void one() { assertEquals(0L, PowInt.sumOfSquares(1)); }
    @Test public void four() { assertEquals(0L + 1 + 4 + 9, PowInt.sumOfSquares(4)); }
    @Test public void ten() { assertEquals(285L, PowInt.sumOfSquares(10)); }
}
""")

ensure_junit("hashset_dedup_001", "Dedup", """package bench;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class DedupTest {
    @Test public void empty() { assertEquals(0, Dedup.countUnique(new int[0])); }
    @Test public void allSame() { assertEquals(1, Dedup.countUnique(new int[]{7, 7, 7})); }
    @Test public void allDistinct() { assertEquals(4, Dedup.countUnique(new int[]{1, 2, 3, 4})); }
    @Test public void someDups() { assertEquals(3, Dedup.countUnique(new int[]{1, 2, 1, 3, 2, 3})); }
}
""")

ensure_junit("sublist_copy_001", "Prefix", """package bench;

import static org.junit.Assert.assertEquals;
import java.util.Arrays;
import org.junit.Test;

public class PrefixTest {
    @Test public void zeroPrefix() {
        assertEquals(0L, Prefix.sumPrefix(Arrays.asList(1, 2, 3), 0));
    }
    @Test public void fullList() {
        assertEquals(6L, Prefix.sumPrefix(Arrays.asList(1, 2, 3), 3));
    }
    @Test public void partial() {
        assertEquals(3L, Prefix.sumPrefix(Arrays.asList(1, 2, 5, 10), 2));
    }
}
""")

ensure_junit("linkedlist_random_access_001", "IndexedSum", """package bench;

import static org.junit.Assert.assertEquals;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.junit.Test;

public class IndexedSumTest {
    @Test public void empty() {
        assertEquals(0L, IndexedSum.sum(new LinkedList<Integer>()));
    }
    @Test public void arrayList() {
        assertEquals(6L, IndexedSum.sum(Arrays.asList(1, 2, 3)));
    }
    @Test public void linkedList() {
        List<Integer> l = new LinkedList<>();
        l.add(10); l.add(20); l.add(30);
        assertEquals(60L, IndexedSum.sum(l));
    }
}
""")

ensure_junit("treemap_lookup_001", "MapLookup", """package bench;

import static org.junit.Assert.assertEquals;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;

public class MapLookupTest {
    @Test public void empty() {
        assertEquals(0L, MapLookup.sumGet(new HashMap<String, Long>(), Arrays.asList("a", "b")));
    }
    @Test public void allHits() {
        Map<String, Long> m = new HashMap<>();
        m.put("a", 1L); m.put("b", 2L);
        assertEquals(3L, MapLookup.sumGet(m, Arrays.asList("a", "b")));
    }
    @Test public void someMisses() {
        Map<String, Long> m = new HashMap<>();
        m.put("a", 7L);
        assertEquals(7L, MapLookup.sumGet(m, Arrays.asList("a", "missing")));
    }
    @Test public void factoryReturnsMap() {
        assertEquals(0L, MapLookup.sumGet(MapLookup.newFastMap(4), Arrays.asList("a")));
    }
}
""")

ensure_junit("string_split_001", "Splitter", """package bench;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class SplitterTest {
    @Test public void empty() { assertEquals(0, Splitter.count("", ',')); }
    @Test public void singleToken() { assertEquals(1, Splitter.count("abc", ',')); }
    @Test public void threeTokens() { assertEquals(3, Splitter.count("a,b,c", ',')); }
    @Test public void trailingSep() { assertEquals(3, Splitter.count("a,b,", ',')); }
}
""")

ensure_junit("byte_buffer_wrap_001", "IntsReader", """package bench;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class IntsReaderTest {
    @Test public void zeros() {
        assertEquals(0L, IntsReader.sum(new byte[16]));
    }
    @Test public void oneInt() {
        byte[] buf = {0, 0, 0, 5};
        assertEquals(5L, IntsReader.sum(buf));
    }
    @Test public void twoInts() {
        byte[] buf = {0, 0, 0, 1, 0, 0, 0, 2};
        assertEquals(3L, IntsReader.sum(buf));
    }
}
""")

ensure_junit("integer_log2_001", "Log2", """package bench;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class Log2Test {
    @Test public void below2() { assertEquals(0L, Log2.sumLog2(1)); }
    @Test public void twoTo4() {
        // floor(log2(1))=0, log2(2)=1, log2(3)=1 -> 0+1+1 = 2
        assertEquals(2L, Log2.sumLog2(4));
    }
    @Test public void atPowerOfTwo() {
        // 1..7: 0+1+1+2+2+2+2 = 10
        assertEquals(10L, Log2.sumLog2(8));
    }
}
""")

ensure_junit("modexp_001", "ModExp", """package bench;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class ModExpTest {
    @Test public void zeroExp() { assertEquals(1L, ModExp.pow(7L, 0L, 13L)); }
    @Test public void smallCase() { assertEquals(8L, ModExp.pow(2L, 3L, 100L)); }
    @Test public void modulus() { assertEquals(4L, ModExp.pow(2L, 10L, 100L)); }
    @Test public void large() {
        // 5^7 mod 1000 = 78125 mod 1000 = 125
        assertEquals(125L, ModExp.pow(5L, 7L, 1000L));
    }
}
""")

ensure_junit("enum_switch_001", "Tag", """package bench;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class TagTest {
    @Test public void empty() {
        assertEquals(0L, Tag.score(new Tag.Kind[0]));
    }
    @Test public void singleAlpha() {
        assertEquals(1L, Tag.score(new Tag.Kind[]{Tag.Kind.ALPHA}));
    }
    @Test public void allFour() {
        Tag.Kind[] all = {Tag.Kind.ALPHA, Tag.Kind.BETA, Tag.Kind.GAMMA, Tag.Kind.DELTA};
        assertEquals(10L, Tag.score(all));
    }
}
""")

ensure_junit("levenshtein_dp_001", "EditDistance", """package bench;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class EditDistanceTest {
    @Test public void empties() { assertEquals(0, EditDistance.distance("", "")); }
    @Test public void prefix() { assertEquals(3, EditDistance.distance("", "abc")); }
    @Test public void identical() { assertEquals(0, EditDistance.distance("hello", "hello")); }
    @Test public void textbook() { assertEquals(3, EditDistance.distance("kitten", "sitting")); }
}
""")

ensure_junit("prime_sieve_001", "Primes", """package bench;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class PrimesTest {
    @Test public void belowTwo() { assertEquals(0, Primes.count(2)); }
    @Test public void justTwo() { assertEquals(1, Primes.count(3)); }
    @Test public void below10() { assertEquals(4, Primes.count(10)); /* 2,3,5,7 */ }
    @Test public void below30() { assertEquals(10, Primes.count(30)); }
}
""")

ensure_junit("set_contains_linear_001", "Intersect", """package bench;

import static org.junit.Assert.assertEquals;
import java.util.Arrays;
import java.util.HashSet;
import org.junit.Test;

public class IntersectTest {
    @Test public void disjoint() {
        assertEquals(0, Intersect.sizeOfIntersection(
            new HashSet<>(Arrays.asList(1, 2)), new HashSet<>(Arrays.asList(3, 4))));
    }
    @Test public void identical() {
        assertEquals(3, Intersect.sizeOfIntersection(
            new HashSet<>(Arrays.asList(1, 2, 3)), new HashSet<>(Arrays.asList(1, 2, 3))));
    }
    @Test public void partial() {
        assertEquals(2, Intersect.sizeOfIntersection(
            new HashSet<>(Arrays.asList(1, 2, 3, 4)), new HashSet<>(Arrays.asList(3, 4, 5))));
    }
}
""")

ensure_junit("string_replace_001", "Replacer", """package bench;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class ReplacerTest {
    @Test public void empty() { assertEquals("", Replacer.stripSpaces("")); }
    @Test public void noSpaces() { assertEquals("abc", Replacer.stripSpaces("abc")); }
    @Test public void allSpaces() { assertEquals("", Replacer.stripSpaces("    ")); }
    @Test public void mixed() { assertEquals("helloworld", Replacer.stripSpaces("hello world")); }
}
""")

ensure_junit("sort_stability_001", "SortShort", """package bench;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class SortShortTest {
    @Test public void singleBatch() {
        int[][] data = {{3, 1, 2}};
        assertEquals(1L, SortShort.sortAndSum(data));
    }
    @Test public void multipleBatches() {
        int[][] data = {{5, 3, 1}, {9, 7, 8}};
        assertEquals(8L, SortShort.sortAndSum(data));
    }
}
""")

ensure_junit("array_sum_unroll_001", "SumLong", """package bench;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class SumLongTest {
    @Test public void strideOne() {
        assertEquals(10L, SumLong.sumStride(new long[]{1, 2, 3, 4}, 1));
    }
    @Test public void strideTwo() {
        assertEquals(4L, SumLong.sumStride(new long[]{1, 2, 3, 4}, 2));
    }
    @Test public void empty() {
        assertEquals(0L, SumLong.sumStride(new long[0], 4));
    }
}
""")

ensure_junit("charset_decode_001", "Decode", """package bench;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class DecodeTest {
    @Test public void empty() { assertEquals("", Decode.fromUtf8(new byte[0])); }
    @Test public void ascii() {
        assertEquals("hello", Decode.fromUtf8(new byte[]{'h', 'e', 'l', 'l', 'o'}));
    }
}
""")


if __name__ == "__main__":
    print("done")
    sys.exit(0)
