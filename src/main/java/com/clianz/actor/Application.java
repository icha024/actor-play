package com.clianz.actor;

import com.clianz.actor.actors.GitActor;
import com.clianz.actor.actors.HttpServerActor;
import com.clianz.actor.actors.ListenerActor;
import com.clianz.actor.actorsystem.EventsHub;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Application {
    public static void main(String[] args) {
        log.info("Starting...");

        EventsHub eventsHub = new EventsHub();
        eventsHub.registerActor(new HttpServerActor());
        eventsHub.registerActor(new ListenerActor());
//        eventsHub.registerActor(new GitActor());
        eventsHub.start();
    }
}
