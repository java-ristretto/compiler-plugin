package ristretto.compiler.plugin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ImportDeclarationTest {

    ImportDeclaration importDeclaration;

    @Nested
    class when_declaration_includes_class_name {

        @BeforeEach
        void beforeEach() {
            importDeclaration = ImportDeclaration.parse("some.package.ClassName");
        }

        @Test
        void has_package_name() {
            assertThat(importDeclaration.packageName(), is(new PackageName("some.package")));
        }

        @Test
        void has_class_name() {
            assertThat(importDeclaration.qualifiedName(), is(Optional.of(ClassReference.parse("some.package.ClassName"))));
        }
    }

    @Nested
    class when_declaration_is_on_demand {

        @BeforeEach
        void beforeEach() {
            importDeclaration = ImportDeclaration.parse("some.package.*");
        }

        @Test
        void has_package_name() {
            assertThat(importDeclaration.packageName(), is(new PackageName("some.package")));
        }

        @Test
        void does_not_have_class_name() {
            assertThat(importDeclaration.qualifiedName(), is(Optional.empty()));
        }
    }

    @Test
    void indicates_declaration_is_illegal_when_missing_package() {
        var exception = assertThrows(IllegalArgumentException.class, () -> ImportDeclaration.parse("ClassName"));

        assertThat(exception.getMessage(), is("illegal import declaration: 'ClassName'"));
    }
}