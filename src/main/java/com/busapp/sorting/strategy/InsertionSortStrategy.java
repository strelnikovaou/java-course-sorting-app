package com.busapp.sorting.strategy;

import com.busapp.model.Bus;
import com.busapp.sorting.SortStrategy;

import java.util.Comparator;
import java.util.List;

public class InsertionSortStrategy implements SortStrategy {

    private final Comparator<Bus> comparator;

    public InsertionSortStrategy(Comparator<Bus> comparator) {
        this.comparator = comparator;
    }

    @Override
    public void sort(List<Bus> data) {
        for (int i = 1; i < data.size(); i++) {
            Bus key = data.get(i);
            int j = i - 1;

            while (j >= 0 && comparator.compare(data.get(j), key) > 0) {
                data.set(j + 1, data.get(j));
                j--;
            }
            data.set(j + 1, key);
        }
    }
}