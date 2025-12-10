package com.busapp.model.builder;

import com.busapp.model.Bus;

public class BusCreator {
    private final BusBuilder busBuilder;
    public BusCreator(BusBuilder busBuilder){
        this.busBuilder = busBuilder;
    }
    public Bus bus(String data){
        busBuilder.setData(data);
        busBuilder.buildNumber();
        busBuilder.buildModel();
        busBuilder.buildMileage();

        return busBuilder.bus();
    }
}
