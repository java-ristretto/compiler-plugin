package ristretto.compiler.plugin;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

final class MetricsCollector<S> {

    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

    private final Map<EventType, Map<S, ValueHolder>> eventCount = new HashMap<>();

    private static final class ValueHolder {
        int value;
    }

    private static <T> int count(Map<T, ValueHolder> countByKey, T key) {
        ValueHolder count = countByKey.get(key);
        if (count == null) {
            return 0;
        }
        return count.value;
    }

    private static <T> void increment(Map<T, ValueHolder> countByKey, T key) {
        countByKey.computeIfAbsent(key, newKey -> new ValueHolder()).value += 1;
    }

    private static BigDecimal percentage(int count, int total) {
        return BigDecimal.valueOf(count)
            .divide(BigDecimal.valueOf(total), 4, RoundingMode.FLOOR)
            .multiply(HUNDRED)
            .setScale(2, RoundingMode.FLOOR);
    }

    Optional<Metrics> calculate(S eventSource) {
        return Metrics.calculate(
            count(eventCount.getOrDefault(EventType.FINAL_MODIFIER_ADDED, Collections.emptyMap()), eventSource),
            count(eventCount.getOrDefault(EventType.ANNOTATED_AS_MUTABLE, Collections.emptyMap()), eventSource),
            count(eventCount.getOrDefault(EventType.FINAL_MODIFIER_ALREADY_PRESENT, Collections.emptyMap()), eventSource)
        );
    }

    void finalModifierAdded(S eventSource) {
        increment(EventType.FINAL_MODIFIER_ADDED, eventSource);
    }

    void annotatedAsMutable(S eventSource) {
        increment(EventType.ANNOTATED_AS_MUTABLE, eventSource);
    }

    void finalModifierAlreadyPresent(S eventSource) {
        increment(EventType.FINAL_MODIFIER_ALREADY_PRESENT, eventSource);
    }

    void increment(EventType eventType, S eventSource) {
        increment(eventCount.computeIfAbsent(eventType, newEventType -> new HashMap<>()), eventSource);
    }

    enum EventType {
        FINAL_MODIFIER_ADDED,
        FINAL_MODIFIER_ALREADY_PRESENT,
        ANNOTATED_AS_MUTABLE
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
