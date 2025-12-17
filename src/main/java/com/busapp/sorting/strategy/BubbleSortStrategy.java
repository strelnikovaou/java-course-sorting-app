package com.busapp.sorting.strategy;

import com.busapp.model.Bus;
import com.busapp.sorting.SortStrategy;

import java.util.Comparator;
import java.util.List;

public class BubbleSortStrategy implements SortStrategy {

    private final Comparator<Bus> comparator;

    public BubbleSortStrategy(Comparator<Bus> comparator) {
        this.comparator = comparator;
    }

    @Override
    public void sort(List<Bus> data) {
        int n = data.size();

        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (comparator.compare(data.get(j), data.get(j + 1)) > 0) {
                    Bus temp = data.get(j);
                    data.set(j, data.get(j + 1));
                    data.set(j + 1, temp);
                }
            }
        }
    }
}