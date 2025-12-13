package com.busapp.sorting;

import com.busapp.model.Bus;

import java.util.Comparator;
import java.util.List;

public class BusNumberSort implements SortStrategy{
    @Override
    public void sort(List<Bus> buses) {
        buses.sort(Comparator.comparing(Bus::getNumber, String.CASE_INSENSITIVE_ORDER));
    }
}
