package com.busapp.model;

import java.util.ArrayList;

public class BusList extends ArrayList<Bus> {

    /**
     * Создает пустой список автобусов.
     */
    public BusList() {
        super();
    }

    /**
     * Создает пустой список автобусов с начальной емкостью.
     *
     * @param initialCapacity начальная емкость списка
     */
    public BusList(int initialCapacity) {
        super(initialCapacity);
    }

}