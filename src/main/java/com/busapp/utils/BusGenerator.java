package com.busapp.utils;

import com.busapp.model.Bus;
import java.util.Random;

public class BusGenerator {
    private static final Random RANDOM = new Random();
    private static final String[] MODELS = {"Mercedes", "Volvo", "MAN", "Scania", "Ikarus", "LiAZ", "PAZ", "KAVZ"};
    private static final String LETTERS = "АВЕКМНОРСТУХ";
    private static final String DIGITS = "0123456789";

    /**
     * Генерирует случайный автобус с валидными данными.
     *
     * @return новый автобус со случайными номером, моделью и пробегом
     */
    public static Bus generateRandomBus() {
        return new Bus.Builder()
                .number(generateNumber())
                .model(MODELS[RANDOM.nextInt(MODELS.length)])
                .mileage(RANDOM.nextInt(500_000))
                .build();
    }

    private static String generateNumber() {
        StringBuilder sb = new StringBuilder();
        // Формат: А123ВЕ (российский)
        sb.append(LETTERS.charAt(RANDOM.nextInt(LETTERS.length())));
        for (int i = 0; i < 3; i++) {
            sb.append(DIGITS.charAt(RANDOM.nextInt(DIGITS.length())));
        }
        sb.append(LETTERS.charAt(RANDOM.nextInt(LETTERS.length())));
        sb.append(LETTERS.charAt(RANDOM.nextInt(LETTERS.length())));
        return sb.toString();
    }
}