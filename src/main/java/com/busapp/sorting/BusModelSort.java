package com.busapp.sorting;

import com.busapp.model.Bus;

import java.util.Comparator;
import java.util.List;

public class BusModelSort implements SortStrategy {
    /**
     * Сортирует автобусы по модели в алфавитном порядке (без учета регистра).
     *
     * @param buses список автобусов для сортировки
     */
    @Override
    public void sort(List<Bus> buses) {
        buses.sort(Comparator.comparing(Bus::getModel, String.CASE_INSENSITIVE_ORDER));
    }
}