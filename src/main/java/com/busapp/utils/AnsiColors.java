package com.busapp.utils;


/**
 * Утилитный класс для работы с ANSI цветовыми кодами в консоли.
 * Содержит константы основных цветов и метод для окрашивания текста.
 * <p>
 * Класс не предназначен для создания экземпляров.
 */
public final class AnsiColors {

    private AnsiColors() {
        throw new UnsupportedOperationException("Utility class");
    }

    /** Код сброса всех цветовых настроек. */
    public static final String RESET = "\u001B[0m";

    public static final String BLACK = "\u001B[30m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String MAGENTA = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    public static final String WHITE = "\u001B[37m";

    /**
     * Окрашивает текст указанным ANSI цветом с автоматическим сбросом.
     *
     * @param text текст для окрашивания
     * @param color ANSI код цвета (используйте константы класса)
     * @return текст с цветовым кодом и сбросом цвета в конце
     */
    public static String colorize(String text, String color) {
        return color + text + RESET;
    }
}