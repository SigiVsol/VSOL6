package be.vsol.tools;

import java.util.Objects;

public class Duo<A, B> {

    private A a;
    private B b;

    public Duo(A a, B b) {
        this.a = a;
        this.b = b;
    }

    @Override public String toString() {
        return "<" + (a == null ? "null" : a.toString()) + ", " + (b == null ? "null" : b.toString()) + ">";
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Duo<?, ?> duo = (Duo<?, ?>) o;
        return Objects.equals(a, duo.a) && Objects.equals(b, duo.b);
    }

    @Override public int hashCode() {
        return Objects.hash(a, b);
    }

    // GETTERS

    public A getA() {
        return a;
    }

    public B getB() {
        return b;
    }

    // SETTERS

    public void setA(A a) {
        this.a = a;
    }

    public void setB(B b) {
        this.b = b;
    }

}
