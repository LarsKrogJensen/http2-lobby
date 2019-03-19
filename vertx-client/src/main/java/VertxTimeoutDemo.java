import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.http.ConnectionPoolTooBusyException;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.dropwizard.DropwizardMetricsOptions;
import io.vertx.ext.dropwizard.Match;
import io.vertx.ext.dropwizard.MatchType;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.ext.web.codec.BodyCodec;

public class VertxTimeoutDemo {
    public static void main(String[] args) {
        VertxOptions vertxOptions = new VertxOptions()
            .setMetricsOptions(new DropwizardMetricsOptions()
                                   .setJmxEnabled(true)
                                   .addMonitoredHttpServerUri(new Match().setValue(".*").setType(MatchType.REGEX))
                                   .addMonitoredHttpClientEndpoint(new Match().setValue(".*").setType(MatchType.REGEX)));
        var vertx = Vertx.vertx(vertxOptions);

        startSlowServer(vertx);

        var options = new WebClientOptions()
            .setConnectTimeout(10_000)
            .setMaxPoolSize(10)
            .setMaxWaitQueueSize(100);
//            .setIdleTimeoutUnit(TimeUnit.SECONDS)
//            .setIdleTimeout(60)

//            .setUseAlpn(true)
//            .setProtocolVersion(HttpVersion.HTTP_2);

        var webClient = WebClient.create(vertx, options);

        vertx.setPeriodic(1000, __ -> {
            System.out.println("sending request");
            webClient.getAbs("http://localhost:8080/hello")
                .as(BodyCodec.none())
                .send(asyncResult -> {
                    if (asyncResult.succeeded()) {
                        System.out.println("Response status: " + asyncResult.result().statusCode());
                    } else {
                        System.out.println("Opps, " + asyncResult.cause());
                        if (asyncResult.cause() instanceof ConnectionPoolTooBusyException) {
//                            webClient.close();
                        }
                    }
                });
        });

    }

    public static void startSlowServer(Vertx vertx) {
        var router = Router.router(vertx);
        router.get("/hello").handler(routingContext -> {
            vertx.setTimer(60_000, __ -> {
                routingContext.response().end("Hello World on protocol " + routingContext.request().version());
            });
            System.out.println("Handling request on protocol: " + routingContext.request().version());
        });

        HttpServerOptions options = new HttpServerOptions().setUseAlpn(false);
        vertx.createHttpServer(options)
            .requestHandler(router)
            .listen(8080, asyncResult -> {
                if (asyncResult.succeeded()) {
                    System.out.println("Vertx http server started on port " + asyncResult.result().actualPort());
                } else {
                    System.out.println("Opps failed to start http server" + asyncResult.cause());
                }
            });
    }
}
