package com.busapp.sorting;

import com.busapp.model.Bus;

import java.util.Comparator;
import java.util.List;

public class BusMileageSort implements SortStrategy {
    @Override
    public void sort(List<Bus> buses) {
        buses.sort(Comparator.comparingInt(Bus::getMileage));
    }
}