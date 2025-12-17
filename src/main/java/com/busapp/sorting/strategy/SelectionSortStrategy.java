package com.busapp.sorting.strategy;

import com.busapp.model.Bus;
import com.busapp.sorting.SortStrategy;

import java.util.Comparator;
import java.util.List;

public class SelectionSortStrategy implements SortStrategy {

    private final Comparator<Bus> comparator;

    public SelectionSortStrategy(Comparator<Bus> comparator) {
        this.comparator = comparator;
    }

    @Override
    public void sort(List<Bus> data) {
        int n = data.size();

        for (int i = 0; i < n - 1; i++) {
            int minIndex = i;

            for (int j = i + 1; j < n; j++) {
                if (comparator.compare(data.get(j), data.get(minIndex)) < 0) {
                    minIndex = j;
                }
            }

            Bus temp = data.get(minIndex);
            data.set(minIndex, data.get(i));
            data.set(i, temp);
        }
    }
}