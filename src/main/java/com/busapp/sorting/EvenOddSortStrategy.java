package com.busapp.sorting;

import com.busapp.model.Bus;

import java.util.*;
import java.util.function.Function;

public class EvenOddSortStrategy implements SortStrategy {
    private final SortStrategy baseStrategy;
    private final Function<Bus, Integer> fieldExtractor;

    public EvenOddSortStrategy(SortStrategy baseStrategy, Function<Bus, Integer> fieldExtractor) {
        this.baseStrategy = baseStrategy;
        this.fieldExtractor = fieldExtractor;
    }

    @Override
    public void sort(List<Bus> buses) {
        if (buses == null || buses.size() <= 1) return;

        Map<Integer, Bus> oddElements = new LinkedHashMap<>();
        List<Bus> evenElements = new ArrayList<>();
        List<Integer> evenIndices = new ArrayList<>();

        for (int i = 0; i < buses.size(); i++) {
            Bus bus = buses.get(i);
            int value = fieldExtractor.apply(bus);
            if (value % 2 == 0) {
                evenElements.add(bus);
                evenIndices.add(i);
            } else {
                oddElements.put(i, bus);
            }
        }

        baseStrategy.sort(evenElements);

        for (int i = 0; i < evenElements.size(); i++) {
            buses.set(evenIndices.get(i), evenElements.get(i));
        }

        oddElements.forEach(buses::set);
    }
}