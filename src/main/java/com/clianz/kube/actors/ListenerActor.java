package com.clianz.kube.actors;

import com.clianz.kube.actorsystem.BaseActor;
import com.clianz.kube.actorsystem.Event;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ListenerActor extends BaseActor {

    @Override
    protected void consumeEvent(Event event) {
        log.info("{} received {}", getId(), event);
    }
}
