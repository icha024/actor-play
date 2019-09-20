package com.clianz.kube.actorsystem;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
public class EventsHub implements Runnable {
    private List<BaseActor> actors = new ArrayList<>();

    static BlockingQueue<Event> eventsHub = new LinkedBlockingQueue<>(1024);

    public void registerActor(BaseActor ... newActors) {
        this.actors.addAll(Arrays.asList(newActors));
    }

    public void start() {
        Thread thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        try {
            while (true) {
                Event event = eventsHub.take();
                actors.forEach(eachActor -> {
                    boolean sentSuccess = eachActor.getInboundQueue()
                                                   .offer(event);
                    if (!sentSuccess) {
                        log.warn("Error sending event to '{}'. Event: {}", eachActor.getId(), event);
                    }
                });
            }
        } catch (
                InterruptedException ex) {
            log.error("Error in Events Hub: ", ex);
        }
    }
}
