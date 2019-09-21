package com.clianz.actor.actorsystem;

@FunctionalInterface
public interface BaseActorFunctionalInterface {

    void consumeEvent(Event event);
}
