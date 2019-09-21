package com.clianz.actor.actorsystem;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class EventsHub {

    private static final int BUFF_SIZE = 4;
    public static final int THREAD_POOL_SIZE = 1;

    private List<BaseActor> actors = new ArrayList<>();
    private EventHolder[] eventHolderArray = new EventHolder[BUFF_SIZE];

    private AtomicLong pubCounter = new AtomicLong();
    private AtomicLong subCounter = new AtomicLong();

    public void registerActor(BaseActor... newActors) {
        this.actors.addAll(Arrays.asList(newActors));
        actors.forEach(eachActor -> eachActor.setPubHandler(this::publish));
    }

    public void publish(Event event) {
        int pubIdx = (int) pubCounter.getAndIncrement() % BUFF_SIZE;
        eventHolderArray[pubIdx].setEvent(event);
    }

    public void start() {
        for (int i = 0; i < eventHolderArray.length; i++) {
            eventHolderArray[i] = new EventHolder();
        }

        ThreadPoolExecutor executor = new ThreadPoolExecutor(THREAD_POOL_SIZE, THREAD_POOL_SIZE,
                60, TimeUnit.SECONDS, new LinkedTransferQueue<>());

        long currentSubIdx = subCounter.get();
        while (true) {
            if (currentSubIdx == pubCounter.get()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continue;
            }
            int subIdx = (int) currentSubIdx % BUFF_SIZE;;
            EventHolder currentEventHolder = eventHolderArray[subIdx];
            actors.forEach(eachActor -> {
                log.info("SubIdx {} -> {}", subIdx, eachActor.getId());
                executor.submit(() -> eachActor.consumeEvent(currentEventHolder.getEvent()));
            });
            currentSubIdx = subCounter.incrementAndGet();
        }
    }
}
