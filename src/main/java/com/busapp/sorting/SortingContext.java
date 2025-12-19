package com.busapp.sorting;

import com.busapp.model.Bus;
import java.util.List;

public class SortingContext {

    private SortStrategy strategy;

    public void setStrategy(SortStrategy strategy) {
        this.strategy = strategy;
    }

    public void sort(List<Bus> data) {
        if (strategy == null) {
            throw new IllegalStateException("SortStrategy is not set");
        }
        strategy.sort(data);
    }
}