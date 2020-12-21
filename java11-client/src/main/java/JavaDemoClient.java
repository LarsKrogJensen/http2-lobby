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


    public static void main(String[] args) throws Exception {
//        System.setProperty("jdk.httpclient.keepalive.timeout", "45");
        HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
//                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();

        HttpRequest get = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/hello"))
//                .version(HttpClient.Version.HTTP_1_1)
                .build();
        var response = client.send(get, HttpResponse.BodyHandlers.ofString());
        System.out.println("Response  - protocol: " + response.version() + ", body: " + response.body());



        while (true) {
            try {
                var request = HttpRequest.newBuilder()
                        .POST(HttpRequest.BodyPublishers.ofString("Say hi"))
                        .uri(URI.create("http://localhost:8080/hello"))
//                        .version(HttpClient.Version.HTTP_1_1)
                        .build();
                var resp = client.send(request, HttpResponse.BodyHandlers.ofString());
                System.out.println("Response  - status: " + resp.statusCode() + ", protocol: " + resp.version() + ", body: [" + resp.body() + "]");
            } catch (Exception e) {
                System.out.println("Error " + e);
            }
            Thread.sleep(30_000);
        }


//        response = client.send(request, HttpResponse.BodyHandlers.ofString());
//        System.out.println("Response  - protocol: " + response.version() + ", body: " + response.body());
//        AtomicInteger completed = new AtomicInteger();
//        IntStream.range(0, 10_000).boxed().forEach(index -> {
//            var start = System.currentTimeMillis();
//            HttpRequest request = HttpRequest.newBuilder()
//                    .uri(URI.create("http://localhost:8080/hello"))
//                    .build();
//            client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
//                    .whenComplete((version, exception) -> {
//                        var x = completed.incrementAndGet();
//                        if (exception != null) {
//                            System.out.println("Opps#" + index + " of " + x + " ex "  + exception.getMessage());
//                        } else {
//                            System.out.println("Completed #" + index + " of " + x + " in " + (System.currentTimeMillis() - start) + "ms");
//                        }
//                    });
//        });


//        System.in.read();
    }

}
