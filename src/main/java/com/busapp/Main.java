package com.busapp;

import com.busapp.service.BusService;
import com.busapp.io.BusRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.Scanner;

import static com.busapp.utils.AnsiColors.*;

/**
 * Главный класс приложения для управления коллекцией автобусов.
 * Предоставляет консольный интерфейс для выполнения операций
 * с автобусами: загрузка, создание, сортировка, поиск и сохранение.
 */
public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private final BusRepository repository = new BusRepository();
    private final Scanner scanner = new Scanner(System.in);
    private final BusService service = new BusService(repository, scanner);

    public static void main(String[] args) {
        Main main = new Main();
        main.runApplication();
    }

    /**
     * Запускает главный цикл приложения.
     * Отображает меню, обрабатывает выбор пользователя и выполняет
     * соответствующие операции. Цикл продолжается до выбора пункта "Выход".
     * Гарантирует освобождение ресурсов (Scanner) при завершении работы.
     */
    void runApplication() {
        System.out.println("~~~ BUS SORTING APP | МЕНЮ ~~~");
        boolean exit = false;

        try {
            while (!exit) {
                showMenu();
                String line = scanner.nextLine();
                if (!isInteger(line)) {
                    continue;
                }

                int choice = Integer.parseInt(line);
                switch (choice) {
                    case 1 -> service.loadFromFile();
                    case 2 -> service.generateRandom();
                    case 3 -> service.inputManual();
                    case 4 -> service.sortCollection();
                    case 5 -> save(false);
                    case 6 -> save(true);
                    case 7 -> service.searchBus();
                    case 8 -> service.countOccurrences();
                    case 9 -> showAllBuses();
                    case 10 -> clearCollection();
                    case 0 -> {
                        exit = true;
                        System.out.println("Выход. До свидания!");
                    }
                    default -> System.out.println("Неверный выбор, попробуйте снова.");
                }
            }
        } finally {
            if (scanner != null) {
                scanner.close();
                logger.info("Ресурсы освобождены");
            }
        }
    }

    /**
     * Отображает главное меню приложения.
     * Выводит список доступных операций с цветным форматированием
     * и запрос на выбор пользователя.
     */
    private void showMenu() {
        System.out.println("\n" + CYAN +
                "╔══════════════════════════════════════════════╗\n" +
                "║ Г Л А В Н О Е   М Е Н Ю                     ║\n" +
                "╠══════════════════════════════════════════════╣" + RESET);
        System.out.println(GREEN + " 0 " + RESET + " — Выход");
        System.out.println(GREEN + " 1 " + RESET + " — Загрузить из файла (JSON)");
        System.out.println(GREEN + " 2 " + RESET + " — Создать случайные автобусы");
        System.out.println(GREEN + " 3 " + RESET + " — Добавить автобусы вручную");
        System.out.println(GREEN + " 4 " + RESET + " — Сортировать коллекцию");
        System.out.println(GREEN + " 5 " + RESET + " — Сохранить в файл (перезаписать)");
        System.out.println(GREEN + " 6 " + RESET + " — Сохранить в файл (добавить)");
        System.out.println(GREEN + " 7 " + RESET + " — Поиск по номеру/модели");
        System.out.println(GREEN + " 8 " + RESET + " — Подсчет вхождений (многопоточно)");
        System.out.println(GREEN + " 9 " + RESET + " — Показать все автобусы");
        System.out.println(GREEN + "10 " + RESET + " — Очистить кэш коллекции");
        System.out.println(CYAN +
                "╚══════════════════════════════════════════════╝" + RESET);
        System.out.print(YELLOW + "Ваш выбор → " + RESET);
    }

    /**
     * Сохраняет текущую коллекцию автобусов в файл.
     * Если файл для сохранения не был установлен ранее, запрашивает путь у пользователя.
     * Проверяет корректность пути перед сохранением.
     *
     * @param append если true, добавляет данные к существующему файлу;
     *               если false, перезаписывает файл
     */
    private void save(boolean append) {
        if (!repository.hasFile()) {
            System.out.println("Введите имя файла: (Пример: src/main/resources/buses.json)");
            String path = scanner.nextLine();
            while (!isValidPath(path)) {
                logger.error("Некорректный путь до файла. Введите корректный.");
                path = scanner.nextLine();
            }
            repository.setBusesFile(new File(path));
        }
        boolean success = repository.save(append);
        logger.info(success ? "✓ Сохранение успешно" : "✗ Ошибка сохранения");
    }


    /**
     * Отображает все автобусы из текущей коллекции.
     * Если коллекция пуста, выводит соответствующее сообщение.
     * Для каждого автобуса выводится его строковое представление.
     */
    private void showAllBuses() {
        if (repository.isEmpty()) {
            logger.info("Коллекция пуста");
            return;
        }
        logger.info("Всего автобусов: {}", repository.size());
        repository.getBusesCache().forEach(bus -> logger.info("  {}", bus));
    }

    /**
     * Очищает текущую коллекцию автобусов.
     * Удаляет все автобусы из кеша репозитория.
     */
    private void clearCollection() {
        repository.clear();
        logger.info("Коллекция очищена");
    }

    /**
     * Проверяет, можно ли преобразовать строку в целое число.
     *
     * @param str строка для проверки
     * @return true если строка представляет корректное целое число, иначе false
     */
    private static boolean isInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Проверяет корректность пути к файлу.
     * Использует Paths.get() для валидации синтаксиса пути.
     *
     * @param path строка с путем к файлу для проверки
     * @return true если путь синтаксически корректен, иначе false
     */
    private static boolean isValidPath(String path) {
        try {
            Paths.get(path);
        } catch (InvalidPathException | NullPointerException ex) {
            return false;
        }
        return true;
    }
}
