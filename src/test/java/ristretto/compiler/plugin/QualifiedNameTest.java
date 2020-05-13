package ristretto.compiler.plugin;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.not;

class QualifiedNameTest {

    @Test
    void can_be_used_as_key() {
        var packageName = new PackageName("some.package");
        var simpleName = new SimpleName("ClassName");
        var anotherSimpleName = new SimpleName("AnotherClassName");

        Map<QualifiedName, String> map = Map.of(packageName.qualify(simpleName), "value");

        assertThat(map, hasKey(packageName.qualify(simpleName)));
        assertThat(map, not(hasKey(packageName.qualify(anotherSimpleName))));
    }
}