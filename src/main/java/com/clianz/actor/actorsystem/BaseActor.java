package com.clianz.actor.actorsystem;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
public abstract class BaseActor implements Runnable {

    @Getter(AccessLevel.PROTECTED)
    private final String id;
    @Getter(AccessLevel.PACKAGE)
    private final BlockingQueue<Event> inboundQueue;
    private final BlockingQueue<Event> outboundQueue = EventsHub.eventsHub;

    public BaseActor() {
        this.inboundQueue = new LinkedBlockingQueue<>(128);
        this.id = String.format("%s-%s", this.getClass()
                                             .getSimpleName(), UUID.randomUUID()
                                                                   .toString()
                                                                   .substring(28));
        Thread thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        try {
            init();
            if (inboundQueue != null) {
                while (true) {
                    Event event = inboundQueue.take();
                    if (!this.id.equals(event.getSender())) {
                        consumeEvent(event);
                    }
                }
            }
        } catch (
                InterruptedException ex) {
            log.error("Error in Actor: ", ex);
        }
    }

    protected void init() {
    }

    protected void publishEvent(Event event) {
        event.setSender(this.id);
        boolean sentSuccessful = outboundQueue.offer(event);
        if (!sentSuccessful) {
            log.warn("Error sending to Event Hub. Actor '{}', Event: {}", getId(), event);
        }
    }

    protected abstract void consumeEvent(Event event);
}