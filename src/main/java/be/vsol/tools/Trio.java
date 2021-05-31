package be.vsol.tools;

import java.util.Objects;

public class Trio<A, B, C> {

    private A a;
    private B b;
    private C c;

    public Trio(A a, B b, C c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    @Override public String toString() {
        return "<" + (a == null ? "null" : a.toString()) + ", " + (b == null ? "null" : b.toString()) + ", " + (c == null ? "null" : c.toString()) + ">";
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Trio<?, ?, ?> trio = (Trio<?, ?, ?>) o;
        return Objects.equals(a, trio.a) && Objects.equals(b, trio.b) && Objects.equals(c, trio.c);
    }

    @Override public int hashCode() {
        return Objects.hash(a, b, c);
    }

    // GETTERS

    public A getA() {
        return a;
    }

    public B getB() {
        return b;
    }

    public C getC() {
        return c;
    }

    // SETTERS

    public void setA(A a) {
        this.a = a;
    }

    public void setB(B b) {
        this.b = b;
    }

    public void setC(C c) {
        this.c = c;
    }
}
