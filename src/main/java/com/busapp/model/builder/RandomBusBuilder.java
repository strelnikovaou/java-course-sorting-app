package com.busapp.model.builder;

import java.util.Random;

public class RandomBusBuilder extends BusBuilder{
    private final Random random = new Random();
    private final String[] busModel = {"Урал", "BMW", "BAD"};

    @Override
    void buildNumber() {
        bus().setNumber(new StringBuilder()
                        .append((char)(getRandomNumber('А', 'В')))
                        .append(getRandomNumber(100, 999))
                        .append((char)(getRandomNumber('Г', 'Е')))
                        .append((char)(getRandomNumber('К', 'Н')))
                        .toString());
    }

    @Override
    void buildModel() {
        bus().setModel(busModel[random.nextInt(busModel.length)]);
    }

    @Override
    void buildMileage() {
        bus().setMileage(getRandomNumber(10, 1_000_000));
    }

    private int getRandomNumber(int min, int max){
        return random.nextInt(max - min + 1) + min;
    }
}
