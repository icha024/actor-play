package com.clianz.actor.actorsystem;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedTransferQueue;

@Slf4j
public class BlockingTransferQueue<T> extends LinkedTransferQueue<T> implements BlockingQueue<T> {

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
