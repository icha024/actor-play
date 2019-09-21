package com.clianz.actor;

import com.clianz.actor.actorsystem.Actor;
import com.clianz.actor.actorsystem.Event;
import com.clianz.actor.actorsystem.EventsHub;
import lombok.extern.slf4j.Slf4j;

import java.util.stream.IntStream;

@Slf4j
public class Application {
    public static void main(String[] args) {
        log.info("Starting...");

        EventsHub eventsHub = new EventsHub();
//        eventsHub.registerActor(new HttpServerActor());
//        eventsHub.registerActor(new ListenerActor());
////        eventsHub.registerActor(new GitActor());
//        eventsHub.registerActor(event -> log.debug("Received {}", event));

        eventsHub.registerActor(event -> {
            if (event.getMessage()
                     .equals("1000000")) {
                log.info("Done processing");
            } else if (event.getMessage()
                            .equals("1")) {
                log.info("Start processing");
            }
        });

        new Thread(() -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            IntStream.range(0, 1000001)
                     .forEach(num -> eventsHub.publish(new Event("", String.valueOf(num))));
        }).start();

        eventsHub.start();
    }
}
