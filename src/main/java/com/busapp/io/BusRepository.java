package com.busapp.io;

import com.busapp.model.Bus;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.busapp.model.BusList;
import com.busapp.validation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BusRepository {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(BusRepository.class);
    private BusValidator validatorChain = new NullBusValidator();
    private BusList busesCache = new BusList();
    private File busesFile = null;
    private boolean loaded = false;

    /**
     * Создает новый репозиторий автобусов с инициализацией цепочки валидаторов.
     */
    public BusRepository() {
        this(null);
    }

    /**
     * Создает новый репозиторий автобусов с автоматической загрузкой из файла.
     *
     * @param path путь к файлу для загрузки данных
     */
    public BusRepository(String path) {
        validatorChain.setNext(new BusMileageValidator())
                .setNext(new BusModelValidator())
                .setNext(new BusNumberValidator());

        if (path != null && !path.isEmpty()) {
            setBusesFile(new File(path), true);
        }

    }

    /**
     * Устанавливает файл для операций сохранения/загрузки.
     *
     * @param file файл для работы с данными
     */
    public void setBusesFile(File file) {
        this.busesFile = file;
    }

    /**
     * Устанавливает файл для операций сохранения/загрузки с опцией автозагрузки.
     *
     * @param file     файл для работы с данными
     * @param autoLoad если true, данные будут загружены автоматически
     */
    public void setBusesFile(File file, boolean autoLoad) {
        this.busesFile = file;
        if (autoLoad && this.busesFile != null && this.busesFile.exists()) {
            loadFromJson(busesFile);
        }
    }

    /**
     * Проверяет, были ли загружены данные из файла.
     *
     * @return true если данные успешно загружены
     */
    public boolean isLoaded() {
        return this.loaded;
    }

    /**
     * Проверяет, установлен ли файл для операций.
     *
     * @return true если файл установлен
     */
    public boolean hasFile() {
        return this.busesFile != null;
    }

    /**
     * Возвращает защищенную копию (defensive copy) текущего кеша автобусов.
     *
     * Метод создает новый список {@link BusList}, содержащий ссылки на те же
     * объекты {@link Bus}, что и внутренний кеш. Это поверхностное копирование
     * (shallow copy) безопасно, поскольку класс {@link Bus} является неизменяемым
     * (immutable) - все его поля финальные и не могут быть изменены после создания.
     *
     * @return новый {@link BusList}, содержащий копию всех автобусов из кеша.
     *         Изменения структуры возвращенного списка не влияют на репозиторий.
     * @see Bus
     * @see BusList
     */
    public BusList getBusesCache() {
        BusList result = new BusList();
        result.addAll(busesCache);
        return result;
    }

    /**
     * Загружает автобусы из ранее установленного JSON файла.
     */
    public void loadFromJson() {
        loadFromJson(busesFile);
    }

    /**
     * Загружает автобусы из указанного JSON файла с валидацией.
     *
     * @param file JSON файл для загрузки
     */
    public void loadFromJson(File file) {
        if (file == null || !file.exists() || !file.isFile()) {
            logger.error("Wrong file");
            return;
        }
        try {
            busesCache.clear();

            Bus[] loaded = objectMapper.readValue(file, Bus[].class);
            for (Bus bus : loaded) {
                BusValidator.ValidationResult vr = validatorChain.validate(bus);
                if (vr.status() == BusValidator.ValidationStatus.SUCCESS) {
                    busesCache.add(bus);
                } else {
                    logger.error("Ошибка валидации: {}", vr.message());
                }
            }
            this.busesFile = file;
            this.loaded = true;
            logger.info("Успешно загружено {} автобусов из {}", busesCache.size(), file.getAbsolutePath());
        } catch (IOException e) {
            logger.error("Ошибка чтения файла: {}", file.getAbsolutePath(), e);
        }
    }

    /**
     * Добавляет автобус в репозиторий после валидации.
     *
     * @param bus автобус для добавления
     * @return результат валидации
     */
    public BusValidator.ValidationResult add(Bus bus) {
        BusValidator.ValidationResult validate = validatorChain.validate(bus);
        if (validate.status() == BusValidator.ValidationStatus.FAIL) {
            logger.error("Fail to add Bus: {}", validate.message());
            return validate;
        }
        busesCache.add(bus);
        return validate;
    }

    /**
     * Добавляет список автобусов в репозиторий с валидацией каждого.
     *
     * @param buses список автобусов для добавления
     * @return список результатов валидации для каждого автобуса
     */
    public List<BusValidator.ValidationResult> addAll(List<Bus> buses) {
        List<BusValidator.ValidationResult> results = new ArrayList<>();
        for (Bus bus : buses) {
            results.add(add(bus));
        }
        return results;
    }

    /**
     * Сохраняет текущую коллекцию в установленный JSON файл.
     *
     * @return true если сохранение успешно
     */
    public boolean saveJson() {
        return saveToFile(busesFile, false);
    }

    /**
     * Сохраняет данные в файл с поддержкой режима добавления
     *
     * @param append true - добавить к существующим данным в файле, false - перезаписать файл
     * @return true если сохранение успешно
     */
    public boolean save(boolean append) {
        if (busesFile == null) {
            logger.error("Файл не задан");
            return false;
        }

        if (append) {
            return appendToFile();
        } else {
            return saveToFile(busesFile, busesCache, false);
        }
    }

    /**
     * Добавляет текущий кеш к существующим данным в файле
     *
     * @return true если сохранение успешно
     */
    private boolean appendToFile() {
        List<Bus> allBuses = new ArrayList<>();

        if (busesFile.exists()) {
            try {
                Bus[] existing = objectMapper.readValue(busesFile, Bus[].class);
                Collections.addAll(allBuses, existing);
                logger.info("Загружено {} существующих автобусов из файла", existing.length);
            } catch (IOException e) {
                logger.error("Ошибка чтения файла для добавления: {}", e.getMessage());
                return false;
            }
        }

        allBuses.addAll(busesCache);
        logger.info("Добавлено {} новых автобусов из кеша", busesCache.size());

        return saveToFile(busesFile, allBuses, false);
    }

    private boolean saveToFile(File file, boolean append) {
        return saveToFile(file, busesCache, append);
    }

    private boolean saveToFile(File file, List<Bus> data, boolean append) {
        try {
            if (!file.exists()) {
                file.createNewFile();
            }

            objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValue(file, data.toArray(new Bus[0]));

            logger.info("Успешно записано {} автобусов в {}", data.size(), file.getAbsolutePath());
            return true;
        } catch (IOException e) {
            logger.error("Ошибка записи файла: {}", file.getAbsolutePath(), e);
            return false;
        }
    }

    /**
     * Очищает кеш автобусов.
     */
    public void clear() {
        busesCache.clear();
    }

    /**
     * Проверяет, пуст ли кеш автобусов.
     *
     * @return true если коллекция пуста
     */
    public boolean isEmpty() {
        return busesCache.isEmpty();
    }

    /**
     * Возвращает количество автобусов в кеше.
     *
     * @return размер коллекции
     */
    public int size() {
        return busesCache.size();
    }

}
