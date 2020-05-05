package ristretto.compiler.plugin;

import ristretto.compiler.plugin.VariableFinalModifier.VariableScope;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

final class MetricsCollector implements VariableFinalModifier.Observer {

    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

    private final Map<VariableScope, AtomicInteger> markedAsFinalCount = new HashMap<>();
    private final Map<VariableScope, AtomicInteger> skippedCount = new HashMap<>();

    private MetricsCollector() {
    }

    static MetricsCollector newCollector() {
        return new MetricsCollector();
    }

    private static int count(Map<VariableScope, AtomicInteger> countByScope, VariableScope scope) {
        AtomicInteger count = countByScope.get(scope);
        if (count == null) {
            return 0;
        }
        return count.get();
    }

    private static void increment(Map<VariableScope, AtomicInteger> countByScope, VariableScope scope) {
        countByScope.computeIfAbsent(scope, newScope -> new AtomicInteger(0)).incrementAndGet();
    }

    private static BigDecimal percentage(int count, int total) {
        return BigDecimal.valueOf(count)
            .divide(BigDecimal.valueOf(total), 4, RoundingMode.FLOOR)
            .multiply(HUNDRED)
            .setScale(2, RoundingMode.FLOOR);
    }

    Optional<Metrics> calculate(VariableScope scope) {
        return Metrics.calculate(count(markedAsFinalCount, scope), count(skippedCount, scope));
    }

    @Override
    public void markedAsFinal(VariableScope scope) {
        increment(markedAsFinalCount, scope);
    }

    @Override
    public void skipped(VariableScope scope) {
        increment(skippedCount, scope);
    }

    static final class Metrics {

        final int inspectedCount;
        final BigDecimal markedAsFinalPercentage;
        final BigDecimal skippedPercentage;

        private Metrics(int inspectedCount, BigDecimal markedAsFinalPercentage, BigDecimal skippedPercentage) {
            this.inspectedCount = inspectedCount;
            this.markedAsFinalPercentage = markedAsFinalPercentage;
            this.skippedPercentage = skippedPercentage;
        }

        private static Optional<Metrics> calculate(int markedAsFinal, int skipped) {
            int inspected = markedAsFinal + skipped;

            if (inspected == 0) {
                return Optional.empty();
            }

            return Optional.of(new Metrics(
                inspected,
                percentage(markedAsFinal, inspected),
                percentage(skipped, inspected)
            ));
        }
    }
}
