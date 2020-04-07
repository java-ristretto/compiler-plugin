package ristretto.compiler.plugin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

class QualifiedClassNameTest {

    @Nested
    class when_package_is_present {

        QualifiedClassName className;

        @BeforeEach
        void beforeEach() {
            className = QualifiedClassName.parse("some.package.ClassName");
        }

        @Test
        void parses_package_name() {
            assertThat(className.packageName(), is(Optional.of("some.package")));
        }

        @Test
        void parses_simple_name() {
            assertThat(className.simpleName(), is("ClassName"));
        }
    }

    @Nested
    class when_package_is_not_present {

        QualifiedClassName className;

        @BeforeEach
        void beforeEach() {
            className = QualifiedClassName.parse("ClassName");
        }

        @Test
        void parses_package_name() {
            assertThat(className.packageName(), is(Optional.empty()));
        }

        @Test
        void parses_simple_name() {
            assertThat(className.simpleName(), is("ClassName"));
        }
    }

    @Test
    void has_a_string_representation() {
        var className = QualifiedClassName.parse("some.package.ClassName");

        assertThat(className.toString(), is("some.package.ClassName"));
    }

    @Test
    void can_be_used_as_key() {
        var className = QualifiedClassName.parse("some.package.ClassName");
        var sameClassName = QualifiedClassName.parse("some.package.ClassName");
        var anotherClassName = QualifiedClassName.parse("some.package.AnotherClassName");

        Map<QualifiedClassName, String> map = Map.of(className, "value");

        assertThat(map, hasKey(sameClassName));
        assertThat(map, not(hasKey(anotherClassName)));
    }
}