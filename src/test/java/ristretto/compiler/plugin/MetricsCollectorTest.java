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
        assertThat(collector.calculateParameter(), is(Optional.empty()));
    }

    @Test
    void calculates_parameter_metrics_when_available() {
        collector.markedAsFinal(VariableScope.METHOD);
        collector.skipped(VariableScope.METHOD);
        collector.skipped(VariableScope.METHOD);
        collector.skipped(VariableScope.METHOD);

        var metrics = collector.calculateParameter().orElseThrow();

        assertThat(metrics.inspectedCount, is(4));
        assertThat(metrics.markedAsFinalPercentage, is(new BigDecimal("25.00")));
        assertThat(metrics.skippedPercentage, is(new BigDecimal("75.00")));
    }

    @Test
    void indicates_when_there_are_no_local_variable_metrics() {
        assertThat(collector.calculateLocalVariable(), is(Optional.empty()));
    }

    @Test
    void calculates_local_variable_metrics_when_available() {
        collector.markedAsFinal(VariableScope.BLOCK);
        collector.skipped(VariableScope.BLOCK);
        collector.skipped(VariableScope.BLOCK);
        collector.skipped(VariableScope.BLOCK);

        var metrics = collector.calculateLocalVariable().orElseThrow();

        assertThat(metrics.inspectedCount, is(4));
        assertThat(metrics.markedAsFinalPercentage, is(new BigDecimal("25.00")));
        assertThat(metrics.skippedPercentage, is(new BigDecimal("75.00")));
    }
}