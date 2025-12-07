package com.busapp.io;

import com.busapp.model.Bus;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.busapp.validation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.print.DocFlavor;

public class BusRepository {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(BusRepository.class);
    private BusValidator validatorChain = new NullBusValidator();
    private List<Bus> busesCache = new ArrayList<>();
    private File busesFile = null;

    public BusRepository(){
        this(null);
    }
    public BusRepository(String path)  {
        validatorChain.setNext(new BussMileageValidator())
                .setNext(new BussModelValidator())
                .setNext(new BussNumberValidator());
        if(path != null && !path.isEmpty()){
            busesFile = new File(path);
            loadFromJson(busesFile);
        }
    }
    public  List<Bus> getBusesCache(){
        return Collections.unmodifiableList(busesCache);
    }

    public List<Bus> loadFromJson(File file) {
        if (file == null || !file.exists() || !file.isFile()) {
            logger.error("Wrong file");
            return Collections.emptyList();
        }
        try {
            Bus[] loaded = objectMapper.readValue(file, Bus[].class);
            for(Bus bus : loaded) {
                BusValidator.ValidationResult vr = validatorChain.validate(bus);
                if(vr.status() == BusValidator.ValidationStatus.FAIL){
                    logger.error("Ошибка валидации : {}, автобус будет удалён\n", vr.message());
                }else {
                    busesCache.add(bus);
                }
            }
            busesFile = file;
            logger.info("Успешно загружено {} автобусов из {}", busesCache.size(), file.getAbsolutePath());
            return Collections.unmodifiableList(busesCache);
        } catch (IOException e) {
            logger.error("Ошибка чтения файла: {}", file.getAbsolutePath(), e);
            return List.of();
        }
    }

    public void remove(Bus bus){
        if (bus == null) {
            logger.error("Fail to remove null Bus");
            return;
        }
        busesCache.remove(bus);
    }

    public BusValidator.ValidationResult add(Bus bus) {
        BusValidator.ValidationResult validate = validatorChain.validate(bus);
        if(validate.status() == BusValidator.ValidationStatus.FAIL){
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
        if (busesFile == null) {
            return false;
        }

        if (!busesFile.exists()) {
            try {
                if (!busesFile.createNewFile()) {
                    return false;
                }
            } catch (IOException e) {
                logger.error(e.getMessage());
                return false;
            }
        }

        try {
            Bus[] busArray = busesCache.toArray(new Bus[0]);
            objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValue(busesFile, busArray);

            logger.info("Успешно записано {} автобусов в {}", busesCache.size(), busesFile.getAbsolutePath());
            return true;
        } catch (IOException e) {
            logger.error("Ошибка записи файла: {}", busesFile.getAbsolutePath(), e);
        }
        return false;
    }

    public void clearCache(){
        busesCache.clear();
    }

}
