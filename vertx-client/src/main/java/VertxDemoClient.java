import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.ext.web.codec.BodyCodec;

import java.util.stream.IntStream;

import static java.lang.System.currentTimeMillis;

public class VertxDemoClient {
    public static void main(String[] args) {
        var vertx = Vertx.vertx();

        var options = new WebClientOptions()
            .setConnectTimeout(10_000)
            .setMaxPoolSize(100)
            .setMaxWaitQueueSize(100);
//            .setIdleTimeoutUnit(TimeUnit.SECONDS)
//            .setIdleTimeout(60)

//            .setUseAlpn(true)
//            .setProtocolVersion(HttpVersion.HTTP_2);

        var webClient = WebClient.create(vertx, options);

        IntStream.range(0, 10).forEach(__ -> {
            var start = currentTimeMillis();
            webClient.getAbs("http://localhost:8080/pump")
                .as(BodyCodec.none())
                .send(asyncResult -> {
                    if (asyncResult.succeeded()) {
                        System.out.println("Response status: " + asyncResult.result().statusCode() + " in " + (currentTimeMillis() -start) + "ms");
                    } else {
                        System.out.println("Opps, " + asyncResult.cause());
                    }
                });
        });

        webClient.close();
    }
}
