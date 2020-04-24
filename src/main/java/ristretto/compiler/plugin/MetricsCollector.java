package ristretto.compiler.plugin;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

final class MetricsCollector implements MethodParameterFinalModifier.Observer {

    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

    private int markedAsFinal;
    private int skipped;

    private MetricsCollector() {
    }

    static MetricsCollector newCollector() {
        return new MetricsCollector();
    }

    private static BigDecimal percentage(int count, int total) {
        return BigDecimal.valueOf(count)
            .divide(BigDecimal.valueOf(total), 4, RoundingMode.FLOOR)
            .multiply(HUNDRED)
            .setScale(2, RoundingMode.FLOOR);
    }

    @Override
    public void parameterMarkedAsFinal() {
        markedAsFinal += 1;
    }

    @Override
    public void parameterSkipped() {
        skipped += 1;
    }

    Optional<Metrics> calculate() {
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

    static final class Metrics {

        final int inspectedCount;
        final BigDecimal markedAsFinalPercentage;
        final BigDecimal skippedPercentage;

        private Metrics(int inspectedCount, BigDecimal markedAsFinalPercentage, BigDecimal skippedPercentage) {
            this.inspectedCount = inspectedCount;
            this.markedAsFinalPercentage = markedAsFinalPercentage;
            this.skippedPercentage = skippedPercentage;
        }
    }
}
