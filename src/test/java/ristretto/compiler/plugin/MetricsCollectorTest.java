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
        assertThat(collector.calculate(VariableFinalModifier.VariableScope.METHOD), is(Optional.empty()));
    }

    @Test
    void calculates_parameter_metrics_when_available() {
        collector.markedAsFinal(VariableFinalModifier.VariableScope.METHOD);
        collector.skipped(VariableFinalModifier.VariableScope.METHOD);
        collector.skipped(VariableFinalModifier.VariableScope.METHOD);
        collector.skipped(VariableFinalModifier.VariableScope.METHOD);

        var metrics = collector.calculate(VariableFinalModifier.VariableScope.METHOD).orElseThrow();

        assertThat(metrics.inspectedCount, is(4));
        assertThat(metrics.markedAsFinalPercentage, is(new BigDecimal("25.00")));
        assertThat(metrics.skippedPercentage, is(new BigDecimal("75.00")));
    }
}