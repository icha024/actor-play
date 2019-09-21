package com.clianz.actor.actors;

import com.clianz.actor.actorsystem.BaseActor;
import com.clianz.actor.actorsystem.Event;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ListenerActor extends BaseActor {

    @Override
    protected void consumeEvent(Event event) {
        log.info("received {}", event);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
