package com.busapp;

import com.busapp.io.BusRepository;
import com.busapp.model.Bus;
import com.busapp.model.BusList;
import com.busapp.validation.*;
import java.util.List;

import static com.busapp.validation.BusValidator.ValidationResult;

public class Main {
    public static void main(String[] args) throws Exception {
        BusRepository repository = new BusRepository("src/main/resources/buses.json");
        List<Bus> loaded = repository.getBusesCache();

        BusList busList = new BusList(loaded);
        busList.add(new Bus.Builder().number(null).model(null).mileage(-1).build());
        busList.add(new Bus.Builder().number(null).model(null).mileage(-1).build());

        busList.add(new Bus.Builder().number("A123ВА").model("ПАЗ").mileage(0).build());
        busList.add(new Bus.Builder().number("А123БВ").model("Икарус").mileage(50000).build());

        busList.forEach(System.out::println);
        busList.countPrint(new Bus.Builder().number("А123БВ").model("Икарус").mileage(50000).build());
        
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