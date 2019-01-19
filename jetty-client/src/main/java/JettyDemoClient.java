import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.http2.client.HTTP2Client;
import org.eclipse.jetty.http2.client.http.HttpClientTransportOverHTTP2;
import org.eclipse.jetty.util.component.LifeCycle;

import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;

public class JettyDemoClient {
    public static void main(String[] args) throws Exception {
        HttpClient httpClient = new HttpClient(new HttpClientTransportOverHTTP2(new HTTP2Client()), null);
        httpClient.start();

        Request request = httpClient.newRequest("http://localhost:8080/hello");
        request.onResponseContent((response, content) -> {
            CharBuffer buffer = StandardCharsets.UTF_8.decode(content);
            String body = new String(buffer.array());
            System.out.println("Got response status: " + response.getStatus() + ", body: " + body + ", protocol: " + response.getVersion());
            closeSilently(httpClient);
        });
        request.send();
    }

    private static void closeSilently(LifeCycle closeable) {
        try {
            closeable.stop();
        } catch (Exception ignored) {
        }
    }
}
