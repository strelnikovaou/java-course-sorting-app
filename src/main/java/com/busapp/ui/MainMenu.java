package com.busapp.ui;

import com.busapp.io.BusRepository;
import com.busapp.model.Bus;
import com.busapp.model.BusList;
import com.busapp.model.builder.BusCreator;
import com.busapp.model.builder.RandomBusBuilder;
import com.busapp.model.builder.StringBusBuilder;

import java.util.Scanner;
import java.util.List;
import java.util.stream.IntStream;


public class MainMenu {

    private final Scanner scanner = new Scanner(System.in);
    private BusList busList = new BusList();

    public static void main(String[] args) {
        new MainMenu().run();
    }

    public void run() {
        System.out.println("~~~ Bus Sorting App | Меню ~~~");
        boolean exit = false;
        while (!exit) {
            printMenu();
            String choice = scanner.nextLine().trim();
            try {
                switch (choice) {
                    case "1" -> loadFromFile();
                    case "2" -> fillRandom();
                    case "3" -> fillManual();
                    case "4" -> showAll();
                    case "5" -> sortAllFields();
                    case "6" -> sortEvenMileage();
                    case "7" -> saveToOutputFile();
                    case "8" -> countOccurrences();
                    case "9" -> clearList();
                    case "0" -> {
                        exit = true;
                        System.out.println("Выход. До свидания!");
                    }
                    default -> System.out.println("Неверный выбор, попробуйте снова.");
                }
            } catch (Exception e) {
                System.out.println("Ошибка: " + e.getMessage());
                e.printStackTrace(System.out);
            }
        }
    }

    private static final String RESET = "\u001B[0m";
    private static final String CYAN = "\u001B[36m";
    private static final String YELLOW = "\u001B[33m";
    private static final String GREEN = "\u001B[32m";
    private static final String BLUE = "\u001B[34m";
    private static final String MAGENTA = "\u001B[35m";

    private void printMenu() {
        System.out.println("\n" + CYAN +
                "╔══════════════════════════════════════════════╗\n" +
                "║ Г Л А В Н О Е М Е Н Ю ║\n" +
                "╠══════════════════════════════════════════════╣" + RESET);

        System.out.println(GREEN + " 1 " + RESET + " —   Загрузить коллекцию из файла (buses.json)");
        System.out.println(GREEN + " 2 " + RESET + " —   Создать случайные автобусы");
        System.out.println(GREEN + " 3 " + RESET + " —   Добавить автобус вручную");
        System.out.println(GREEN + " 4 " + RESET + " —   Показать все автобусы");
        System.out.println(GREEN + " 5 " + RESET + " —   Отсортировать (по всем полям)");
        System.out.println(GREEN + " 6 " + RESET + " —   Специальная сортировка чётных пробегов");
        System.out.println(GREEN + " 7 " + RESET + " —   Сохранить в файл (append)");
        System.out.println(GREEN + " 8 " + RESET + " —   Подсчитать вхождения (многопоточно)");
        System.out.println(GREEN + " 9 " + RESET + " —   Очистить список");
        System.out.println(GREEN + " 0 " + RESET + " —   Выход");

        System.out.println(CYAN +
                "╚══════════════════════════════════════════════╝" +
                RESET);

        System.out.print(YELLOW + "Ваш выбор → " + RESET);
    }

    private void loadFromFile() {
        System.out.print("Укажите путь к файлу (ENTER — src/main/resources/buses.json): ");
        String path = scanner.nextLine().trim();
        if (path.isEmpty()) path = "src/main/resources/buses.json";
        BusRepository repository = new BusRepository(path);
        busList = new BusList(repository.getBusesCache());
        System.out.println("Загружено автобусов: " + busList.size());
    }

    private void fillRandom() {
        System.out.print("Сколько случайных автобусов добавить? (число): ");
        int n = readInt();
        BusCreator creator = new BusCreator(new RandomBusBuilder());
        IntStream.range(0, n).forEach(i -> busList.add(creator.bus(null)));
        System.out.println("Добавлено " + n + " случайных автобусов.");
    }

    private void fillManual() {
        System.out.println("Введите строку в формате: Номер, Модель, Пробег");
        System.out.print("Строка (или пустая для возврата): ");
        String line = scanner.nextLine().trim();
        if (line.isEmpty()) {
            System.out.println("Ввод отменён.");
            return;
        }
        BusCreator creator = new BusCreator(new StringBusBuilder());
        busList.add(creator.bus(line));
        System.out.println("Автобус добавлен (если прошёл валидацию). Текущий размер: " + busList.size());
    }

    private void showAll() {
        System.out.println("Содержимое коллекции:");
        busList.stream().forEach(System.out::println);
    }

    private void sortAllFields() {
        System.out.println("Сортировка по всем полям...");
        busList.sort();
        System.out.println("Сортировка завершена.");
    }

    private void sortEvenMileage() {
        System.out.println("Сортировка автобусов по чётным пробегам (нечётные остаются на месте)...");
        busList.sortByEvenValuesOfMileage();
        System.out.println("Специальная сортировка завершена.");
    }

    private void saveToOutputFile() {
        System.out.print("Укажите имя выходного файла (ENTER — busOutputFile.json): ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) name = "busOutputFile.json";
        BusRepository repo = new BusRepository(name);
        repo.addAll(busList.stream().toList());
        repo.saveJson();
        System.out.println("Данные добавлены в файл: " + name);
    }

    private void countOccurrences() throws Exception {
        System.out.println("Введите автобус для подсчёта в формате: Номер, Модель, Пробег");
        System.out.print("Строка: ");
        String line = scanner.nextLine().trim();
        if (line.isEmpty()) {
            System.out.println("Отмена.");
            return;
        }
        BusCreator creator = new BusCreator(new StringBusBuilder());
        Bus target = creator.bus(line);
        int cnt = busList.count(target);
        System.out.printf("Найдено %d вхождений.\n", cnt);
    }

    private void clearList() {
        busList = new BusList();
        System.out.println("Список очищен.");
    }

    private int readInt() {
        String s = scanner.nextLine().trim();
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            System.out.println("Неверный ввод — будет использовано 0.");
            return 0;
        }
    }
}
