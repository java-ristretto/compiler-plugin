package ristretto.compiler.plugin;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

final class MetricsCollector {

    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

    private final Map<VariableType, AtomicInteger> finalModifierAddedCount = new HashMap<>();
    private final Map<VariableType, AtomicInteger> annotatedAsMutableCount = new HashMap<>();
    private final Map<VariableType, AtomicInteger> finalModifierAlreadyPresentCount = new HashMap<>();

    private static <T> int count(Map<T, AtomicInteger> countByScope, T key) {
        AtomicInteger count = countByScope.get(key);
        if (count == null) {
            return 0;
        }
        return count.get();
    }

    private static <T> void increment(Map<T, AtomicInteger> countByScope, T key) {
        countByScope.computeIfAbsent(key, newScope -> new AtomicInteger(0)).incrementAndGet();
    }

    private static BigDecimal percentage(int count, int total) {
        return BigDecimal.valueOf(count)
            .divide(BigDecimal.valueOf(total), 4, RoundingMode.FLOOR)
            .multiply(HUNDRED)
            .setScale(2, RoundingMode.FLOOR);
    }

    Optional<Metrics> calculate(VariableType type) {
        return Metrics.calculate(
            count(finalModifierAddedCount, type),
            count(annotatedAsMutableCount, type),
            count(finalModifierAlreadyPresentCount, type)
        );
    }

    void finalModifierAdded(VariableType type) {
        increment(finalModifierAddedCount, type);
    }

    void annotatedAsMutable(VariableType type) {
        increment(annotatedAsMutableCount, type);
    }

    void finalModifierAlreadyPresent(VariableType type) {
        increment(finalModifierAlreadyPresentCount, type);
    }

    enum VariableType {
        LOCAL, FIELD, PARAMETER
    }

    static final class Metrics {

        final int inspectedCount;
        final BigDecimal finalModifierAddedPercentage;
        final BigDecimal annotatedAsMutablePercentage;
        final BigDecimal finalModifierAlreadyPresentPercentage;

        private Metrics(
            int inspectedCount,
            BigDecimal finalModifierAddedPercentage,
            BigDecimal annotatedAsMutablePercentage,
            BigDecimal finalModifierAlreadyPresentPercentage
        ) {
            this.inspectedCount = inspectedCount;
            this.finalModifierAddedPercentage = finalModifierAddedPercentage;
            this.annotatedAsMutablePercentage = annotatedAsMutablePercentage;
            this.finalModifierAlreadyPresentPercentage = finalModifierAlreadyPresentPercentage;
        }

        private static Optional<Metrics> calculate(
            int finalModifierAdded,
            int annotatedAsMutable,
            int finalModifierAlreadyPresent
        ) {
            int inspected = finalModifierAdded + finalModifierAlreadyPresent + annotatedAsMutable;

            if (inspected == 0) {
                return Optional.empty();
            }

            return Optional.of(new Metrics(
                inspected,
                percentage(finalModifierAdded, inspected),
                percentage(annotatedAsMutable, inspected),
                percentage(finalModifierAlreadyPresent, inspected)
            ));
        }
    }
}
