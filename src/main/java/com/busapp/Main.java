package com.busapp;

import com.busapp.io.BusRepository;
import com.busapp.model.*;
import com.busapp.model.builder.*;

import static java.lang.System.*;
import static com.busapp.validation.BusValidator.ValidationResult;

public class Main {
    public static void main(String[] args) throws Exception {

        // fill from file buses.json
        BusRepository repository = new BusRepository("src/main/resources/buses.json");
        BusList busList = new BusList(repository.getBusesCache());

        // fill from random data
        BusCreator busCreator = new BusCreator(new RandomBusBuilder());
        busList.add(busCreator.bus(null));

        // fill from string (console user input)
        busCreator = new BusCreator(new StringBusBuilder());
        busList.add(busCreator.bus("А123БВ Икарус 50000"));

        // multithreaded search
        busList.countPrint(busCreator.bus("А123БВ Икарус 50000"));

        // sort by all fields
        busList.sort();

        // sort by Mileage field
        busList.sortByEvenValuesOfMileage();

        // save (add) buses to file busOutputFile.json
        busList.saveToFile();

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