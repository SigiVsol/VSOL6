package be.vsol.tools;

public class Twin<A> extends Duo<A, A> {

    public Twin(A a1, A a2) {
        super(a1, a2);
    }

    public Twin(A a) {
        super(a, a);
    }

}
