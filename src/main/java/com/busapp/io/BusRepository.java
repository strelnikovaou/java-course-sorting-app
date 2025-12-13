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
    private  boolean loaded = false;

    public BusRepository() {
        this(null);
    }

    public BusRepository(String path) {

        validatorChain.setNext(new BusMileageValidator())
                .setNext(new BusModelValidator())
                .setNext(new BusNumberValidator());

        if (path != null && !path.isEmpty()) {
            setBusesFile(new File(path));
        }

    }

    public void setBusesFile(File file) {
        this.busesFile = file;
        if (this.busesFile != null && this.busesFile.exists()) {
            loadFromJson(busesFile);
        }
    }

    public boolean isLoaded() {return this.loaded;}
    public boolean hasFile() {return this.busesFile != null;}

    public BusList getBusesCache() {
        BusList result = new BusList();
        result.addAll(busesCache);
        return result;
    }
    public void loadFromJson(){
        loadFromJson(busesFile);
    }
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


    public BusValidator.ValidationResult add(Bus bus) {
        BusValidator.ValidationResult validate = validatorChain.validate(bus);
        if (validate.status() == BusValidator.ValidationStatus.FAIL) {
            logger.error("Fail to add Bus: {}", validate.message());
            return validate;
        }
        busesCache.add(bus);
        return validate;
    }

    public List<BusValidator.ValidationResult> addAll(List<Bus> buses) {
        List<BusValidator.ValidationResult> results = new ArrayList<>();
        for (Bus bus : buses) {
            results.add(add(bus));
        }
        return results;
    }


    public boolean saveJson() {
        return saveToFile(busesFile, false);
    }

    public boolean appendToJson(List<Bus> additionalBuses) {
        if (busesFile == null) {
            logger.error("Файл не задан");
            return false;
        }

        List<Bus> existing = new ArrayList<>();
        if (busesFile.exists()) {
            try {
                Bus[] loaded = objectMapper.readValue(busesFile, Bus[].class);
                Collections.addAll(existing, loaded);

            } catch (IOException e) {
                logger.error("Ошибка чтения файла для добавления: {}", e.getMessage());
                return false;
            }
        }

        List<Bus> toAdd = new ArrayList<>();
        for (Bus bus : additionalBuses) {
            if (validatorChain.validate(bus).status() == BusValidator.ValidationStatus.SUCCESS) {
                toAdd.add(bus);
            }
        }

        existing.addAll(toAdd);
        return saveToFile(busesFile, existing, false);
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


    public void clear() {
        busesCache.clear();
    }

    public boolean isEmpty() {
        return busesCache.isEmpty();
    }

    public int size() {
        return busesCache.size();
    }

}
