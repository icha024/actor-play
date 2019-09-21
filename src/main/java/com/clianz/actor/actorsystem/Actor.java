package com.clianz.actor.actorsystem;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

@Slf4j
public abstract class Actor {

    @Getter(AccessLevel.PROTECTED)
    private final String id;
    @Getter(AccessLevel.PACKAGE)
    private final BlockingQueue<Event> inboundQueue;

    @Setter
    private Consumer<Event> pubHandler;

    public Actor() {
        this.inboundQueue = new LinkedBlockingQueue<>(128);
        this.id = String.format("%s-%s", this.getClass()
                                             .getSimpleName(), UUID.randomUUID()
                                                                   .toString()
                                                                   .substring(28));
        init();
    }

    void assignEvent(Event event) {
        if (!this.id.equals(event.getSender())) {
            consumeEvent(event);
        }
    }

    protected void init() {
    }

    protected void publishEvent(Event event) {
        event.setSender(this.id);
        pubHandler.accept(event);
    }

    protected abstract void consumeEvent(Event event);
}