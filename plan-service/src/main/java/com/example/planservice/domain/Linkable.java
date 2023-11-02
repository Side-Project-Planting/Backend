package com.example.planservice.domain;

public interface Linkable<T> {
    T getNext();

    Long getId();
}
