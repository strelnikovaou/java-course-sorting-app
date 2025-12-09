package com.busapp.model.builder;

public class StringBusBuilder extends BusBuilder{
    @Override
    void buildNumber() {
        bus().setNumber(dataIsSet() ? getData()[0] : "");
    }

    @Override
    void buildModel() {
        bus().setModel(dataIsSet() ? getData()[1] : "");
    }

    @Override
    void buildMileage() {
        try {
            bus().setMileage(dataIsSet() ? Integer.parseInt(getData()[2]) : 0);
        }
        catch (NumberFormatException ex){
            bus().setMileage(0);
        }
    }
}
