package com.busapp.service;

import com.busapp.io.BusRepository;
import com.busapp.model.Bus;
import com.busapp.model.BusList;
import com.busapp.sorting.*;
import com.busapp.utils.BusGenerator;
import com.busapp.validation.BusValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;

/**
 * Сервис для выполнения операций с автобусами.
 * Содержит бизнес-логику приложения.
 */
public class BusService {
    private static final Logger logger = LoggerFactory.getLogger(BusService.class);
    private final BusRepository repository;
    private final Scanner scanner;
    private final Map<Integer, SortStrategy> sortStrategies = new HashMap<>();

    public BusService(BusRepository repository, Scanner scanner) {
        this.repository = repository;
        this.scanner = scanner;
        initSortStrategies();
    }

    private void initSortStrategies() {
        sortStrategies.put(1, new BusNumberSort());
        sortStrategies.put(2, new BusModelSort());
        sortStrategies.put(3, new BusMileageSort());
        sortStrategies.put(4, new CompositeBusSort());
        sortStrategies.put(5, new EvenOddSortStrategy(new BusMileageSort(), Bus::getMileage));
        sortStrategies.put(6, new EvenOddSortStrategy(new BusNumberSort(), Bus::getMileage));
    }

    /**
     * Загружает автобусы из JSON файла.
     * Запрашивает путь к файлу у пользователя, очищает текущую коллекцию
     * и загружает данные из указанного файла.
     */
    public void loadFromFile() {
        System.out.println("Введите путь к файлу. (Пример: src/main/resources/buses.json) :");
        String line = scanner.nextLine();
        if (line.isBlank()) {
            logger.warn("Путь к файлу не может быть пустым");
            return;
        }
        repository.clear();
        repository.loadFromJson(new File(line));
        logger.info("Загружено {} автобусов", repository.size());
    }

    /**
     * Генерирует случайные автобусы и добавляет их в коллекцию.
     * Запрашивает у пользователя количество автобусов для генерации,
     * создает их с помощью BusGenerator и добавляет в репозиторий с валидацией.
     */
    public void generateRandom() {
        System.out.println("Сколько автобусов сгенерировать? :");
        String line = scanner.nextLine();
        if (line.isBlank() || !isInteger(line)) {
            return;
        }
        int count = Integer.parseInt(line);
        BusList generated = BusList.fromStream(
                IntStream.range(0, count).mapToObj(i -> BusGenerator.generateRandomBus())
        );
        List<BusValidator.ValidationResult> results = repository.addAll(generated);
        long successCount = results.stream()
                .filter(r -> r.status() == BusValidator.ValidationStatus.SUCCESS)
                .count();
        logger.info("Сгенерировано {} автобусов, добавлено {}", count, successCount);
    }

    /**
     * Выполняет ручной ввод автобусов через консоль.
     * Запрашивает у пользователя номер, модель и пробег для каждого автобуса.
     * Ввод продолжается до тех пор, пока пользователь не введет пустой номер.
     * Каждый автобус проходит валидацию перед добавлением в коллекцию.
     */
    public void inputManual() {
        logger.info("Ввод данных автобуса (пустой номер для завершения):");
        while (true) {
            System.out.println("Номер (формат А123ВЕ) РАЗРЕШЁННЫЕ БУКВЫ: А, В, Е, К, М, Н, О, Р, С, Т, У, Х : ");
            String number = scanner.nextLine();
            if (number.isEmpty()) break;

            System.out.println("Модель: ");
            String model = scanner.nextLine();
            System.out.println("Пробег: ");
            String tmp = scanner.nextLine();
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

    /**
     * Сортирует коллекцию автобусов согласно выбранной стратегии.
     * Отображает меню с доступными стратегиями сортировки,
     * получает выбор пользователя и применяет соответствующую стратегию.
     * Если коллекция пуста или стратегия не найдена, операция отменяется.
     */
    public void sortCollection() {
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

        String tmp = scanner.nextLine();
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
        repository.clear();
        repository.addAll(data);
        logger.info("Коллекция отсортирована: {}", strategy.getName());
    }

    /**
     * Выполняет поиск автобусов по номеру или модели.
     * Запрашивает поисковый запрос у пользователя и фильтрует коллекцию,
     * возвращая все автобусы, чей номер или модель содержат указанную подстроку.
     * Поиск не чувствителен к регистру.
     */
    public void searchBus() {
        System.out.println("Введите номер или модель: ");
        String query = scanner.nextLine().trim().toLowerCase();
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
            found.forEach(bus -> logger.info(" {}", bus));
        }
    }

    /**
     * Выполняет многопоточный подсчет количества автобусов с заданным пробегом.
     * Запрашивает целевой пробег у пользователя, затем асинхронно подсчитывает
     * количество автобусов с таким пробегом, используя параллельный стрим.
     * Результат выводится в консоль после завершения подсчета.
     * Метод блокирует выполнение до получения результата.
     */
    public void countOccurrences() {
        System.out.println("Введите пробег для подсчета: от 0 до 1_000_000");
        String tmp = scanner.nextLine();
        if (!isInteger(tmp)) {
            System.err.printf("Неверный пробег - %s\n", tmp);
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
}
