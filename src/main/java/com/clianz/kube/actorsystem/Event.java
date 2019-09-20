package com.clianz.kube.actorsystem;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Data
@RequiredArgsConstructor
public class Event {
    private final String context;
    private final Object message;
    @Setter
    private String sender;
}
