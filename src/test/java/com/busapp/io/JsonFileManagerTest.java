package com.busapp.io;

import com.busapp.model.Bus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JsonFileManagerTest {
    private final JsonFileManager manager = new JsonFileManager();

    @Test
    void shouldReadBusesFromFile(@TempDir Path tempDir) throws IOException {
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

        List<Bus> buses = manager.readFromFile(jsonFile.toString());

        assertEquals(1, buses.size());
        assertEquals("А123БВ", buses.get(0).getNumber());
    }

    @Test
    void shouldReturnEmptyListForNonExistentFile() {
        List<Bus> buses = manager.readFromFile("несуществующий_файл.json");
        assertTrue(buses.isEmpty());
    }

    @Test
    void shouldWriteAndReadBuses(@TempDir Path tempDir) {
        List<Bus> busesToWrite = List.of(
                new Bus.Builder().number("А123").model("МАЗ").mileage(100).build(),
                new Bus.Builder().number("Б456").model("КАВЗ").mileage(200).build()
        );

        Path jsonFile = tempDir.resolve("output.json");

        manager.writeToFile(jsonFile.toString(), busesToWrite);

        List<Bus> readBuses = manager.readFromFile(jsonFile.toString());

        assertEquals(2, readBuses.size());
        assertEquals(busesToWrite, readBuses);
    }

    @Test
    void shouldHandleEmptyList(@TempDir Path tempDir) {
        Path jsonFile = tempDir.resolve("empty.json");

        manager.writeToFile(jsonFile.toString(), new ArrayList<>());
        List<Bus> readBuses = manager.readFromFile(jsonFile.toString());

        assertTrue(readBuses.isEmpty());
    }
}
