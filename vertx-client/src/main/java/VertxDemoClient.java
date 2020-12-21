import io.vertx.core.Vertx;
import io.vertx.core.http.HttpVersion;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.ext.web.codec.BodyCodec;

import java.util.stream.IntStream;

import static java.lang.System.currentTimeMillis;

public class VertxDemoClient {
    public static void main(String[] args) throws InterruptedException {
        var vertx = Vertx.vertx();

        var options = new WebClientOptions()
                .setConnectTimeout(10_000)
                .setMaxPoolSize(100)
                .setHttp2MaxPoolSize(100)
                .setMaxWaitQueueSize(10_000)
                .setFollowRedirects(true)
                .setProtocolVersion(HttpVersion.HTTP_2);

        var webClient = WebClient.create(vertx, options);

        while (true) {
            var start = currentTimeMillis();
            webClient.postAbs("http://localhost:8080/hello")
                    .as(BodyCodec.string())
                    .sendJson(new JsonObject(), asyncResult -> {
                        if (asyncResult.succeeded()) {
                            System.out.println("Response status: " + asyncResult.result().statusCode() + " in " + (currentTimeMillis() - start) + "ms body: " + asyncResult.result().body());
                        } else {
                            System.out.println("Opps, " + asyncResult.cause());
                        }
                    });

            Thread.sleep(30_000);
        }

//        webClient.close();
    }
}
