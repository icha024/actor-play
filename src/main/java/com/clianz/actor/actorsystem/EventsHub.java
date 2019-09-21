package com.clianz.actor.actorsystem;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class EventsHub {

    // FIXME: Make this config.
    private static final int BUFF_SIZE = 1024 * 1024;
    private static final int THREAD_POOL_SIZE = 4;
    private static final int WAIT_SLEEP_INTERVAL = 100;

    private List<Actor> actors = new ArrayList<>();
    private EventHolder[] eventHolderArray = new EventHolder[BUFF_SIZE];

    private AtomicLong pubCounter = new AtomicLong();
    private AtomicLong subCounter = new AtomicLong();

    public EventsHub() {
        for (int i = 0; i < eventHolderArray.length; i++) {
            eventHolderArray[i] = new EventHolder();
        }
    }

    public void registerActor(Actor... newActors) {
        this.actors.addAll(Arrays.asList(newActors));
        actors.forEach(eachActor -> eachActor.setPubHandler(this::publish));
    }

    public void registerActor(BaseActorFunctionalInterface baseActorFunctionalInterface) {
        this.registerActor(new Actor() {
            @Override
            protected void consumeEvent(Event event) {
                baseActorFunctionalInterface.consumeEvent(event);
            }
        });
    }

    @SuppressWarnings("WeakerAccess")
    public boolean publish(Event event) {
        if (subCounter.get() % BUFF_SIZE == (pubCounter.get() + 1) % BUFF_SIZE) {
            log.warn("Buffer too small, pub event failed.");
            return false;
        }
        int pubIdx = (int) pubCounter.getAndIncrement() % BUFF_SIZE;
//        log.debug("Pub to Idx: {}", pubIdx);
        eventHolderArray[pubIdx].setEvent(event);
        return true;
    }

    public void start() {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(THREAD_POOL_SIZE, THREAD_POOL_SIZE,
                60, TimeUnit.SECONDS, new BlockingTransferQueue<>());
        executor.prestartAllCoreThreads();

        long currentSubIdx = subCounter.get();
        while (true) {
            if (currentSubIdx == pubCounter.get()) {
                try {
                    Thread.sleep(WAIT_SLEEP_INTERVAL);
                } catch (InterruptedException e) {
                    log.warn("Sleep interrupted, {}", e.getMessage());
                }
                continue;
            }
            int subIdx = (int) currentSubIdx % BUFF_SIZE;

            EventHolder currentEventHolder = eventHolderArray[subIdx];
            actors.forEach(eachActor -> {
//                log.debug("Distribute subIdx {} -> {}", subIdx, eachActor.getId());
                executor.submit(() -> eachActor.assignEvent(currentEventHolder.getEvent()));
            });
            currentSubIdx = subCounter.incrementAndGet();
        }
    }

    @Slf4j
    private static class BlockingTransferQueue<T> extends LinkedTransferQueue<T> implements BlockingQueue<T> {
        @Override
        public boolean offer(T element) {
            try {
                super.transfer(element);
                return true;
            } catch (InterruptedException ex) {
                log.warn("Thread interrupted", ex);
                return super.offer(element);
            }
        }
    }

    @Data
    private static class EventHolder {
        private Event event;
    }

    @FunctionalInterface
    public interface BaseActorFunctionalInterface {
        void consumeEvent(Event event);
    }
}
