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
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static final BusRepository repository = new BusRepository();
    private static final Map<Integer, SortStrategy> sortStrategies = new HashMap<>();
    private static final Scanner SCANNER = new Scanner(System.in);

    static {
        sortStrategies.put(1, new BusNumberSort());
        sortStrategies.put(2, new BusModelSort());
        sortStrategies.put(3, new BusMileageSort());
        sortStrategies.put(4, new CompositeBusSort());
        sortStrategies.put(5, new EvenOddSortStrategy(new BusMileageSort(), Bus::getMileage));
        sortStrategies.put(6, new EvenOddSortStrategy(new BusNumberSort(), Bus::getMileage));
    }

    public static void main(String[] args) {
        System.out.println("~~~ BUS SORTING APP | МЕНЮ ~~~");
        boolean exit = false;
        while (!exit) {
            showMenu();
            System.out.println("Введите число от 0 до 9 : ");
            String line = SCANNER.nextLine();
            if(!isInteger(line)){
                continue;
            }
            int choice =  Integer.parseInt(line);

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
                case 0 -> {
                    exit = true;
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

    private static void save(boolean append) {
        if(!repository.hasFile()){
            System.out.println("Введите имы файла:");
            String path = SCANNER.nextLine();
            while (!isValidPath(path)){
                logger.error("Не корректный путь до файла.Введите корректный.");
                path = SCANNER.nextLine();
            }
            repository.setBusesFile(new File(path));
        }

        if (append) {
            if(!repository.isLoaded()){
                BusList busesCache = repository.getBusesCache();
                repository.loadFromJson(); //  clear cache and load from file
                repository.addAll(busesCache);
            }
        }
        boolean success = repository.saveJson();
        logger.info(success ? "✓ Сохранение успешно" : "✗ Ошибка сохранения");
    }


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
        System.out.println(GREEN + " 1 " + RESET + " —   Загрузить из файла (JSON)");
        System.out.println(GREEN + " 2 " + RESET + " —   Создать случайные автобусы");
        System.out.println(GREEN + " 3 " + RESET + " —   Добавить автобусы вручную");
        System.out.println(GREEN + " 4 " + RESET + " —   Сортировать коллекцию");
        System.out.println(GREEN + " 5 " + RESET + " —   Сохранить в файл (перезаписать)");
        System.out.println(GREEN + " 6 " + RESET + " —   Сохранить в файл (добавить)");
        System.out.println(GREEN + " 7 " + RESET + " —   Поиск по номеру/модели");
        System.out.println(GREEN + " 8 " + RESET + " —   Подсчет вхождений (многопоточно)");
        System.out.println(GREEN + " 9 " + RESET + " —   Показать все автобусы");
        System.out.println(GREEN + " 0 " + RESET + " —   Выход");

        System.out.println(CYAN +
                "╚══════════════════════════════════════════════╝" +
                RESET);

        System.out.print(YELLOW + "Ваш выбор → " + RESET);
    }

    private static void loadFromFile() {
        System.out.println("Введите путь к файлу. (Пример: src/main/resources/buses.json) :");
        String line = SCANNER.nextLine();
        if (line.isBlank()) {
            logger.warn("Путь к файлу не может быть пустым");
            return;
        }
        repository.clear();
        repository.loadFromJson(new File(line));
        logger.info("Загружено {} автобусов", repository.size());
    }


    private static void generateRandom() {
        System.out.println("Сколько автобусов сгенерировать? :");
        String line = SCANNER.nextLine();
         if(line.isBlank() || !isInteger(line)) {
             return;
         }
        int count = Integer.parseInt(line);

        List<Bus> generated = IntStream.range(0, count)
                .mapToObj(i -> BusGenerator.generateRandomBus())
                .collect(Collectors.toList());

        List<BusValidator.ValidationResult> results = repository.addAll(generated);
        long successCount = results.stream()
                .filter(r -> r.status() == BusValidator.ValidationStatus.SUCCESS)
                .count();

        logger.info("Сгенерировано {} автобусов, добавлено {}", count, successCount);
    }

    private static void inputManual() {
        logger.info("Ввод данных автобуса (пустой номер для завершения):");

        while (true)
        {
            System.out.println("Номер (формат А123ВЕ) РАЗРЕШЁННЫЕ БУКВЫ: А, В, Е, К, М, Н, О, Р, С, Т, У, Х : ");
            String number = SCANNER.nextLine();

            if (number.isEmpty())
                break;

            System.out.println("Модель: ");
            String model = SCANNER.nextLine();

            System.out.println("Пробег: ");
            String tmp = SCANNER.nextLine();
            if(tmp.isEmpty()||!isInteger(tmp)){
                System.err.printf("Не верный пробег - %s\n",tmp);
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

    private static void sortCollection() {
        if (repository.isEmpty()){
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
        String tmp = SCANNER.nextLine();
        if(tmp.isEmpty()||!isInteger(tmp)){
            System.err.printf("Не верный выбор %s\n",tmp);
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


    private static void searchBus() {

        System.out.println("Введите номер или модель: ");
        String query = SCANNER.nextLine().trim().toLowerCase();
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

    private static void countOccurrences() {
        System.out.println("Введите пробег для подсчета: от  0 до 1_000_000");
        int target = Integer.parseInt(SCANNER.nextLine().trim());

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



    private static void showAllBuses() {
        if (repository.isEmpty()) {
            logger.info("Коллекция пуста");
            return;
        }
        logger.info("Всего автобусов: {}", repository.size());
        repository.getBusesCache().forEach(bus -> logger.info("  {}", bus));
    }
}