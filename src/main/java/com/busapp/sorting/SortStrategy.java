package com.busapp.sorting;

import com.busapp.model.Bus;

import java.util.List;

public interface SortStrategy {
    void sort(List<Bus> buses);

    default String getName() {
        return this.getClass().getSimpleName();
    }
}
