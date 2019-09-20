package com.clianz.kube;

import com.clianz.kube.actors.HttpServerActor;
import com.clianz.kube.actorsystem.EventsHub;

public class Application {
    public static void main(String[] args) throws InterruptedException {

        EventsHub eventsHub = new EventsHub();
        eventsHub.registerActor(new HttpServerActor());
        eventsHub.start();

        while (true) {
            Thread.sleep(10_000);
        }
    }
}
