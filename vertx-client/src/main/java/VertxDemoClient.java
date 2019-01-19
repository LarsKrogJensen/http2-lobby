import io.vertx.core.Vertx;
import io.vertx.core.http.HttpVersion;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;

public class VertxDemoClient {
    public static void main(String[] args) {
        var vertx = Vertx.vertx();

        var options = new WebClientOptions()
            .setUseAlpn(true)
            .setProtocolVersion(HttpVersion.HTTP_2);

        var webClient = WebClient.create(vertx, options);

        webClient.getAbs("http://localhost:8080/hello").send(asyncResult -> {
            if (asyncResult.succeeded()) {
                System.out.println("Response status: " + asyncResult.result().statusCode() +
                                       ", http version: " + asyncResult.result().version() +
                                       ", body: " + asyncResult.result().bodyAsString());
            } else {
                System.out.println("Opps, " + asyncResult.cause());
            }

            vertx.close();
        });
    }
}
