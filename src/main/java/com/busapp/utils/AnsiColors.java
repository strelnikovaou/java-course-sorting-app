package com.busapp.utils;


/**
 * Класс с константами ANSI цветовых кодов.
 */
public final class AnsiColors {

    // Приватный конструктор - запрещаем создание экземпляров
    private AnsiColors() {
        throw new UnsupportedOperationException("Utility class");
    }

    // Сброс
    public static final String RESET = "\u001B[0m";

    // Основные цвета
    public static final String BLACK = "\u001B[30m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String MAGENTA = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    public static final String WHITE = "\u001B[37m";

    // Утилитный метод для окрашивания текста
    public static String colorize(String text, String color) {
        return color + text + RESET;
    }
}