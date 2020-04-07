package ristretto.compiler.plugin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class QualifiedClassNameResolverTest {

    QualifiedClassNameResolver resolver;

    @Nested
    class when_there_is_a_qualified_import {

        @BeforeEach
        void beforeEach() {
            resolver = QualifiedClassNameResolver.newResolver();
            resolver.importClass(QualifiedImport.parse("some.qualified.ClassName"));
        }

        @Test
        void resolves_qualified_name() {
            assertThat(resolver.resolve(QualifiedClassName.parse("some.qualified.ClassName")), is(QualifiedClassName.parse("some.qualified.ClassName")));
        }

        @Test
        void resolves_simple_name() {
            assertThat(resolver.resolve(QualifiedClassName.parse("ClassName")), is(QualifiedClassName.parse("some.qualified.ClassName")));
        }
    }

    @Nested
    class when_there_is_a_wildcard_import {

        @BeforeEach
        void beforeEach() {
            resolver = QualifiedClassNameResolver.newResolver(java.util.List.class);
            resolver.importClass(QualifiedImport.parse("java.util.*"));
        }

        @Test
        void resolves_qualified_name() {
            assertThat(resolver.resolve(QualifiedClassName.parse("java.util.List")), is(QualifiedClassName.parse("java.util.List")));
        }

        @Test
        void resolves_simple_name() {
            assertThat(resolver.resolve(QualifiedClassName.parse("List")), is(QualifiedClassName.parse("java.util.List")));
        }
    }

    @Nested
    class when_there_is_no_import {

        @BeforeEach
        void beforeEach() {
            resolver = QualifiedClassNameResolver.newResolver();
        }

        @Test
        void resolves_qualified_name() {
            assertThat(resolver.resolve(QualifiedClassName.parse("some.qualified.ClassName")), is(QualifiedClassName.parse("some.qualified.ClassName")));
        }

        @Test
        void resolves_simple_name() {
            assertThat(resolver.resolve(QualifiedClassName.parse("ClassName")), is(QualifiedClassName.parse("ClassName")));
        }
    }
}
