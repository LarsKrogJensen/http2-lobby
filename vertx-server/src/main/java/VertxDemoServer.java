import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.http.Http2Settings;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Router;

import java.io.IOException;

public class VertxDemoServer {
    public static void main(String[] args) throws IOException {
        var vertx = Vertx.vertx();

        vertx.deployVerticle(WebServer::new, new DeploymentOptions().setInstances(16));
        System.in.read();
    }

    private static class WebServer extends AbstractVerticle {
        @Override
        public void start() {
            var router = Router.router(vertx);
            router.get("/hello").handler(routingContext -> {
                System.out.println("Handling request on protocol: " + routingContext.request().version());
                vertx.setTimer(2000, __ -> {
                    routingContext.response().end("Hello World on protocol " + routingContext.request().version());
                });
            });
            router.post("/hello").handler(routingContext -> {
                System.out.println("Handling request on protocol: " + routingContext.request().version());
                routingContext.response().end("Hello World on protocol " + routingContext.request().version());

            });

            HttpServerOptions options = new HttpServerOptions().setUseAlpn(true).setInitialSettings(new Http2Settings().setMaxConcurrentStreams(10_000));
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
}
