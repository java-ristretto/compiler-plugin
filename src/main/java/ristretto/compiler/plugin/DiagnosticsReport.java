package ristretto.compiler.plugin;

import com.sun.source.tree.VariableTree;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.DiagnosticSource;

import javax.tools.FileObject;
import javax.tools.JavaFileObject;
import java.net.URI;
import java.util.Optional;

final class DiagnosticsReport implements DefaultModifierRule.Listener {

    private final MetricsCollector<Class<? extends DefaultModifierRule>, EventType> metrics;
    private final RistrettoLogger logger;
    private final Optional<JavaFileObject> javaFile;

    DiagnosticsReport(RistrettoLogger logger) {
        this(new MetricsCollector<>(), logger, null);
    }

    private DiagnosticsReport(
        MetricsCollector<Class<? extends DefaultModifierRule>, EventType> metrics,
        RistrettoLogger logger,
        JavaFileObject javaFile
    ) {
        this.metrics = metrics;
        this.logger = logger;
        this.javaFile = Optional.ofNullable(javaFile);
    }

    DiagnosticsReport withJavaFile(JavaFileObject javaFile) {
        return new DiagnosticsReport(metrics, logger, javaFile);
    }

    void pluginLoaded() {
        logger.summary("ristretto plugin loaded");
    }

    @Override
    public void modifierAdded(DefaultModifierRule source, Variable target) {
        handleEvent(source, target, EventType.MODIFIER_ADDED);
    }

    @Override
    public void modifierNotAdded(DefaultModifierRule source, Variable target) {
        handleEvent(source, target, EventType.MODIFIER_NOT_ADDED);
    }

    @Override
    public void modifierAlreadyPresent(DefaultModifierRule source, Variable target) {
        handleEvent(source, target, EventType.MODIFIER_ALREADY_PRESENT);
    }

    private void handleEvent(DefaultModifierRule source, Variable target, EventType eventType) {
        metrics.count(source.getClass(), eventType);
        logger.diagnostic(String.format("%s %s %s", source.getClass().getSimpleName(), target.position(), eventType));
    }

    @Override
    public void modifierAdded(DefaultModifierRule source, VariableTree target) {
        handleEvent(source, target, EventType.MODIFIER_ADDED);
    }

    @Override
    public void modifierNotAdded(DefaultModifierRule source, VariableTree target) {
        handleEvent(source, target, EventType.MODIFIER_NOT_ADDED);
    }

    @Override
    public void modifierAlreadyPresent(DefaultModifierRule source, VariableTree target) {
        handleEvent(source, target, EventType.MODIFIER_ALREADY_PRESENT);
    }

    private void handleEvent(DefaultModifierRule source, VariableTree target, EventType eventType) {
        metrics.count(source.getClass(), eventType);
        logger.diagnostic(String.format("%s %s %s", source.getClass().getSimpleName(), positionOf(target), eventType));
    }

    private String positionOf(VariableTree variable) {
        String filePath = javaFile
            .map(FileObject::toUri)
            .map(URI::getPath)
            .orElse("[unknown source]");

        int lineNumber = javaFile
            .map(file -> new DiagnosticSource(file, null))
            .map(file -> file.getLineNumber(((JCTree.JCVariableDecl) variable).getPreferredPosition()))
            .orElse(0);

        return filePath + ":" + lineNumber;
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
