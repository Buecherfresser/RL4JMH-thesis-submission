"""Unit tests for the deterministic rollout repairs (jmhgen.generate.repair)."""

from __future__ import annotations

from jmhgen.generate.repair import repair_java_source

_BODY = (
    "@State(Scope.Thread)\n"
    "@BenchmarkMode(Mode.Throughput)\n"
    "@OutputTimeUnit(TimeUnit.MICROSECONDS)\n"
    "public class FooBenchmark {\n"
    "    @Benchmark\n"
    "    public void measure(Blackhole bh) { bh.consume(Foo.compute()); }\n"
    "}"
)


class TestMissingImports:
    def test_adds_timeunit_when_used_but_not_imported(self) -> None:
        src = f"package com.ex;\n\nimport org.openjdk.jmh.annotations.*;\n\n{_BODY}"
        out = repair_java_source(src)
        assert "import java.util.concurrent.TimeUnit;" in out.source
        assert any(a.startswith("imports:") for a in out.applied)

    def test_adds_jmh_annotations_wildcard(self) -> None:
        src = f"package com.ex;\n\n{_BODY}"
        out = repair_java_source(src)
        assert "import org.openjdk.jmh.annotations.*;" in out.source
        assert "import org.openjdk.jmh.infra.Blackhole;" in out.source

    def test_noop_when_already_imported(self) -> None:
        src = (
            "package com.ex;\n\n"
            "import org.openjdk.jmh.annotations.*;\n"
            "import org.openjdk.jmh.infra.Blackhole;\n"
            "import java.util.concurrent.TimeUnit;\n\n" + _BODY
        )
        assert repair_java_source(src).source == src

    def test_wildcard_on_same_package_counts_as_imported(self) -> None:
        src = (
            "package com.ex;\n\nimport org.openjdk.jmh.annotations.*;\n"
            "import org.openjdk.jmh.infra.*;\n"
            "import java.util.concurrent.TimeUnit;\n\n" + _BODY
        )
        assert repair_java_source(src).source == src

    def test_does_not_shadow_a_locally_declared_type(self) -> None:
        src = "package com.ex;\n\nclass TimeUnit {}\n\n" + _BODY
        assert "import java.util.concurrent.TimeUnit;" not in repair_java_source(src).source

    def test_inserts_after_package_when_no_imports_exist(self) -> None:
        out = repair_java_source(f"package com.ex;\n\n{_BODY}").source
        assert out.index("package com.ex;") < out.index("import ")


class TestStaticBlackhole:
    def test_rewrites_onto_the_injected_parameter(self) -> None:
        src = (
            "package com.ex;\nimport org.openjdk.jmh.annotations.*;\n"
            "import org.openjdk.jmh.infra.Blackhole;\n"
            "public class B {\n  @Benchmark\n"
            "  public void m(Blackhole bh) { Blackhole.consume(1); }\n}"
        )
        out = repair_java_source(src)
        assert "bh.consume(1)" in out.source
        assert "Blackhole.consume" not in out.source
        assert "static_blackhole" in out.applied

    def test_falls_through_to_injection_when_there_is_no_parameter(self) -> None:
        # Nothing to rewrite onto, so _fix_static_blackhole declines and
        # _inject_blackhole_param supplies the parameter instead.
        src = (
            "package com.ex;\nimport org.openjdk.jmh.infra.Blackhole;\n"
            "public class B {\n  @Benchmark\n"
            "  public void m() { Blackhole.consume(1); }\n}"
        )
        out = repair_java_source(src)
        assert "m(Blackhole bh)" in out.source
        assert "static_blackhole" not in out.applied
        assert "blackhole_param" in out.applied

    def test_declines_outside_a_benchmark_method(self) -> None:
        # A helper with no @Benchmark gets no injection: JMH would not fill the parameter in.
        src = (
            "package com.ex;\nimport org.openjdk.jmh.infra.Blackhole;\n"
            "public class B {\n"
            "  private void helper() { Blackhole.consume(1); }\n}"
        )
        assert "Blackhole.consume(1)" in repair_java_source(src).source

    def test_uses_the_parameters_actual_name(self) -> None:
        src = (
            "package com.ex;\nimport org.openjdk.jmh.infra.Blackhole;\n"
            "public class B {\n  @Benchmark\n"
            "  public void m(Blackhole hole) { Blackhole.consume(1); }\n}"
        )
        assert "hole.consume(1)" in repair_java_source(src).source


class TestFenceLines:
    def test_drops_stray_fence_lines(self) -> None:
        src = f"package com.ex;\n```java\n{_BODY}\n```"
        out = repair_java_source(src)
        assert "```" not in out.source
        assert "fence_lines" in out.applied


class TestClean:
    def test_clean_source_is_untouched(self) -> None:
        src = (
            "package com.ex;\n\nimport org.openjdk.jmh.annotations.*;\n"
            "import org.openjdk.jmh.infra.Blackhole;\n"
            "import java.util.concurrent.TimeUnit;\n\n" + _BODY
        )
        out = repair_java_source(src)
        assert out.source == src
        assert not out.changed


class TestInjectBlackholeParam:
    def test_adds_parameter_when_body_calls_it_statically(self) -> None:
        src = (
            "package com.ex;\nimport org.openjdk.jmh.annotations.*;\n"
            "public class B {\n"
            "  @Benchmark\n"
            "  public void measure() { Blackhole.consume(Foo.compute()); }\n}"
        )
        out = repair_java_source(src)
        assert "measure(Blackhole bh)" in out.source
        assert "bh.consume(Foo.compute())" in out.source
        assert "Blackhole.consume" not in out.source
        assert "import org.openjdk.jmh.infra.Blackhole;" in out.source
        assert "blackhole_param" in out.applied

    def test_appends_to_an_existing_parameter_list(self) -> None:
        src = (
            "package com.ex;\nimport org.openjdk.jmh.annotations.*;\n"
            "import org.openjdk.jmh.infra.Blackhole;\n"
            "public class B {\n"
            "  @Benchmark\n"
            "  public void measure(MyState s) { Blackhole.consume(s.x); }\n}"
        )
        out = repair_java_source(src)
        assert "measure(MyState s, Blackhole bh)" in out.source

    def test_leaves_methods_that_already_have_one(self) -> None:
        src = (
            "package com.ex;\nimport org.openjdk.jmh.annotations.*;\n"
            "import org.openjdk.jmh.infra.Blackhole;\n"
            "public class B {\n"
            "  @Benchmark\n"
            "  public void measure(Blackhole bh) { bh.consume(1); }\n}"
        )
        out = repair_java_source(src)
        assert out.source == src

    def test_only_touches_the_method_that_needs_it(self) -> None:
        src = (
            "package com.ex;\nimport org.openjdk.jmh.annotations.*;\n"
            "import org.openjdk.jmh.infra.Blackhole;\n"
            "public class B {\n"
            "  @Benchmark\n  public int clean() { return 1; }\n"
            "  @Benchmark\n  public void dirty() { Blackhole.consume(2); }\n}"
        )
        out = repair_java_source(src)
        assert "clean()" in out.source
        assert "dirty(Blackhole bh)" in out.source

    def test_handles_generic_return_types(self) -> None:
        src = (
            "package com.ex;\nimport org.openjdk.jmh.annotations.*;\n"
            "import org.openjdk.jmh.infra.Blackhole;\n"
            "public class B {\n"
            "  @Benchmark\n"
            "  public java.util.List<String> m() { Blackhole.consume(1); return null; }\n}"
        )
        assert "m(Blackhole bh)" in repair_java_source(src).source

    def test_avoids_shadowing_an_existing_bh_name(self) -> None:
        src = (
            "package com.ex;\nimport org.openjdk.jmh.annotations.*;\n"
            "import org.openjdk.jmh.infra.Blackhole;\n"
            "public class B {\n"
            "  @Benchmark\n"
            "  public void m() { int bh = 1; Blackhole.consume(bh); }\n}"
        )
        out = repair_java_source(src)
        assert "m(Blackhole jmhBlackhole)" in out.source
        assert "jmhBlackhole.consume(bh)" in out.source


class TestBareStateIsNotRepaired:
    """Withdrawn after run 3: see the note in repair.py. A bare @State must be left alone."""

    def test_bare_state_is_left_untouched(self) -> None:
        src = (
            "package com.ex;\nimport org.openjdk.jmh.annotations.*;\n@State\n"
            "public class B { @Benchmark public int m() { return 1; } }"
        )
        out = repair_java_source(src)
        assert "@State\n" in out.source
        assert "Scope.Thread" not in out.source
