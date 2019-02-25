import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class JavaDemoClient {

    public static void main(String[] args) {
        HttpClient client = HttpClient.newHttpClient();

        List<CompletableFuture<Void>> futures = IntStream.range(0, 10).boxed().map(__ -> {
            var start = System.currentTimeMillis();
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/pump"))
                .build();
            return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
//                .thenApply(HttpResponse::version)
                .thenAccept(version -> System.out.println("Completed in " + (System.currentTimeMillis() - start) + "ms"));
        }).collect(Collectors.toList());

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
//        testCovaranice();
//        testContraVariance();
    }

    public static void testCovaranice() {
        List<? extends Number> numbers = new ArrayList<>(List.of(1.2d, 1.3d));

        for (Number number : numbers) {
            System.out.println("Number: " + number);
        }
    }

    public static void testContraVariance() {
        List<? super Number> numbers = new ArrayList<>();
        numbers.addAll(List.of(1.2d, 1.3d));


//        for (Number number : numbers) {
//
//        }

    }
}
