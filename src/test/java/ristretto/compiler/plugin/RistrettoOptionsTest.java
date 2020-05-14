package ristretto.compiler.plugin;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class RistrettoOptionsTest {

    @Test
    void indicates_when_standard_error_output_is_enabled() {
        RistrettoOptions options = RistrettoOptions.parse("--output=stderr");

        assertThat(options.isStandardErrorOutputEnabled(), is(true));
    }

    @Test
    void indicates_when_standard_error_output_is_disabled() {
        RistrettoOptions options = RistrettoOptions.parse();

        assertThat(options.isStandardErrorOutputEnabled(), is(false));
    }
}