package ristretto.compiler.plugin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class ClassReferenceTest {

    ClassReference classReference;

    @Nested
    class when_package_is_present {

        @BeforeEach
        void beforeEach() {
            classReference = ClassReference.parse("some.package.ClassName");
        }

        @Test
        void parses_class_reference_into_qualified_name() {
            assertThat(classReference, is(new QualifiedName(new PackageName("some.package"), new SimpleName("ClassName"))));
        }
    }

    @Nested
    class when_package_is_not_present {

        @BeforeEach
        void beforeEach() {
            classReference = ClassReference.parse("ClassName");
        }

        @Test
        void parses_class_reference_into_simple_name() {
            assertThat(classReference, is(new SimpleName("ClassName")));
        }
    }
}