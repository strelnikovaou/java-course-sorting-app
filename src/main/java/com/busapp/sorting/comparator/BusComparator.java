package com.busapp.sorting.comparator;

import com.busapp.model.Bus;
import java.util.Comparator;

public class BusComparator {

    public static final Comparator<Bus> BY_ALL_FIELDS =
            Comparator.comparing(Bus::getNumber)
                      .thenComparing(Bus::getModel)
                      .thenComparingInt(Bus::getMileage);
}