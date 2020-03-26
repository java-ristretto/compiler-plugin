package ristretto.compiler.plugin;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class JavacPluginTest {

    @Test
    void has_name() {
        JavacPlugin plugin = new JavacPlugin();

        assertThat(plugin.getName(), is("ristretto"));
    }
}
