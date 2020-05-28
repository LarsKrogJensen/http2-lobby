import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Router;

public class VertxDemoServer {
    public static void main(String[] args) {
        var vertx = Vertx.vertx();

        var router = Router.router(vertx);
        router.get("/hello").handler(routingContext -> {
            System.out.println("Handling request on protocol: " + routingContext.request().version());
            routingContext.response().end("Hello World on protocol " + routingContext.request().version());
        });

        HttpServerOptions options = new HttpServerOptions().setUseAlpn(true);
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
