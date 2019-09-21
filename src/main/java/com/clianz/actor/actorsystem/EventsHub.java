package com.clianz.actor.actorsystem;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class EventsHub {

    private static final int BUFF_SIZE = 4;
    private static final int THREAD_POOL_SIZE = 1;
    private static final int WAIT_SLEEP_INTERVAL = 100;

    private List<BaseActor> actors = new ArrayList<>();
    private EventHolder[] eventHolderArray = new EventHolder[BUFF_SIZE];

    private AtomicLong pubCounter = new AtomicLong();
    private AtomicLong subCounter = new AtomicLong();

    public EventsHub() {
        for (int i = 0; i < eventHolderArray.length; i++) {
            eventHolderArray[i] = new EventHolder();
        }
    }

    public void registerActor(BaseActor... newActors) {
        this.actors.addAll(Arrays.asList(newActors));
        actors.forEach(eachActor -> eachActor.setPubHandler(this::publish));
    }

    public boolean publish(Event event) {
        if (subCounter.get() % BUFF_SIZE == (pubCounter.get() + 1) % BUFF_SIZE) {
            log.warn("Buffer too small, pub event failed.");
            return false;
        }
        int pubIdx = (int) pubCounter.getAndIncrement() % BUFF_SIZE;
        log.debug("Pub to Idx: {}", pubIdx);
        eventHolderArray[pubIdx].setEvent(event);
        return true;
    }

//    public void getEvent() {
//        long currentSubIdx = subCounter.get();
//        if (currentSubIdx == pubCounter.get()) {
//            return Optional.empty();
//        }
//        while (true) {
//            int subIdx = (int) currentSubIdx % BUFF_SIZE;;
//            EventHolder currentEventHolder = eventHolderArray[subIdx];
//            actors.forEach(eachActor -> {
//                log.info("SubIdx {} -> {}", subIdx, eachActor.getId());
//                executor.submit(() -> eachActor.consumeEvent(currentEventHolder.getEvent()));
//            });
//            currentSubIdx = subCounter.incrementAndGet();
//        }
//    }

    public void start() {

        ThreadPoolExecutor executor = new ThreadPoolExecutor(THREAD_POOL_SIZE, THREAD_POOL_SIZE,
                60, TimeUnit.SECONDS, new BlockingTransferQueue<>());
        executor.prestartAllCoreThreads();
//        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(THREAD_POOL_SIZE);

        long currentSubIdx = subCounter.get();
        while (true) {
            if (currentSubIdx == pubCounter.get()) {
                try {
                    Thread.sleep(WAIT_SLEEP_INTERVAL);
                } catch (InterruptedException e) {
                    // Do nothing
                    log.warn("Sleep interrupted.");
                }
                continue;
            }
            int subIdx = (int) currentSubIdx % BUFF_SIZE;

            EventHolder currentEventHolder = eventHolderArray[subIdx];
            actors.forEach(eachActor -> {
//                boolean success = false;
//                while (!success) {
                log.debug("Distribute subIdx {} -> {}", subIdx, eachActor.getId());
                executor.submit(() -> eachActor.consumeEvent(currentEventHolder.getEvent()));
//                    success = submitTask(executor, currentEventHolder, eachActor);
//                    if (!success) {
//                        try {
//                            Thread.sleep(WAIT_SLEEP_INTERVAL);
//                        } catch (InterruptedException e) {
//                            // Do nothing
//                            e.printStackTrace();
//                        }
//                    }
//                }
            });
            currentSubIdx = subCounter.incrementAndGet();
        }
    }

//    private boolean submitTask(ThreadPoolExecutor executor, EventHolder currentEventHolder, BaseActor eachActor) {
//        try {
//            executor.submit(() -> eachActor.consumeEvent(currentEventHolder.getEvent()));
//            return true;
//        } catch (RejectedExecutionException rex) {
//            return false;
//        }
//    }
}
