import java.security.MessageDigest;
import java.util.Base64;
import java.util.Scanner;

class ChainOfResponsibilityDemo {

    /**
     * Accepts a request and returns new request with data wrapped in the tag <transaction>...</transaction>
     */
    static RequestHandler wrapInTransactionTag = req ->
            new Request(String.format("<transaction>%s</transaction>", req.getData()));

    /**
     * Accepts a request and returns a new request with calculated digest inside the tag <digest>...</digest>
     */
    static RequestHandler createDigest = req -> {
        String digest = "";
        try {
            final MessageDigest md5 = MessageDigest.getInstance("MD5");
            final byte[] digestBytes = md5.digest(req.getData().getBytes("UTF-8"));
            digest = new String(Base64.getEncoder().encode(digestBytes));
        } catch (Exception ignored) {
            System.out.println("An error occurred");
        }
        return new Request(req.getData() + String.format("<digest>%s</digest>", digest));
    };

    /**
     * Accepts a request and returns a new request with data wrapped in the tag <request>...</request>
     */
    static RequestHandler wrapInRequestTag = req ->
            new Request(String.format("<request>%s</request>", req.getData()));

    /**
     * It should represents a chain of responsibility combined from another handlers.
     * The format: commonRequestHandler = handler1.setSuccessor(handler2.setSuccessor(...))
     * The combining method setSuccessor may have another name
     */
    static RequestHandler commonRequestHandler = req ->
            wrapInTransactionTag.andThan(createDigest).andThan(wrapInRequestTag).handle(req);
//            new Request(wrapInRequestTag.handle(createDigest.handle(wrapInTransactionTag.handle(req))).getData());

    /**
     * It represents a handler and has two methods: one for handling requests and other for combining handlers
     */
    @FunctionalInterface
    interface RequestHandler {
        Request handle(Request val);

        default RequestHandler andThan(RequestHandler handler) {
            return request -> handler.handle(this.handle(request));
        }
    }

    /**
     * Immutable class for representing requests.
     * If you need to change the request data then create new request.
     */
    static class Request {
        private final String data;

        public Request(String requestData) {
            this.data = requestData;
        }

        public String getData() {
            return data;
        }
    }

    // Don't change the code below
    public static void main(String[] args) throws Exception {

        final Scanner scanner = new Scanner(System.in);

        final String requestData = scanner.nextLine();

        final Request notCompletedRequest = new Request(requestData);

        System.out.println(commonRequestHandler.handle(notCompletedRequest).getData());
    }
}

// Input example: <type>payment</type><sum>100000</sum><order_id>e94dc619-6172-4ffe-aae8-63112c551570</order_id><desc>We'd like to buy an elephant</desc>