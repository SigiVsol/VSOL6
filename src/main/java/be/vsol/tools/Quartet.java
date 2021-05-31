package be.vsol.tools;

import java.util.Objects;

public class Quartet<A, B, C, D> {

    private A a;
    private B b;
    private C c;
    private D d;

    public Quartet(A a, B b, C c, D d) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
    }

    @Override public String toString() {
        return "<" + (a == null ? "null" : a.toString()) + ", " + (b == null ? "null" : b.toString()) + ", " + (c == null ? "null" : c.toString()) + ", " + (d == null ? "null" : d.toString()) + ">";
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Quartet<?, ?, ?, ?> quartet = (Quartet<?, ?, ?, ?>) o;
        return Objects.equals(a, quartet.a) && Objects.equals(b, quartet.b) && Objects.equals(c, quartet.c) && Objects.equals(d, quartet.d);
    }

    @Override public int hashCode() {
        return Objects.hash(a, b, c, d);
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

    public D getD() {
        return d;
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

    public void setD(D d) {
        this.d = d;
    }
}
