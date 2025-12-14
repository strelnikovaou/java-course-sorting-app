package com.busapp.sorting;

import com.busapp.model.Bus;

import java.util.Comparator;
import java.util.List;

public class CompositeBusSort implements SortStrategy {
    /**
     * Сортирует автобусы по всем полям: номер → модель → пробег.
     *
     * @param buses список автобусов для сортировки
     */
    @Override
    public void sort(List<Bus> buses) {
        buses.sort(Comparator
                .comparing(Bus::getNumber, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(Bus::getModel, String.CASE_INSENSITIVE_ORDER)
                .thenComparingInt(Bus::getMileage));
    }
}