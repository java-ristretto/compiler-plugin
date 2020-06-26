package ristretto.compiler.plugin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class MetricsCollectorTest {

  MetricsCollector<String, String> collector;

  @BeforeEach
  void beforeEach() {
    collector = new MetricsCollector<>();
    collector.count("parameter", "added");
    collector.count("method", "added");
  }

  @Test
  void indicates_when_there_are_no_metrics_for_provided_event_source() {
    assertThat(collector.calculate("local-variable"), is(Optional.empty()));
  }

  @Test
  void calculates_metrics_when_available_for_provided_event_source() {
    var parameterMetrics = collector.calculate("parameter").orElseThrow();

    assertThat(parameterMetrics.getTotal(), is(1));
    assertThat(parameterMetrics.percentage("added"), is(new BigDecimal("100.00")));
  }
}