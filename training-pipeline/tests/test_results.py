"""Unit tests for JMH result parsing and the robust RSD metric."""

from __future__ import annotations

import math

import pytest

from jmhgen.runner.results import parse_jmh_json, robust_rsd

_MAD_TO_STD = 1.4826


class TestRobustRsd:
    def test_zero_variance_is_zero(self) -> None:
        assert robust_rsd([10.0, 10.0, 10.0]) == 0.0

    def test_known_value(self) -> None:
        # values [8, 10, 12]: median 10, abs deviations [2, 0, 2], MAD = 2
        expected = _MAD_TO_STD * 2.0 / 10.0
        assert robust_rsd([8.0, 10.0, 12.0]) == pytest.approx(expected)

    def test_empty_is_none(self) -> None:
        assert robust_rsd([]) is None

    def test_zero_median_is_none(self) -> None:
        assert robust_rsd([0.0, 0.0, 0.0]) is None

    def test_negative_median_is_none(self) -> None:
        assert robust_rsd([-5.0, -5.0, -5.0]) is None


_SAMPLE_JSON = """
[
  {
    "benchmark": "com.example.MyBenchmark.sumLoop",
    "mode": "avgt",
    "primaryMetric": {
      "score": 2.5,
      "scoreError": 0.3,
      "scoreUnit": "ns/op",
      "rawData": [[1.0, 2.0], [3.0, 4.0]]
    }
  }
]
"""


class TestParseJmhJson:
    def test_parses_single_benchmark(self) -> None:
        stats = parse_jmh_json(_SAMPLE_JSON)
        assert len(stats) == 1
        stat = stats[0]
        assert stat.benchmark == "com.example.MyBenchmark.sumLoop"
        assert stat.mode == "avgt"
        assert stat.score == pytest.approx(2.5)
        assert stat.score_error == pytest.approx(0.3)
        assert stat.unit == "ns/op"

    def test_flattens_raw_data_across_forks(self) -> None:
        stat = parse_jmh_json(_SAMPLE_JSON)[0]
        assert stat.raw_measurements == (1.0, 2.0, 3.0, 4.0)

    def test_computes_robust_rsd_from_raw(self) -> None:
        stat = parse_jmh_json(_SAMPLE_JSON)[0]
        # median 2.5, abs deviations [1.5, 0.5, 0.5, 1.5], MAD = 1.0
        assert stat.robust_rsd == pytest.approx(_MAD_TO_STD * 1.0 / 2.5)

    def test_missing_raw_data_yields_no_rsd(self) -> None:
        stats = parse_jmh_json(
            '[{"benchmark": "x", "mode": "avgt", '
            '"primaryMetric": {"score": 1.0, "scoreError": 0.0, "scoreUnit": "ns/op"}}]'
        )
        assert stats[0].raw_measurements == ()
        assert stats[0].robust_rsd is None

    def test_malformed_json_raises_value_error(self) -> None:
        with pytest.raises(ValueError):
            parse_jmh_json("not json at all")

    def test_non_array_raises_value_error(self) -> None:
        with pytest.raises(ValueError):
            parse_jmh_json('{"benchmark": "x"}')

    def test_nan_scores_when_fields_absent(self) -> None:
        stats = parse_jmh_json('[{"benchmark": "x", "mode": "avgt", "primaryMetric": {}}]')
        assert math.isnan(stats[0].score)
