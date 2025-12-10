package com.busapp.model.builder;

import com.busapp.model.Bus;

public abstract class BusBuilder {
    private final int fieldsCount = 3;
    private String[] data;
    private Bus bus;

    public void setData(String data){
        if(data != null)
            this.data = data.trim().split("[\\s,]+");
        bus = new Bus();
    }

    public String[] getData(){
        return data;
    }

    public boolean dataIsSet(){
        return data != null && data.length == fieldsCount;
    }

    public Bus bus(){ return bus; }

    abstract void buildNumber();
    abstract void buildModel();
    abstract void buildMileage();
}
