package ristretto.compiler.plugin;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class JavacPluginTest {

    @Test
    void test_some_library_method() {
        JavacPlugin classUnderTest = new JavacPlugin();

        assertThat(classUnderTest.someLibraryMethod(), is(true));
    }
}
