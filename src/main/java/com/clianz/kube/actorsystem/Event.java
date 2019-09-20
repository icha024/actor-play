package com.clianz.kube.actorsystem;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Data
@RequiredArgsConstructor
public class Event implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String context;
    private final Object message;
    @Setter
    private String sender;
}
