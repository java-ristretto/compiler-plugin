package ristretto.compiler.plugin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import ristretto.Mutable;
import ristretto.Nullable;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class QualifiedClassNameResolverTest {

    QualifiedClassNameResolver resolver;

    @BeforeEach
    void beforeEach() {
        resolver = QualifiedClassNameResolver.newResolver();
    }

    @Nested
    class when_resolving_mutable_annotation {

        @Nested
        class when_there_is_a_qualified_import {

            @BeforeEach
            void beforeEach() {
                resolver.importClass(Mutable.class.getName());
            }

            @Test
            void resolves_qualified_name() {
                assertThat(resolver.isMutable(Mutable.class.getName()), is(true));
            }

            @Test
            void resolves_simple_name() {
                assertThat(resolver.isMutable(Mutable.class.getSimpleName()), is(true));
            }
        }

        @Nested
        class when_there_is_a_wildcard_import {

            @BeforeEach
            void beforeEach() {
                resolver.importClass("ristretto.*");
            }

            @Test
            void resolves_qualified_name() {
                assertThat(resolver.isMutable(Mutable.class.getName()), is(true));
            }

            @Test
            void resolves_simple_name() {
                assertThat(resolver.isMutable(Mutable.class.getSimpleName()), is(true));
            }
        }

        @Nested
        class when_there_is_no_import {

            @Test
            void resolves_qualified_name() {
                assertThat(resolver.isMutable(Mutable.class.getName()), is(true));
            }

            @Test
            void does_not_resolve_simple_name() {
                assertThat(resolver.isMutable(Mutable.class.getSimpleName()), is(false));
            }
        }

        @Nested
        class when_there_is_an_import_of_another_mutable_class {

            @BeforeEach
            void beforeEach() {
                resolver.importClass("some.other." + Mutable.class.getSimpleName());
            }

            @Test
            void resolves_qualified_name() {
                assertThat(resolver.isMutable(Mutable.class.getName()), is(true));
            }

            @Test
            void does_not_resolve_simple_name() {
                assertThat(resolver.isMutable(Mutable.class.getSimpleName()), is(false));
            }

            @Test
            void does_not_resolve_unknown_qualified_name() {
                assertThat(resolver.isMutable("some.other." + Mutable.class.getSimpleName()), is(false));
            }

            @Test
            void does_not_resolve_unknown_simple_name() {
                assertThat(resolver.isMutable("SomeClass"), is(false));
            }
        }
    }

    @Nested
    class when_resolving_nullable_annotation {

        @Nested
        class when_there_is_a_qualified_import {

            @BeforeEach
            void beforeEach() {
                resolver.importClass(Nullable.class.getName());
            }

            @Test
            void resolves_qualified_name() {
                assertThat(resolver.isNullable(Nullable.class.getName()), is(true));
            }

            @Test
            void resolves_simple_name() {
                assertThat(resolver.isNullable(Nullable.class.getSimpleName()), is(true));
            }
        }

        @Nested
        class when_there_is_a_wildcard_import {

            @BeforeEach
            void beforeEach() {
                resolver.importClass("ristretto.*");
            }

            @Test
            void resolves_qualified_name() {
                assertThat(resolver.isNullable(Nullable.class.getName()), is(true));
            }

            @Test
            void resolves_simple_name() {
                assertThat(resolver.isNullable(Nullable.class.getSimpleName()), is(true));
            }
        }

        @Nested
        class when_there_is_no_import {

            @Test
            void resolves_qualified_name() {
                assertThat(resolver.isNullable(Nullable.class.getName()), is(true));
            }

            @Test
            void does_resolve_simple_name() {
                assertThat(resolver.isNullable(Nullable.class.getSimpleName()), is(false));
            }
        }

        @Nested
        class when_there_is_an_import_of_another_mutable_class {

            @BeforeEach
            void beforeEach() {
                resolver.importClass("some.other." + Nullable.class.getSimpleName());
            }

            @Test
            void resolves_qualified_name() {
                assertThat(resolver.isNullable(Nullable.class.getName()), is(true));
            }

            @Test
            void does_not_resolve_simple_name() {
                assertThat(resolver.isNullable(Nullable.class.getSimpleName()), is(false));
            }

            @Test
            void does_not_resolve_unknown_qualified_name() {
                assertThat(resolver.isNullable("some.other." + Nullable.class.getSimpleName()), is(false));
            }

            @Test
            void does_not_resolve_unknown_simple_name() {
                assertThat(resolver.isNullable("SomeClass"), is(false));
            }
        }
    }
}
