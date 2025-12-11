package com.busapp.io;

import com.busapp.model.Bus;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BusRepositoryTest {
    private static final String OUTPUT_FILE = "output.json";
    private static final String EMPTY_FILE = "empty.json";

    @BeforeAll
    static void cleanUp()  {
        try {
            Files.delete(new File(OUTPUT_FILE).toPath());
        } catch (IOException ignored) {}

        try {
            Files.delete(new File(EMPTY_FILE).toPath());
        } catch (IOException ignored) {}
    }

    @Test
    void shouldReadBusesFromFile(@TempDir Path tempDir) throws IOException {
        BusRepository busRepository = new BusRepository();
        Path jsonFile = tempDir.resolve("test-buses.json");
        String jsonContent = """
                [
                  {
                    "number": "А123БВ",
                    "model": "МАЗ",
                    "mileage": 50000
                  }
                ]
                """;
        Files.writeString(jsonFile, jsonContent);

        List<Bus> buses = busRepository.loadFromJson(jsonFile.toFile());


        assertEquals(1, buses.size());
        assertEquals("А123БВ", buses.get(0).getNumber());
    }


    @Test
    void shouldWriteAndReadBuses() {
        BusRepository busRepository = new BusRepository( OUTPUT_FILE );

        List<Bus> busesToWrite = List.of(
                new Bus.Builder().number("А123").model("МАЗ").mileage(100).build(),
                new Bus.Builder().number("Б456").model("КАВЗ").mileage(200).build()
        );

        busRepository.addAll(busesToWrite);
        busRepository.saveJson();

        BusRepository busRepositoryForRead = new BusRepository( OUTPUT_FILE );
        List<Bus> busesCache1 = busRepository.getBusesCache();
        List<Bus> busesCache2 = busRepositoryForRead.getBusesCache();

        assertEquals(2, busesCache1.size());
        assertEquals(2, busesCache2.size());
        assertTrue(busesCache2.containsAll(busesCache1));
    }

    @Test
    void shouldHandleEmptyList(@TempDir Path tempDir) {
        Path jsonFile = tempDir.resolve(EMPTY_FILE);

        try {
            Files.writeString(jsonFile, "");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        BusRepository busRepository = new BusRepository();
        List<Bus> buses = busRepository.loadFromJson(jsonFile.toFile());
        assertTrue(buses.isEmpty());
    }
}
