package com.busapp.sorting;

import com.busapp.model.Bus;

import java.util.List;

public interface SortStrategy {
    /**
     * Сортирует список автобусов согласно реализованной стратегии.
     *
     * @param buses список автобусов для сортировки
     */
    void sort(List<Bus> buses);

    /**
     * Возвращает название стратегии сортировки.
     *
     * @return имя класса стратегии
     */
    default String getName() {
        return this.getClass().getSimpleName();
    }
}
