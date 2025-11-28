package io.github.skydynamic.quickbackupmulti.service;

import io.github.skydynamic.quickbackupmulti.WebUI;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

import java.net.URI;

public class HttpApiService {
    private String host = "localhost";
    private int port = 53222;

    private HttpServer httpServer;

    public HttpApiService(
        String host,
        int port
    ) {
        this.host = host;
        this.port = port;
    }

    public HttpApiService() {
    }

    public void startService() {
        URI baseUri = URI.create("http://" + host + ":" + port + "/");
        ResourceConfig rc = new ResourceConfig()
            .packages("io.github.skydynamic.quickbackupmulti.api")
            .register(JacksonFeature.class);
        httpServer = GrizzlyHttpServerFactory.createHttpServer(baseUri, rc);

        WebUI.getLOGGER().info("HTTP Service started at http://{}:{}/", host, port);
    }

    public void stopService() {
        if (httpServer != null) {
            httpServer.shutdownNow();
            WebUI.getLOGGER().info("HTTP Service stopped.");
        } else {
            WebUI.getLOGGER().warn("HTTP Service is not running.");
        }
    }
}
