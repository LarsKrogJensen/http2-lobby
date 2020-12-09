import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

// java http client does not support 'force' with prio knowledge, so it takes one request to uppgrade
public class JavaDemoClient {


    public static void main(String[] args) throws IOException {
        HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();

        AtomicInteger completed = new AtomicInteger();
        IntStream.range(0, 10_000).boxed().forEach(index -> {
            var start = System.currentTimeMillis();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/hello"))
                    .build();
            client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .whenComplete((version, exception) -> {
                        var x = completed.incrementAndGet();
                        if (exception != null) {
                            System.out.println("Opps#" + index + " of " + x + " ex "  + exception.getMessage());
                        } else {
                            System.out.println("Completed #" + index + " of " + x + " in " + (System.currentTimeMillis() - start) + "ms");
                        }
                    });
        });



        System.in.read();
    }

}
