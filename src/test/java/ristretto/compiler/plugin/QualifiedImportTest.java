package ristretto.compiler.plugin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

class QualifiedImportTest {

    QualifiedImport qualifiedImport;

    @Nested
    class when_declaration_includes_class_name {

        @BeforeEach
        void beforeEach() {
            qualifiedImport = QualifiedImport.of(QualifiedName.parse("some.package.ClassName"));
        }

        @Test
        void has_package_name() {
            assertThat(qualifiedImport.packageName(), is("some.package"));
        }

        @Test
        void has_class_name() {
            assertThat(qualifiedImport.className(), is(Optional.of(QualifiedClassName.parse("some.package.ClassName"))));
        }

        @Test
        void has_a_string_representation() {
            assertThat(qualifiedImport.toString(), is("some.package.ClassName"));
        }
    }

    @Nested
    class when_declaration_is_on_demand {

        @BeforeEach
        void beforeEach() {
            qualifiedImport = QualifiedImport.of(QualifiedName.parse("some.package.*"));
        }

        @Test
        void has_package_name() {
            assertThat(qualifiedImport.packageName(), is("some.package"));
        }

        @Test
        void does_not_have_class_name() {
            assertThat(qualifiedImport.className(), is(Optional.empty()));
        }

        @Test
        void has_a_string_representation() {
            assertThat(qualifiedImport.toString(), is("some.package.*"));
        }
    }

    @Test
    void indicates_declaration_is_illegal_when_missing_package() {
        var exception = assertThrows(IllegalArgumentException.class, () -> QualifiedImport.of(QualifiedName.parse("ClassName")));

        assertThat(exception.getMessage(), is("illegal import declaration: 'ClassName'"));
    }
}