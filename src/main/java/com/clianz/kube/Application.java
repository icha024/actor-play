package com.clianz.kube;

import com.clianz.kube.actors.HttpServerActor;
import com.clianz.kube.actors.ListenerActor;
import com.clianz.kube.actorsystem.EventsHub;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Application {
    public static void main(String[] args) throws InterruptedException {
        log.info("Starting...");

        EventsHub eventsHub = new EventsHub();
        eventsHub.registerActor(new HttpServerActor());
        eventsHub.registerActor(new ListenerActor());
        eventsHub.start();

        log.info("Started.");
        while (true) {
            Thread.sleep(10_000);
        }
    }
}
