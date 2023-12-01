package com.serverless.dto;

public class Game {
    private String name;

    public Game(String name) {
        this();
        this.name = name;
    }

    public Game() {
        super();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
