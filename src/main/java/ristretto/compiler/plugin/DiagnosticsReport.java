package ristretto.compiler.plugin;

final class DiagnosticsReport implements DefaultModifierRule.Listener {

    private final MetricsCollector<Class<? extends DefaultModifierRule>, EventType> metrics;
    private final RistrettoLogger logger;

    DiagnosticsReport(RistrettoLogger logger) {
        this.metrics = new MetricsCollector<>();
        this.logger = logger;
    }

    void pluginLoaded() {
        logger.summary("ristretto plugin loaded");
    }

    @Override
    public void modifierAdded(DefaultModifierRule source, ModifierTarget target) {
        handleEvent(source, target, EventType.MODIFIER_ADDED);
    }

    @Override
    public void modifierNotAdded(DefaultModifierRule source, ModifierTarget target) {
        handleEvent(source, target, EventType.MODIFIER_NOT_ADDED);
    }

    @Override
    public void modifierAlreadyPresent(DefaultModifierRule source, ModifierTarget target) {
        handleEvent(source, target, EventType.MODIFIER_ALREADY_PRESENT);
    }

    private void handleEvent(DefaultModifierRule source, ModifierTarget target, EventType eventType) {
        metrics.count(source.getClass(), eventType);
        logger.diagnostic(String.format("%s %s %s", source.getClass().getSimpleName(), target.position(), eventType));
    }

    void pluginFinished() {
        logger.summary("summary:");
        logger.summary("| rule                                     | inspected   | added   | present | not added |");
        logger.summary("|------------------------------------------|-------------|---------|---------|-----------|");
        logger.summary(formatMetrics(DefaultFieldImmutabilityRule.class));
        logger.summary(formatMetrics(DefaultParameterImmutabilityRule.class));
        logger.summary(formatMetrics(DefaultLocalVariableImmutabilityRule.class));
        logger.summary(formatMetrics(DefaultFieldAccessRule.class));
    }

    private String formatMetrics(Class<? extends DefaultModifierRule> rule) {
        String eventSourceName = rule.getSimpleName();

        return metrics.calculate(rule)
            .map(percentages ->
                String.format(
                    "| %-40s | %,11d | %6.2f%% | %6.2f%% |   %6.2f%% |",
                    eventSourceName,
                    percentages.getTotal(),
                    percentages.percentage(EventType.MODIFIER_ADDED),
                    percentages.percentage(EventType.MODIFIER_ALREADY_PRESENT),
                    percentages.percentage(EventType.MODIFIER_NOT_ADDED)
                )
            )
            .orElse(String.format("| %-40s |           0 |       - |       - |         - |", eventSourceName));
    }

    private enum EventType {
        MODIFIER_ADDED,
        MODIFIER_ALREADY_PRESENT,
        MODIFIER_NOT_ADDED
    }
}
