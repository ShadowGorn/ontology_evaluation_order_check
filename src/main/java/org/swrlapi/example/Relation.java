package org.swrlapi.example;

public class Relation {
    Integer from;
    Integer to;
    String type;

    public Relation(Integer from, Integer to, String type) {
        this.from = from;
        this.to = to;
        this.type = type;
    }

    public Integer getFrom() {
        return from;
    }

    public Integer getTo() {
        return to;
    }

    public String getType() {
        return type;
    }
}
