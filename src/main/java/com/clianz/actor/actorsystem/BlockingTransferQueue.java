package com.clianz.actor.actorsystem;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedTransferQueue;

public class BlockingTransferQueue<T> extends LinkedTransferQueue<T> implements BlockingQueue<T> {

    @Override
    public boolean offer(T element) {
        try {
            super.transfer(element);
            return true;
        } catch (InterruptedException ex) {
            // Do nothing
            ex.printStackTrace();
            return super.offer(element);
        }
    }
}
