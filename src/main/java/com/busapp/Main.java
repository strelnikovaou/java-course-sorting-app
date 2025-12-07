package com.busapp;

import com.busapp.io.JsonFileManager;
import com.busapp.model.Bus;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        JsonFileManager fileManager = new JsonFileManager();

        // Чтение из файла
        List<Bus> buses = fileManager.readFromFile("src/main/resources/buses.json");

        System.out.println("Загружено автобусов: " + buses.size());

        for (int i = 0; i < buses.size(); i++) {
            Bus bus = buses.get(i);
            System.out.printf("№: %s, Модель: %s, Пробег: %d\n",
                    bus.getNumber(), bus.getModel(), bus.getMileage());
        }


        List <Bus> buses2 = new ArrayList<>();
        buses2.addAll(buses);
        buses2.add(new Bus.Builder().number("РТ678СО").model("MERZ").mileage(90000).build());

        // Запись в файл
        fileManager.writeToFile("src/main/resources/buses.json", buses2);

    }
}