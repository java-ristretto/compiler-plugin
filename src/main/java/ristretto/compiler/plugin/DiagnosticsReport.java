package ristretto.compiler.plugin;

import com.sun.source.tree.VariableTree;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.DiagnosticSource;

import javax.tools.FileObject;
import javax.tools.JavaFileObject;
import java.net.URI;
import java.util.Optional;

final class DiagnosticsReport implements DefaultImmutabilityRule.Observer {

    private final MetricsCollector metrics;
    private final RistrettoLogger logger;
    private final Optional<JavaFileObject> javaFile;

    DiagnosticsReport(RistrettoLogger logger) {
        this(new MetricsCollector(), logger, null);
    }

    private DiagnosticsReport(MetricsCollector metrics, RistrettoLogger logger, JavaFileObject javaFile) {
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
    public void finalModifierAdded(DefaultImmutabilityRule.VariableScope scope) {
        metrics.finalModifierAdded(toVariableType(scope));
    }

    @Override
    public void finalModifierAlreadyPresent(VariableTree variable, DefaultImmutabilityRule.VariableScope scope) {
        metrics.finalModifierAlreadyPresent(toVariableType(scope));

        logger.diagnostic(
            String.format(
                "warning: %s variable %s has unnecessary final modifier",
                positionOf(variable),
                variable.getName()
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
    public void annotatedAsMutable(DefaultImmutabilityRule.VariableScope scope) {
        metrics.annotatedAsMutable(toVariableType(scope));
    }

    void pluginFinished() {
        logger.summary("immutable by default summary:");
        logger.summary("| var type  | inspected   | final   | skipped | annotated |");
        logger.summary("|-----------|-------------|---------|---------|-----------|");
        logger.summary(formatMetrics(MetricsCollector.VariableType.FIELD));
        logger.summary(formatMetrics(MetricsCollector.VariableType.LOCAL));
        logger.summary(formatMetrics(MetricsCollector.VariableType.PARAMETER));
    }

    private String formatMetrics(MetricsCollector.VariableType type) {
        return metrics.calculate(type)
            .map(metricsForScope ->
                String.format(
                    "| %-9s | %,11d | %6.2f%% | %6.2f%% |   %6.2f%% |",
                    type.name().toLowerCase(),
                    metricsForScope.inspectedCount,
                    metricsForScope.finalModifierAddedPercentage,
                    metricsForScope.finalModifierAlreadyPresentPercentage,
                    metricsForScope.annotatedAsMutablePercentage
                )
            )
            .orElse("| %-9s |           0 |       - |       - |         - |");
    }

    private static MetricsCollector.VariableType toVariableType(DefaultImmutabilityRule.VariableScope scope) {
        switch (scope) {
            case BLOCK:
                return MetricsCollector.VariableType.LOCAL;
            case CLASS:
            case ENUM:
                return MetricsCollector.VariableType.FIELD;
            case METHOD:
                return MetricsCollector.VariableType.PARAMETER;
            default:
                throw new AssertionError("cannot handle variable scope " + scope);
        }
    }
}
