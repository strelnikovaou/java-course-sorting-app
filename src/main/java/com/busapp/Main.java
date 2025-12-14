package com.busapp;

import com.busapp.io.BusRepository;
import com.busapp.model.Bus;
import com.busapp.model.BusList;
import com.busapp.sorting.*;
import com.busapp.utils.BusGenerator;
import com.busapp.validation.BusValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.busapp.utils.AnsiColors.*;


public class Main {
    private  Logger logger = LoggerFactory.getLogger(Main.class);
    private  BusRepository repository = new BusRepository();
    private  Map<Integer, SortStrategy> sortStrategies = new HashMap<>();
    private  Scanner sysInScanner = new Scanner(System.in);

    public Main(){
        sortStrategies.put(1, new BusNumberSort());
        sortStrategies.put(2, new BusModelSort());
        sortStrategies.put(3, new BusMileageSort());
        sortStrategies.put(4, new CompositeBusSort());
        sortStrategies.put(5, new EvenOddSortStrategy(new BusMileageSort(), Bus::getMileage));
        sortStrategies.put(6, new EvenOddSortStrategy(new BusNumberSort(), Bus::getMileage));
    }
    /**
     * Точка входа в приложение для управления коллекцией автобусов.
     *
     * @param args аргументы командной строки (не используются)
     */
    public static void main(String[] args) {
      Main main = new Main();
      main.runApplication();
    }

    void runApplication() {
        System.out.println("~~~ BUS SORTING APP | МЕНЮ ~~~");
        boolean exit = false;
        while (!exit) {
            showMenu();
            System.out.println("Введите число от 0 до 10 : ");
            String line = sysInScanner.nextLine();
            if (!isInteger(line)) {
                continue;
            }
            int choice = Integer.parseInt(line);

            switch (choice) {
                case 1 -> loadFromFile();
                case 2 -> generateRandom();
                case 3 -> inputManual();
                case 4 -> sortCollection();
                case 5 -> save(false);
                case 6 -> save(true);
                case 7 -> searchBus();
                case 8 -> countOccurrences();
                case 9 -> showAllBuses();
                case 10 -> clearCollection();
                case 0 -> {
                    exit = true;
                    sysInScanner.close();
                    System.out.println("Выход. До свидания!");
                }
                default -> System.out.println("Неверный выбор, попробуйте снова.");
            }
        }
    }
    private static boolean isValidPath(String path) {
        try {
            Paths.get(path);
        } catch (InvalidPathException | NullPointerException ex) {
            return false;
        }
        return true;
    }

    private void save(boolean append) {
        if (!repository.hasFile()) {
            System.out.println("Введите имя файла: (Пример: src/main/resources/buses.json)");
            String path = sysInScanner.nextLine();
            while (!isValidPath(path)) {
                logger.error("Некорректный путь до файла. Введите корректный.");
                path = sysInScanner.nextLine();
            }
            repository.setBusesFile(new File(path));
        }

        boolean success = repository.save(append);
        logger.info(success ? "✓ Сохранение успешно" : "✗ Ошибка сохранения");
    }


    /**
     * Проверяет, можно ли преобразовать строку в целое число.
     *
     * @param str строка для проверки
     * @return true если строка представляет целое число
     */
    public static boolean isInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static void showMenu() {
        System.out.println("\n" + CYAN +
                "╔══════════════════════════════════════════════╗\n" +
                "║ Г Л А В Н О Е М Е Н Ю ║\n" +
                "╠══════════════════════════════════════════════╣" + RESET);
        System.out.println(GREEN + " 0 " + RESET + " —   Выход");
        System.out.println(GREEN + " 1 " + RESET + " —   Загрузить из файла (JSON)");
        System.out.println(GREEN + " 2 " + RESET + " —   Создать случайные автобусы");
        System.out.println(GREEN + " 3 " + RESET + " —   Добавить автобусы вручную");
        System.out.println(GREEN + " 4 " + RESET + " —   Сортировать коллекцию");
        System.out.println(GREEN + " 5 " + RESET + " —   Сохранить в файл (перезаписать)");
        System.out.println(GREEN + " 6 " + RESET + " —   Сохранить в файл (добавить)");
        System.out.println(GREEN + " 7 " + RESET + " —   Поиск по номеру/модели");
        System.out.println(GREEN + " 8 " + RESET + " —   Подсчет вхождений (многопоточно)");
        System.out.println(GREEN + " 9 " + RESET + " —   Показать все автобусы");
        System.out.println(GREEN + "10 " + RESET + " —   Очистить кэш коллекции");

        System.out.println(CYAN +
                "╚══════════════════════════════════════════════╝" +
                RESET);

        System.out.print(YELLOW + "Ваш выбор → " + RESET);
    }

    private void loadFromFile() {
        System.out.println("Введите путь к файлу. (Пример: src/main/resources/buses.json) :");
        String line = sysInScanner.nextLine();
        if (line.isBlank()) {
            logger.warn("Путь к файлу не может быть пустым");
            return;
        }
        repository.clear();
        repository.loadFromJson(new File(line));
        logger.info("Загружено {} автобусов", repository.size());
    }


    private void generateRandom() {
        System.out.println("Сколько автобусов сгенерировать? :");
        String line = sysInScanner.nextLine();
        if (line.isBlank() || !isInteger(line)) {
            return;
        }
        int count = Integer.parseInt(line);

        BusList generated = BusList.fromStream(
                IntStream.range(0, count)
                        .mapToObj(i -> BusGenerator.generateRandomBus())
        );

        List<BusValidator.ValidationResult> results = repository.addAll(generated);
        long successCount = results.stream()
                .filter(r -> r.status() == BusValidator.ValidationStatus.SUCCESS)
                .count();

        logger.info("Сгенерировано {} автобусов, добавлено {}", count, successCount);
    }

    private void inputManual() {
        logger.info("Ввод данных автобуса (пустой номер для завершения):");

        while (true) {
            System.out.println("Номер (формат А123ВЕ) РАЗРЕШЁННЫЕ БУКВЫ: А, В, Е, К, М, Н, О, Р, С, Т, У, Х : ");
            String number = sysInScanner.nextLine();

            if (number.isEmpty())
                break;

            System.out.println("Модель: ");
            String model = sysInScanner.nextLine();

            System.out.println("Пробег: ");
            String tmp = sysInScanner.nextLine();
            if (tmp.isEmpty() || !isInteger(tmp)) {
                System.err.printf("Неверный пробег - %s\n", tmp);
                continue;
            }

            int mileage = Integer.parseInt(tmp);
            Bus bus = new Bus.Builder()
                    .number(number.trim())
                    .model(model.trim())
                    .mileage(mileage)
                    .build();

            BusValidator.ValidationResult result = repository.add(bus);
            if (result.status() == BusValidator.ValidationStatus.SUCCESS) {
                logger.info("✓ Автобус добавлен");
            } else {
                logger.error("✗ Ошибка: {}", result.message());
            }
        }
    }

    private void sortCollection() {
        if (repository.isEmpty()) {
            logger.warn("Коллекция пуста");
            return;
        }

        System.out.println("\nВыберите стратегию сортировки:");
        System.out.println("1. По номеру");
        System.out.println("2. По модели");
        System.out.println("3. По пробегу");
        System.out.println("4. По всем полям (Number → Model → Mileage)");
        System.out.println("5. По пробегу (только четные позиции)");
        System.out.println("6. По номеру (только четные позиции)");

        System.out.println("Ваш выбор: ");
        String tmp = sysInScanner.nextLine();
        if (tmp.isEmpty() || !isInteger(tmp)) {
            System.err.printf("Неверный выбор %s\n", tmp);
            return;
        }

        int choice = Integer.parseInt(tmp);
        SortStrategy strategy = sortStrategies.get(choice);

        if (strategy == null) {
            logger.error("Стратегия не найдена");
            return;
        }

        BusList data = repository.getBusesCache();
        strategy.sort(data);
        logger.info("Коллекция отсортирована: {}", strategy.getName());
    }


    private void searchBus() {

        System.out.println("Введите номер или модель: ");
        String query = sysInScanner.nextLine().trim().toLowerCase();
        if (query.isEmpty()) {
            logger.warn("Запрос не может быть пустым");
            return;
        }
        List<Bus> found = repository.getBusesCache().stream()
                .filter(bus -> bus.getNumber().toLowerCase().contains(query) ||
                        bus.getModel().toLowerCase().contains(query))
                .toList();

        if (found.isEmpty()) {
            logger.info("Автобусы не найдены");
        } else {
            logger.info("Найдено {} автобусов:", found.size());
            found.forEach(bus -> logger.info("  {}", bus));
        }
    }

    private void countOccurrences() {
        System.out.println("Введите пробег для подсчета: от  0 до 1_000_000");

        String tmp = sysInScanner.nextLine();
        if (!isInteger(tmp)) {
            System.err.printf("Не верный пробег - %s\n", tmp);
            return;
        }

        int target = Integer.parseInt(tmp);
        logger.info("Запуск многопоточного подсчета...");

        CompletableFuture<Long> future = CompletableFuture.supplyAsync(() ->
                repository.getBusesCache().parallelStream()
                        .filter(bus -> bus.getMileage() == target)
                        .count()
        );

        future.thenAccept(count -> {
            System.out.println("\n=== РЕЗУЛЬТАТ ===");
            logger.info("Количество автобусов с пробегом {}: {}", target, count);
        }).join();
    }

    private void clearCollection() {
        repository.clear();
        logger.info("Коллекция очищена");
    }

    private void showAllBuses() {
        if (repository.isEmpty()) {
            logger.info("Коллекция пуста");
            return;
        }
        logger.info("Всего автобусов: {}", repository.size());
        repository.getBusesCache().forEach(bus -> logger.info("  {}", bus));
    }
}