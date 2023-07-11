package middleware;

public abstract class Middleware {
    private Middleware next;

    /*
     * Building a chain of objects
     * */
    public static Middleware link(Middleware first, Middleware... chain) {
        Middleware head = first;
        for (Middleware nextInChain : chain) {
            head.next = nextInChain;
            head = nextInChain;
        }
        return first;
    }

    /*
     * Here subclasses will implement their logic
     * */
    public abstract boolean check(String email, String password);

    /*
     * Last object in chain?
     * If not we go with next obj in line
     * */
    protected boolean checkNext(String email, String password) {
        if (next == null) {
            return false;
        }
        return next.check(email, password);
    }
}
