package ristretto.compiler.plugin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class MetricsCollectorTest {

    MetricsCollector<String> collector;

    @BeforeEach
    void beforeEach() {
        collector = new MetricsCollector<>();
    }

    @Test
    void indicates_when_there_are_no_metrics() {
        assertThat(collector.calculate("parameter"), is(Optional.empty()));
    }

    @Test
    void calculates_metrics_when_available() {
        collector.finalModifierAdded("parameter");
        collector.finalModifierAdded("parameter");
        collector.finalModifierAdded("parameter");
        collector.annotatedAsMutable("parameter");
        collector.annotatedAsMutable("parameter");
        collector.finalModifierAlreadyPresent("parameter");

        var metrics = collector.calculate("parameter").orElseThrow();

        assertThat(metrics.inspectedCount, is(6));
        assertThat(metrics.finalModifierAddedPercentage, is(new BigDecimal("50.00")));
        assertThat(metrics.finalModifierAlreadyPresentPercentage, is(new BigDecimal("16.66")));
        assertThat(metrics.annotatedAsMutablePercentage, is(new BigDecimal("33.33")));
    }
}