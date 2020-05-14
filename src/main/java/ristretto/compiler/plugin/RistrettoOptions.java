package ristretto.compiler.plugin;

final class RistrettoOptions {

    private final boolean standardErrorOutputEnabled;

    private RistrettoOptions(boolean standardErrorOutputEnabled) {
        this.standardErrorOutputEnabled = standardErrorOutputEnabled;
    }

    static RistrettoOptions parse(String... args) {
        return new RistrettoOptions(args.length > 0 && "--output=stderr".equals(args[0]));
    }

    boolean isStandardErrorOutputEnabled() {
        return standardErrorOutputEnabled;
    }
}
