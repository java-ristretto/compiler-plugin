package ristretto.compiler.plugin;

import com.sun.source.tree.VariableTree;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.DiagnosticSource;

import javax.tools.FileObject;
import javax.tools.JavaFileObject;
import java.net.URI;
import java.util.Optional;

final class DiagnosticsReport implements DefaultImmutabilityRule.Listener, DefaultFieldAccessRule.Listener {

    private final MetricsCollector<DefaultImmutabilityRule.EventSource, EventType> immutabilityMetrics;
    private final MetricsCollector<String, EventType> privateAccessMetrics;
    private final RistrettoLogger logger;
    private final Optional<JavaFileObject> javaFile;

    DiagnosticsReport(RistrettoLogger logger) {
        this(new MetricsCollector<>(), new MetricsCollector<>(), logger, null);
    }

    private DiagnosticsReport(
        MetricsCollector<DefaultImmutabilityRule.EventSource, EventType> immutabilityMetrics,
        MetricsCollector<String, EventType> privateAccessMetrics,
        RistrettoLogger logger,
        JavaFileObject javaFile
    ) {
        this.immutabilityMetrics = immutabilityMetrics;
        this.privateAccessMetrics = privateAccessMetrics;
        this.logger = logger;
        this.javaFile = Optional.ofNullable(javaFile);
    }

    DiagnosticsReport withJavaFile(JavaFileObject javaFile) {
        return new DiagnosticsReport(immutabilityMetrics, privateAccessMetrics, logger, javaFile);
    }

    void pluginLoaded() {
        logger.summary("ristretto plugin loaded");
    }

    @Override
    public void markedAsFinal(DefaultImmutabilityRule source, VariableTree target, DefaultImmutabilityRule.EventSource eventSource) {
        immutabilityMetrics.count(eventSource, EventType.FINAL_MODIFIER_ADDED);
    }

    @Override
    public void alreadyMarkedAsFinal(DefaultImmutabilityRule source, VariableTree target, DefaultImmutabilityRule.EventSource eventSource) {
        immutabilityMetrics.count(eventSource, EventType.FINAL_MODIFIER_ALREADY_PRESENT);

        logger.diagnostic(
            String.format(
                "warning: %s variable %s has unnecessary final modifier",
                positionOf(target),
                target.getName()
            )
        );
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

    @Override
    public void annotatedAsMutable(DefaultImmutabilityRule source, VariableTree target, DefaultImmutabilityRule.EventSource eventSource) {
        immutabilityMetrics.count(eventSource, EventType.ANNOTATED_AS_MUTABLE);
    }

    @Override
    public void markedAsPrivate(DefaultFieldAccessRule source, VariableTree target) {
        privateAccessMetrics.count(source.getClass().getSimpleName(), EventType.FINAL_MODIFIER_ADDED);
    }

    @Override
    public void annotatedAsPackagePrivate(DefaultFieldAccessRule source, VariableTree target) {
        privateAccessMetrics.count(source.getClass().getSimpleName(), EventType.ANNOTATED_AS_MUTABLE);
    }

    void pluginFinished() {
        logger.summary("immutable by default summary:");
        logger.summary("| var type  | inspected   | final   | skipped | annotated |");
        logger.summary("|-----------|-------------|---------|---------|-----------|");
        logger.summary(formatMetrics(DefaultImmutabilityRule.EventSource.FIELD));
        logger.summary(formatMetrics(DefaultImmutabilityRule.EventSource.LOCAL));
        logger.summary(formatMetrics(DefaultImmutabilityRule.EventSource.PARAMETER));

        logger.summary("default field access rule summary:");
        logger.summary("| member    | inspected   | marked  | skipped | annotated |");
        logger.summary("|-----------|-------------|---------|---------|-----------|");
        logger.summary(formatMetrics());
    }

    private String formatMetrics(DefaultImmutabilityRule.EventSource eventSource) {
        String eventSourceName = eventSource.name().toLowerCase();

        return immutabilityMetrics.calculate(eventSource)
            .map(percentages ->
                String.format(
                    "| %-9s | %,11d | %6.2f%% | %6.2f%% |   %6.2f%% |",
                    eventSourceName,
                    percentages.getTotal(),
                    percentages.percentage(EventType.FINAL_MODIFIER_ADDED),
                    percentages.percentage(EventType.FINAL_MODIFIER_ALREADY_PRESENT),
                    percentages.percentage(EventType.ANNOTATED_AS_MUTABLE)
                )
            )
            .orElse(String.format("| %-9s |           0 |       - |       - |         - |", eventSourceName));
    }

    private String formatMetrics() {
        String eventSourceName = DefaultFieldAccessRule.class.getSimpleName();

        return privateAccessMetrics.calculate(eventSourceName)
            .map(percentages ->
                String.format(
                    "| %-9s | %,11d | %6.2f%% | %6.2f%% |   %6.2f%% |",
                    eventSourceName,
                    percentages.getTotal(),
                    percentages.percentage(EventType.FINAL_MODIFIER_ADDED),
                    percentages.percentage(EventType.FINAL_MODIFIER_ALREADY_PRESENT),
                    percentages.percentage(EventType.ANNOTATED_AS_MUTABLE)
                )
            )
            .orElse(String.format("| %-9s |           0 |       - |       - |         - |", eventSourceName));
    }

    private enum EventType {
        FINAL_MODIFIER_ADDED,
        FINAL_MODIFIER_ALREADY_PRESENT,
        ANNOTATED_AS_MUTABLE
    }
}
