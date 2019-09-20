package com.clianz.kube.httpserver;

import io.undertow.server.HttpServerExchange;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.function.Consumer;

@Data
@AllArgsConstructor
public class EndpointHandler {
    private String endpoint;
    private Consumer<HttpServerExchange> action;
}
