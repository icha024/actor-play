package com.clianz.kube.actors;

import com.clianz.kube.actorsystem.BaseActor;
import com.clianz.kube.actorsystem.Event;
import com.clianz.kube.httpserver.EndpointHandler;
import com.clianz.kube.httpserver.HttpServer;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;

@Slf4j
public class HttpServerActor extends BaseActor {

    @Override
    protected void init() {
        super.init();
        EndpointHandler statusEndpoint = new EndpointHandler("/", exchange -> exchange.getResponseSender()
                                                                                      .send("up"));
        HttpServer server = new HttpServer(Collections.singletonList(statusEndpoint));
        server.start();
    }

    @Override
    protected void consumeEvent(Event msg) {
        log.info("Actor2 Received: {}", msg);
    }
}
