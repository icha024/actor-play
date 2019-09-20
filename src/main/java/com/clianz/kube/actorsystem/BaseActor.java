package com.clianz.kube.actorsystem;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
public abstract class BaseActor implements Runnable {

    @Getter
    private final String id;
    @Getter
    private final BlockingQueue<Event> inboundQueue;
    private final BlockingQueue<Event> outboundQueue = EventsHub.eventsHub;

    public BaseActor() {
        this.inboundQueue = new LinkedBlockingQueue<>(128);
        this.id = this.getClass().getName() + "-" + UUID.randomUUID().toString().substring(16);
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