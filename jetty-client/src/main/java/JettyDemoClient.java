import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.api.Response;
import org.eclipse.jetty.client.api.Result;
import org.eclipse.jetty.client.util.BufferingResponseListener;
import org.eclipse.jetty.http2.client.HTTP2Client;
import org.eclipse.jetty.http2.client.http.HttpClientTransportOverHTTP2;
import org.eclipse.jetty.util.component.LifeCycle;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.stream.IntStream;

public class JettyDemoClient {
    public static void main(String[] args) throws Exception {
        HttpClient httpClient = new HttpClient(new HttpClientTransportOverHTTP2(new HTTP2Client()), null);
//        var httpClient = new HTTP2Client();
//        httpClient.setMaxRequestsQueuedPerDestination(10_000);
        httpClient.setMaxConnectionsPerDestination(100);
        httpClient.start();

        IntStream.range(0, 10_000).forEach(index -> {
//            System.out.println("Making request #" + index);
            var start = System.currentTimeMillis();
            Request request = httpClient.newRequest("http://localhost:8080/hello");
//            request.onResponseContent((response, content) -> {
//                CharBuffer buffer = StandardCharsets.UTF_8.decode(content);
//                String body = new String(buffer.array());
//                System.out.println("Got response #" + index + "status:" + response.getStatus() + ", body: " + body + ", protocol: " + response.getVersion() + ", in " + (System.currentTimeMillis() - start) + " ms");
//
//            });
            try {
                request.send(new Response.Listener.Adapter() {
                    @Override
                    public void onContent(Response response, ByteBuffer buffer) {
                        var body = StandardCharsets.UTF_8.decode(buffer);

                        System.out.println("Got response #" + index + "  status:" + response.getStatus() + ", body: " + body + ", protocol: " + response.getVersion() + ", in " + (System.currentTimeMillis() - start) + " ms");
                    }

                    @Override
                    public void onFailure(Response response, Throwable failure) {
                        System.out.println("Opps error #" + index + " status: " + response.getStatus() + ", cause: " + failure.getMessage());
                    }
                });
            } catch (Exception e) {
                System.out.println("Opps #" + index + " cause: " + e.getMessage());
            }
        });
//        closeSilently(httpClient);
    }

    private static void closeSilently(LifeCycle closeable) {
        try {
            closeable.stop();
        } catch (Exception ignored) {
        }
    }
}
