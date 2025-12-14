package com.busapp.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import java.util.Comparator;
import java.util.Objects;

@JsonDeserialize(builder = Bus.Builder.class)
public class Bus implements Comparable<Bus> {
    private final String number;
    private final String model;
    private final int mileage;

    private static final Comparator<Bus> NATURAL_ORDER_COMPARATOR =
            Comparator.comparing(Bus::getNumber, String.CASE_INSENSITIVE_ORDER)
                    .thenComparing(Bus::getModel, String.CASE_INSENSITIVE_ORDER)
                    .thenComparingInt(Bus::getMileage);

    private Bus(Builder builder) {
        this.number = builder.number;
        this.model = builder.model;
        this.mileage = builder.mileage;
    }

    /**
     * Возвращает номер автобуса.
     *
     * @return номер автобуса в формате А123ВЕ
     */
    public String getNumber() {
        return number;
    }

    /**
     * Возвращает модель автобуса.
     *
     * @return модель автобуса
     */
    public String getModel() {
        return model;
    }

    /**
     * Возвращает пробег автобуса.
     *
     * @return пробег в километрах
     */
    public int getMileage() {
        return mileage;
    }

    @JsonPOJOBuilder(buildMethodName = "build", withPrefix = "")
    public static class Builder {
        private String number;
        private String model;
        private int mileage;

        /**
         * Устанавливает номер автобуса.
         *
         * @param number номер автобуса в формате А123ВЕ
         * @return текущий экземпляр Builder для цепочки вызовов
         */
        public Builder number(String number) {
            this.number = number;
            return this;
        }

        /**
         * Устанавливает модель автобуса.
         *
         * @param model модель автобуса
         * @return текущий экземпляр Builder для цепочки вызовов
         */
        public Builder model(String model) {
            this.model = model;
            return this;
        }

        /**
         * Устанавливает пробег автобуса.
         *
         * @param mileage пробег в километрах
         * @return текущий экземпляр Builder для цепочки вызовов
         */
        public Builder mileage(int mileage) {
            this.mileage = mileage;
            return this;
        }

        /**
         * Создает экземпляр Bus с заданными параметрами.
         *
         * @return новый экземпляр автобуса
         */
        public Bus build() {
            return new Bus(this);
        }
    }

    @Override
    public int compareTo(Bus other) {
        return NATURAL_ORDER_COMPARATOR.compare(this, other);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bus bus = (Bus) o;
        return mileage == bus.mileage &&
                Objects.equals(number, bus.number) &&
                Objects.equals(model, bus.model);
    }

    @Override
    public int hashCode() {
        return Objects.hash(number, model, mileage);
    }

    @Override
    public String toString() {
        return String.format("Bus{number='%s', model='%s', mileage=%d}", number, model, mileage);
    }
}