import org.eclipse.jetty.http2.server.HTTP2CServerConnectionFactory;
import org.eclipse.jetty.server.AsyncNCSARequestLog;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.HandlerWrapper;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


// jetty http2 upgrade only works with GET
// https://stackoverflow.com/questions/55391521/curl-post-request-is-not-working-with-option-http2-but-it-works-fine-when-i-u
public class JettyServerDemo {
    public static void main(String[] args) throws Exception {
        try (JettyStarter starter = new JettyStarter();) {
            starter.start();
            System.out.println("Press any key to stop Jetty.");
            System.in.read();
        }
    }

    public static class HelloWorldServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
            }
            resp.setContentType("text/html;charset=UTF-8");
            resp.getWriter().append("Hello World protocol ").append(req.getProtocol());
        }

        @Override
        protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
            resp.setContentType("text/html;charset=UTF-8");
//            resp.getWriter().append("Hello World protocol ").append(req.getProtocol());
            resp.setStatus(200);
        }
    }

    private static class JettyStarter implements AutoCloseable {

        private static final int PORT = 8080;
        private final Server server;

        JettyStarter() {
            Server server = createServer();

            HandlerWrapper servletHandler = createServletHandlerWithServlet();
            HandlerWrapper gzipHandler = createGzipHandler();
            gzipHandler.setHandler(servletHandler);
            server.setHandler(gzipHandler);

            this.server = server;
        }

        private Server createServer() {
            HttpConfiguration config = createHttpConfiguration();
            // HTTP/1.1 support.
            HttpConnectionFactory http1 = new HttpConnectionFactory(config);

            // HTTP/2 cleartext support.
            HTTP2CServerConnectionFactory http2c = new HTTP2CServerConnectionFactory(config);
            http2c.setMaxConcurrentStreams(1024);
//            http2c.set

            Server server = new Server();
            server.setRequestLog(new AsyncNCSARequestLog());

            ServerConnector connector = new ServerConnector(server, http1, http2c);
            connector.setPort(PORT);
            server.addConnector(connector);

            return server;
        }

        private GzipHandler createGzipHandler() {
            GzipHandler gzipHandler = new GzipHandler();
            gzipHandler.setIncludedPaths("/*");
            gzipHandler.setMinGzipSize(1000);
            gzipHandler.setIncludedMimeTypes("text/plain", "text/html");
            return gzipHandler;
        }

        private ServletContextHandler createServletHandlerWithServlet() {
            ServletContextHandler context = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);

            context.addServlet(HelloWorldServlet.class, "/*");
            context.setContextPath("/");

            return context;
        }

        private static HttpConfiguration createHttpConfiguration() {
            HttpConfiguration config = new HttpConfiguration();
            config.setSendXPoweredBy(false);
            config.setSendServerVersion(false);
            return config;
        }

        void start() throws Exception {
            server.start();
        }

        @Override
        public void close() throws Exception {
            server.stop();
        }

    }
}
