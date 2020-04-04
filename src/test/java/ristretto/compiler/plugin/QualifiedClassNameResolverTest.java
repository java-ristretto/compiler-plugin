package ristretto.compiler.plugin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class QualifiedClassNameResolverTest {

    QualifiedClassNameResolver resolver;

    @Nested
    class when_there_is_a_qualified_import {

        @BeforeEach
        void beforeEach() {
            resolver = QualifiedClassNameResolver.newResolver();
            resolver.importClass(QualifiedImport.of(QualifiedName.parse("some.qualified.ClassName")));
        }

        @Test
        void resolves_qualified_name() {
            assertThat(resolver.resolve(QualifiedName.parse("some.qualified.ClassName")), is(QualifiedClassName.of(QualifiedName.parse("some.qualified.ClassName"))));
        }

        @Test
        void resolves_simple_name() {
            assertThat(resolver.resolve(QualifiedName.parse("ClassName")), is(QualifiedClassName.of(QualifiedName.parse("some.qualified.ClassName"))));
        }
    }

    @Nested
    class when_there_is_a_wildcard_import {

        @BeforeEach
        void beforeEach() {
            resolver = QualifiedClassNameResolver.newResolver(QualifiedClassName.of(QualifiedName.parse("some.qualified.ClassName")));
            resolver.importClass(QualifiedImport.of(QualifiedName.parse("some.qualified.*")));
        }

        @Test
        void resolves_qualified_name() {
            assertThat(resolver.resolve(QualifiedName.parse("some.qualified.ClassName")), is(QualifiedClassName.of(QualifiedName.parse("some.qualified.ClassName"))));
        }

        @Test
        void resolves_simple_name() {
            assertThat(resolver.resolve(QualifiedName.parse("ClassName")), is(QualifiedClassName.of(QualifiedName.parse("some.qualified.ClassName"))));
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
            assertThat(resolver.resolve(QualifiedName.parse("some.qualified.ClassName")), is(QualifiedClassName.of(QualifiedName.parse("some.qualified.ClassName"))));
        }

        @Test
        void resolves_simple_name() {
            assertThat(resolver.resolve(QualifiedName.parse("ClassName")), is(QualifiedClassName.of(QualifiedName.parse("ClassName"))));
        }
    }

    @Test
    void indicates_classes_of_interest_must_have_a_package() {
        var exception = assertThrows(IllegalArgumentException.class, () -> QualifiedClassNameResolver.newResolver(QualifiedClassName.of(QualifiedName.parse("ClassName"))));

        assertThat(exception.getMessage(), is("illegal class of interest: 'ClassName'"));
    }
}
