package com.clianz.actor.httpserver;

import io.undertow.server.HttpServerExchange;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.function.Consumer;

@Data
@AllArgsConstructor
public class EndpointHandler {

    private final String endpoint;
    private final Consumer<HttpServerExchange> action;
}
