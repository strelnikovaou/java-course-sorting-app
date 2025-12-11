package com.busapp.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.Comparator;
import java.util.Objects;
import java.util.Random;

@JsonDeserialize(builder = Bus.Builder.class)
public class Bus implements Comparable<Bus> {
    @JsonProperty("number")
    private final String number;
    @JsonProperty("model")
    private final String model;
    @JsonProperty("mileage")
    private final int mileage;

    private Bus(Builder builder) {
        this.number = builder.number;
        this.model = builder.model;
        this.mileage = builder.mileage;
    }

    public String getNumber() {
        return number;
    }

    public String getModel() {
        return model;
    }

    public int getMileage() {
        return mileage;
    }

    public static class Builder {
        private final static String[] defaultBusModel = {"Урал", "BMW", "BAD"};
        private final static Random random = new Random();
        private String number = "";
        private String model = "";
        private int mileage = 0;

        @JsonProperty("number")
        public Builder number(String number) {
            this.number = Objects.requireNonNullElseGet(number, () ->
                    new StringBuilder()
                    .append((char) (getRandomNumber('А', 'В')))
                    .append(getRandomNumber(100, 999))
                    .append((char) (getRandomNumber('Г', 'Е')))
                    .append((char) (getRandomNumber('К', 'Н')))
                    .toString());
            return this;
        }

        @JsonProperty("model")
        public Builder model(String model) {
            this.model = Objects.requireNonNullElseGet(model, () ->
                    defaultBusModel[random.nextInt(defaultBusModel.length)]);
            return this;
        }

        @JsonProperty("mileage")
        public Builder mileage(int mileage) {
            this.mileage = mileage < 0 ? getRandomNumber(10, 1_000_000) : mileage;
            return this;
        }

        public String getNumber() {
            return number;
        }

        public String getModel() {
            return model;
        }

        public int getMileage() {
            return mileage;
        }

        public Bus build() {
            return new Bus(this);
        }

        private int getRandomNumber(int min, int max){
            return random.nextInt(max - min + 1) + min;
        }
    }

    @Override
    public int compareTo(Bus other) {
        if (other == null) throw new NullPointerException("Сравнение с null невозможно");

        return Comparator.comparing(Bus::getNumber)
                .thenComparing(Bus::getModel)
                .thenComparingInt(Bus::getMileage)
                .compare(this, other);
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