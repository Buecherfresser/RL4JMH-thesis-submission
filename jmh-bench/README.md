# JMH-Bench

A benchmark for evaluating how well LLMs write [JMH](https://github.com/openjdk/jmh)
microbenchmarks for Java code.

A generated benchmark is scored on whether it:

1. compiles,
2. runs,
3. avoids the JMH anti-patterns of Costa et al. (TSE 2019), checked with
   [SpotJMHBugs](https://github.com/DiegoEliasCosta/spotjmhbugs), and
4. detects performance regressions: injected slowdowns ("mutants") must make it measurably
   slower (≥ 1.10×, Welch's t-test, p < 0.05).

There are two tracks:

- **Task track**: 25 synthetic and 30 real-world single-class tasks (`dataset/tasks`,
  `dataset/tasks_real`).
- **Project track**: 6 real Java libraries with 350 performance mutants in total
  (`dataset/projects`). The model writes a benchmark suite class by class; the score is the
  share of mutants the suite detects.

## Install

Requires Python 3.10+, [uv](https://docs.astral.sh/uv/), JDK 17 and Maven 3.9.

```bash
uv sync --extra openai --extra dev
./tools/install_spotjmhbugs.sh   # optional: bytecode anti-pattern checks
```

Use JDK 17 or 21. JDK 23 and newer skip JMH's annotation processor, so the built jar contains
no benchmarks.

## Usage

```bash
# Score the hand-written reference suite (no API key needed)
uv run jmhbench project-bench --harness dummy-passthrough --project fastfilter --quick

# Generate a suite with a model behind an OpenAI-compatible API, then score it
uv run jmhbench project-gen --harness openai-zero-shot --project hppc \
  -o model=<model> --compile-check --out gen/hppc
uv run jmhbench project-bench gen/hppc --project hppc

# Task track
uv run jmhbench run --harness openrouter -o model=<model> --track all
```

Generation and measurement are separate steps, so suites can be generated on a GPU box and
measured on a quiet machine. API keys go in `.env` (see `.env.example`). Run
`uv run jmhbench --help` for all commands.

Pin measurements to one CPU core type on hybrid CPUs (e.g. with `taskset`); otherwise the
core a fork lands on biases the results.

## Layout

```
jmhbench/          scorer and CLI
jmhbench/adapters/ harnesses: openai-zero-shot, openrouter, anthropic-zero-shot, ju2jmh, ...
dataset/           tasks and the six vendored projects with their mutant patches
prompts/           prompt templates
java-runner/       Maven templates for building benchmarks
tools/             mutant generation, calibration and campaign scripts
thesis-bundles/    the suites evaluated in the thesis
```

## Tests

```bash
uv run pytest
```

## License

MIT. `consumeCpu` in `tools/make_project_mutants.py` and `tools/make_compress_mutants.py` is
adapted from `Blackhole.consumeCPU` in [OpenJDK JMH](https://github.com/openjdk/jmh) 1.37
(GPLv2 with Classpath Exception).
