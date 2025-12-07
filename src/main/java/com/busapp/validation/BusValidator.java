package com.busapp.validation;

import com.busapp.model.Bus;
import java.util.ArrayList;
import java.util.List;

public final class BusValidator {

    private BusValidator() {}

    //Валидация Builder
    public static void validate(Bus.Builder builder) {
        if (builder == null) {
            throw new IllegalArgumentException("Builder не может быть null");
        }

        List<String> errors = new ArrayList<>();

        if (builder.getNumber() == null || builder.getNumber().trim().isEmpty()) {
            errors.add("номер автобуса не может быть пустым");
        }
        if (builder.getModel() == null || builder.getModel().trim().isEmpty()) {
            errors.add("модель автобуса не может быть пустой");
        }
        if (builder.getMileage() < 0) {
            errors.add("пробег не может быть отрицательным (значение: " + builder.getMileage() + ")");
        }

        if (!errors.isEmpty()) {
            throw new IllegalArgumentException("Невалидные данные автобуса: " + String.join(", ", errors));
        }
    }

    //Валидация готового объекта Bus
    public static void validate(Bus bus) {
        if (bus == null) {
            throw new IllegalArgumentException("Bus не может быть null");
        }

        List<String> errors = new ArrayList<>();

        if (bus.getNumber() == null || bus.getNumber().trim().isEmpty()) {
            errors.add("номер автобуса не может быть пустым");
        }
        if (bus.getModel() == null || bus.getModel().trim().isEmpty()) {
            errors.add("модель автобуса не может быть пустой");
        }
        if (bus.getMileage() < 0) {
            errors.add("пробег не может быть отрицательным (значение: " + bus.getMileage() + ")");
        }
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException("Невалидные данные автобуса: " + String.join(", ", errors));
        }
    }
}