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
        collector = new MetricsCollector();
    }

    @Test
    void indicates_when_there_are_no_metrics() {
        assertThat(collector.calculate(VariableFinalModifier.VariableScope.METHOD), is(Optional.empty()));
    }

    @Test
    void calculates_metrics_when_available() {
        collector.finalModifierAdded(VariableFinalModifier.VariableScope.METHOD);
        collector.finalModifierAdded(VariableFinalModifier.VariableScope.METHOD);
        collector.finalModifierAdded(VariableFinalModifier.VariableScope.METHOD);
        collector.annotatedAsMutable(VariableFinalModifier.VariableScope.METHOD);
        collector.annotatedAsMutable(VariableFinalModifier.VariableScope.METHOD);
        collector.finalModifierAlreadyPresent(VariableFinalModifier.VariableScope.METHOD);

        var metrics = collector.calculate(VariableFinalModifier.VariableScope.METHOD).orElseThrow();

        assertThat(metrics.inspectedCount, is(6));
        assertThat(metrics.finalModifierAddedPercentage, is(new BigDecimal("50.00")));
        assertThat(metrics.finalModifierAlreadyPresentPercentage, is(new BigDecimal("16.66")));
        assertThat(metrics.annotatedAsMutablePercentage, is(new BigDecimal("33.33")));
    }
}