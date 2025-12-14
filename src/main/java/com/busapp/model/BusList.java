package com.busapp.model;

import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    /**
     * Создает BusList из Stream.
     *
     * @param stream поток автобусов
     * @return новый BusList с элементами из стрима
     */
    public static BusList fromStream(Stream<Bus> stream) {
        return stream.collect(Collectors.toCollection(BusList::new));
    }

}