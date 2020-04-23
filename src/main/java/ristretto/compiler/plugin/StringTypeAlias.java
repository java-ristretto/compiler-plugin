package ristretto.compiler.plugin;

import java.util.Objects;

public abstract class StringTypeAlias {

    private final String value;

    protected StringTypeAlias(String value) {
        if (value == null) {
            throw new NullPointerException("value cannot be null");
        }
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
