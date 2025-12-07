package com.busapp.model;

import com.busapp.validation.BusValidator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Comparator;
import java.util.Objects;

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

    public String getNumber() { return number; }
    public String getModel() { return model; }
    public int getMileage() { return mileage; }

    public static class Builder {
        private String number;
        private String model;
        private int mileage;

        @JsonProperty("number")
        public Builder number(String number) {
            this.number = number;
            return this;
        }

        @JsonProperty("model")
        public Builder model(String model) {
            this.model = model;
            return this;
        }

        @JsonProperty("mileage")
        public Builder mileage(int mileage) {
            this.mileage = mileage;
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
            BusValidator.validate(this);
            return new Bus(this);
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