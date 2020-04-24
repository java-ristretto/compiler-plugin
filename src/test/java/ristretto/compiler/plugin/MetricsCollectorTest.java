package ristretto.compiler.plugin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class MetricsCollectorTest {

    MetricsCollector collector;

    @BeforeEach
    void beforeEach() {
        collector = MetricsCollector.newCollector();
    }

    @Test
    void indicates_when_there_are_no_metrics() {
        assertThat(collector.calculate(), is(Optional.empty()));
    }

    @Test
    void calculates_metrics_when_available() {
        collector.parameterMarkedAsFinal();
        collector.parameterSkipped();
        collector.parameterSkipped();
        collector.parameterSkipped();

        var metrics = collector.calculate().orElseThrow();

        assertThat(metrics.inspectedCount, is(4));
        assertThat(metrics.markedAsFinalPercentage, is(new BigDecimal("25.00")));
        assertThat(metrics.skippedPercentage, is(new BigDecimal("75.00")));
    }
}