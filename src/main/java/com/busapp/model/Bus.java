package com.busapp.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.Comparator;
import java.util.Objects;

@JsonDeserialize(builder = Bus.class)
public class Bus implements Comparable<Bus> {
    @JsonProperty("number")
    private String number;
    @JsonProperty("model")
    private String model;
    @JsonProperty("mileage")
    private int mileage;

    public String getNumber() {
        return number;
    }
    public String getModel() { return model; }
    public int getMileage() { return mileage; }

    @JsonProperty("number")
    public Bus setNumber(String number){
        this.number = number;
        return this;
    }

    @JsonProperty("model")
    public Bus setModel(String model){
        this.model = model;
        return this;
    }

    @JsonProperty("mileage")
    public Bus setMileage(int mileage){
        this.mileage = mileage;
        return this;
    }

    public Bus build(){
        return this;
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