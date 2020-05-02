package ristretto.compiler.plugin;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

final class MetricsCollector implements MethodParameterFinalModifier.Observer, LocalVariableFinalModifier.Observer {

    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

    private int parametersMarkedAsFinal;
    private int parametersSkipped;
    private int localVariablesMarkedAsFinal;
    private int localVariablesSkipped;

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
        parametersMarkedAsFinal += 1;
    }

    @Override
    public void parameterSkipped() {
        parametersSkipped += 1;
    }

    Optional<Metrics> calculateParameter() {
        return Metrics.calculate(parametersMarkedAsFinal, parametersSkipped);
    }

    @Override
    public void localVariableMarkedAsFinal() {
        localVariablesMarkedAsFinal += 1;
    }

    @Override
    public void localVariableSkipped() {
        localVariablesSkipped += 1;
    }

    Optional<Metrics> calculateLocalVariable() {
        return Metrics.calculate(localVariablesMarkedAsFinal, localVariablesSkipped);
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
