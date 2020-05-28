import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

// java http client does not support 'force' with prio knowledge, so it takes one request to uppgrade
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
    }

}
