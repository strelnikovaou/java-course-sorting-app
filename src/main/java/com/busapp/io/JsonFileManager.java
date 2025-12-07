package com.busapp.io;

import com.busapp.model.Bus;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonFileManager {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(JsonFileManager.class);

    public List<Bus> readFromFile(String filePath) {
        try {
            Bus[] busArray = objectMapper.readValue(new File(filePath), Bus[].class);
            logger.info("Успешно прочитано {} автобусов из {}", busArray.length, filePath);
            return new ArrayList<>(Arrays.asList(busArray));
        } catch (IOException e) {
            logger.error("Ошибка чтения файла: {}", filePath, e);
            return new ArrayList<>();
        }
    }

    public void writeToFile(String filePath, List<Bus> buses) {
        try {
            Bus[] busArray = buses.toArray(new Bus[0]);

            objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValue(new File(filePath), busArray);

            logger.info("Успешно записано {} автобусов в {}", buses.size(), filePath);

        } catch (IOException e) {
            logger.error("Ошибка записи файла: {}", filePath, e);
        }
    }
}
