package com.busapp;

import com.busapp.io.BusRepository;
import com.busapp.model.*;
import com.busapp.model.builder.*;

import static java.lang.System.*;
import static com.busapp.validation.BusValidator.ValidationResult;

public class Main {
    public static void main(String[] args) throws Exception {

        // case 1:
        BusRepository repository = new BusRepository("src/main/resources/buses.json");
        BusList busList = new BusList(repository.getBusesCache());

        // case 2:
        BusCreator busCreator = new BusCreator(new RandomBusBuilder());
        busList.add(busCreator.bus(null));

        // case 3:
        busCreator = new BusCreator(new StringBusBuilder());
        busList.add(busCreator.bus("А123БВ Икарус 50000"));

        busList.stream().forEach(out::println);
        busList.countPrint(busCreator.bus("А123БВ Икарус 50000"));

        Bus invalidBus = new Bus()
                .setNumber("РТ678СО")
                .setModel("MERZ")
                .setMileage(-90);

        ValidationResult result = repository.add(invalidBus);

        /**
         *  repository.loadFromJson(new File("path"))
         *  repository.remove( bus );
         *  repository.save();
         */

    }

}