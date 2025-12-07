package com.busapp;

import com.busapp.io.BusRepository;
import com.busapp.model.Bus;
import com.busapp.validation.*;
import java.util.List;

import static com.busapp.validation.BusValidator.ValidationResult;

public class Main {
    public static void main(String[] args) {
        BusRepository repository = new BusRepository("src/main/resources/buses.json");
        List<Bus> loaded = repository.getBusesCache();
        
        Bus invalidBus = new Bus.Builder()
                .number("РТ678СО")
                .model("MERZ")
                .mileage(-90)
                .build();

        ValidationResult result = repository.add(invalidBus);

        /**
         *  repository.loadFromJson(new File("path"))
         *  repository.remove( bus );
         *  repository.save();
         */

    }

}