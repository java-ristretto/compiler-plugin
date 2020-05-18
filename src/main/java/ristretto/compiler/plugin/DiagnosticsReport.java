package ristretto.compiler.plugin;

import com.sun.source.tree.VariableTree;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.DiagnosticSource;

import javax.tools.FileObject;
import javax.tools.JavaFileObject;
import java.net.URI;
import java.util.Optional;

final class DiagnosticsReport implements VariableFinalModifier.Observer {

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
    public void finalModifierAdded(VariableFinalModifier.VariableScope scope) {
        metrics.finalModifierAdded(scope);
    }

    @Override
    public void finalModifierAlreadyPresent(VariableTree variable, VariableFinalModifier.VariableScope scope) {
        metrics.finalModifierAlreadyPresent(scope);

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
    public void annotatedAsMutable(VariableFinalModifier.VariableScope scope) {
        metrics.annotatedAsMutable(scope);
    }

    void pluginFinished() {
        logger.summary("immutable by default summary:");
        logger.summary("| var type  | inspected   | final   | skipped | annotated |");
        logger.summary("|-----------|-------------|---------|---------|-----------|");
        logger.summary(formatMetrics(VariableFinalModifier.VariableScope.CLASS)); // TODO: count VariableScope.ENUM
        logger.summary(formatMetrics(VariableFinalModifier.VariableScope.BLOCK));
        logger.summary(formatMetrics(VariableFinalModifier.VariableScope.METHOD));
    }

    private String formatMetrics(VariableFinalModifier.VariableScope scope) {
        return metrics.calculate(scope)
            .map(metricsForScope ->
                String.format(
                    "| %-9s | %,11d | %6.2f%% | %6.2f%% |   %6.2f%% |",
                    describe(scope),
                    metricsForScope.inspectedCount,
                    metricsForScope.finalModifierAddedPercentage,
                    metricsForScope.finalModifierAlreadyPresentPercentage,
                    metricsForScope.annotatedAsMutablePercentage
                )
            )
            .orElse("| %-9s |           0 |       - |       - |         - |");
    }

    private static String describe(VariableFinalModifier.VariableScope scope) {
        switch (scope) {
            case CLASS:
                return "field";
            case BLOCK:
                return "local";
            case METHOD:
                return "parameter";
            default:
                throw new AssertionError("unexpected scope " + scope);
        }
    }
}
