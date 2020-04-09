package ristretto.compiler.plugin;

import java.util.Objects;

abstract class StringTypeAlias {

    private final String value;

    protected StringTypeAlias(String value) {
        this.value = value;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StringTypeAlias that = (StringTypeAlias) o;
        return value.equals(that.value);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public final String toString() {
        return value;
    }
}
