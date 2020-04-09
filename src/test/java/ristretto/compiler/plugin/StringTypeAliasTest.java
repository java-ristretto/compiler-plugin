package ristretto.compiler.plugin;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.not;

class StringTypeAliasTest {

    @Test
    void can_be_used_as_key() {
        var name = new Name("name");
        var sameName = new Name("name");
        var anotherName = new Name("another name");

        Map<Name, String> map = Map.of(name, "value");

        assertThat(map, hasKey(sameName));
        assertThat(map, not(hasKey(anotherName)));
    }

    private static final class Name extends StringTypeAlias {
        protected Name(String value) {
            super(value);
        }
    }
}