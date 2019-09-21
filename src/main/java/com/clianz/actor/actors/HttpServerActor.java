package com.clianz.actor.actors;

import com.clianz.actor.actorsystem.BaseActor;
import com.clianz.actor.actorsystem.Event;
import com.clianz.actor.httpserver.EndpointHandler;
import com.clianz.actor.httpserver.HttpServer;
import io.undertow.server.HttpServerExchange;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;

@Slf4j
public class HttpServerActor extends BaseActor {

    @Override
    protected void init() {
        super.init();
        EndpointHandler rootEndpoint = new EndpointHandler("/", this::rootEndpointHandler);
        HttpServer server = new HttpServer(Collections.singletonList(rootEndpoint));
        server.start();
    }

    private void rootEndpointHandler(HttpServerExchange exchange) {
        publishEvent(new Event("http.endpoint.root", "HTTP Event Triggered"));
        exchange.getResponseSender()
                .send("up");
    }

    @Override
    protected void consumeEvent(Event msg) {
        log.info("{} received {}", getId(), msg);
    }
}
