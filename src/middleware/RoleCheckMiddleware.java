package middleware;


public class RoleCheckMiddleware extends Middleware {

    @Override
    public boolean check(String email, String password) {
        if (email.equals("admin@domain.com")) {
            System.out.println("Hi boss!");
            return true;
        }
        System.out.println("Hello, User!");

        return checkNext(email, password);
    }
}
