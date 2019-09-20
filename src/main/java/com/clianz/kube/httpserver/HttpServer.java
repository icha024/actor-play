package com.clianz.kube.httpserver;

import io.undertow.Undertow;
import io.undertow.server.handlers.PathHandler;

import java.util.List;

import static io.undertow.Handlers.path;

public class HttpServer {

    private final Undertow server;

    public HttpServer(List<EndpointHandler> endpointHandlers) {
        PathHandler pathHander = getPathHandler(endpointHandlers);
        Undertow.Builder builder = Undertow.builder()
                .setIoThreads(2)
                .setWorkerThreads(10)
                .addHttpListener(8080, "0.0.0.0")
                .setHandler(pathHander);
        this.server = builder.build();
    }

    private PathHandler getPathHandler(List<EndpointHandler> endpointHandlers) {
        PathHandler pathHander = path();
        endpointHandlers.forEach(eachEndpointHandler -> pathHander.addExactPath(
                eachEndpointHandler.getEndpoint(),
                exchange -> eachEndpointHandler.getAction()
                        .accept(exchange)
        ));
        return pathHander;
    }

    public void start() {
        server.start();
    }
}
