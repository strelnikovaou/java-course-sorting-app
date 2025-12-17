package com.busapp.io;

import com.busapp.model.Bus;
import com.busapp.model.BusList;
import com.busapp.validation.BusValidator;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BusRepositoryTest {

    private BusRepository repository;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        repository = new BusRepository();
    }

    @Test
    void addShouldAddValidBus() {
        Bus bus = new Bus.Builder()
                .number("А123ВЕ")
                .model("ЛиАЗ-5292")
                .mileage(150000)
                .build();

        BusValidator.ValidationResult result = repository.add(bus);

        assertEquals(BusValidator.ValidationStatus.SUCCESS, result.status());
        assertEquals(1, repository.size());
    }

    @Test
    void addShouldRejectInvalidBus() {
        Bus invalidBus = new Bus.Builder()
                .number("INVALID")
                .model("Test")
                .mileage(-1000)
                .build();

        BusValidator.ValidationResult result = repository.add(invalidBus);

        assertEquals(BusValidator.ValidationStatus.FAIL, result.status());
        assertEquals(0, repository.size());
    }

    @Test
    void addAllShouldAddMultipleBuses() {
        BusList buses = new BusList();
        buses.add(new Bus.Builder().number("А123ВЕ").model("ЛиАЗ").mileage(100000).build());
        buses.add(new Bus.Builder().number("В456КМ").model("МАЗ").mileage(50000).build());

        List<BusValidator.ValidationResult> results = repository.addAll(buses);

        assertEquals(2, results.size());
        assertEquals(2, repository.size());
    }

    @Test
    void addAllShouldValidateEachBus() {
        BusList buses = new BusList();
        buses.add(new Bus.Builder().number("А123ВЕ").model("ЛиАЗ").mileage(100000).build());
        buses.add(new Bus.Builder().number("INVALID").model("МАЗ").mileage(-1000).build());

        List<BusValidator.ValidationResult> results = repository.addAll(buses);

        assertEquals(2, results.size());
        assertEquals(BusValidator.ValidationStatus.SUCCESS, results.get(0).status());
        assertEquals(BusValidator.ValidationStatus.FAIL, results.get(1).status());
        assertEquals(1, repository.size());
    }

    @Test
    void clearShouldRemoveAllBuses() {
        repository.add(new Bus.Builder().number("А123ВЕ").model("Test").mileage(100000).build());

        repository.clear();

        assertTrue(repository.isEmpty());
        assertEquals(0, repository.size());
    }

    @Test
    void isEmptyShouldReturnTrueForEmptyRepository() {
        assertTrue(repository.isEmpty());
    }

    @Test
    void isEmptyShouldReturnFalseForNonEmptyRepository() {
        repository.add(new Bus.Builder().number("А123ВЕ").model("Test").mileage(100000).build());

        assertFalse(repository.isEmpty());
    }

    @Test
    void getBusesCacheShouldReturnDefensiveCopy() {
        Bus bus = new Bus.Builder().number("А123ВЕ").model("Test").mileage(100000).build();
        repository.add(bus);

        BusList cache = repository.getBusesCache();
        cache.clear();

        assertEquals(1, repository.size());
    }

    @Test
    void hasFileShouldReturnFalseInitially() {
        assertFalse(repository.hasFile());
    }

    @Test
    void hasFileShouldReturnTrueAfterSet() {
        File testFile = tempDir.resolve("test.json").toFile();
        repository.setBusesFile(testFile);

        assertTrue(repository.hasFile());
    }

    @Test
    void saveJsonShouldWriteToFile() throws IOException {
        File testFile = tempDir.resolve("test_buses.json").toFile();
        repository.setBusesFile(testFile);
        repository.add(new Bus.Builder().number("А123ВЕ").model("ЛиАЗ").mileage(100000).build());

        boolean result = repository.saveJson();

        assertTrue(result);
        assertTrue(testFile.exists());
        String content = Files.readString(testFile.toPath());
        assertTrue(content.contains("А123ВЕ"));
    }

    @Test
    void loadFromJsonShouldReadFromFile() throws IOException {
        File testFile = tempDir.resolve("test_buses.json").toFile();
        String json = """
                [
                  {
                    "number": "А123ВЕ",
                    "model": "ЛиАЗ-5292",
                    "mileage": 150000
                  }
                ]
                """;
        Files.writeString(testFile.toPath(), json);

        repository.loadFromJson(testFile);

        assertEquals(1, repository.size());
        assertTrue(repository.isLoaded());
    }

    @Test
    void loadFromJsonShouldValidateBuses() throws IOException {
        File testFile = tempDir.resolve("test_buses.json").toFile();
        String json = """
                [
                  {
                    "number": "А123ВЕ",
                    "model": "ЛиАЗ",
                    "mileage": 150000
                  },
                  {
                    "number": "INVALID",
                    "model": "Test",
                    "mileage": -1000
                  }
                ]
                """;
        Files.writeString(testFile.toPath(), json);

        repository.loadFromJson(testFile);

        assertEquals(1, repository.size());
    }

    @Test
    void saveWithoutAppendShouldOverwriteFile() throws IOException {
        File testFile = tempDir.resolve("test_buses.json").toFile();
        repository.setBusesFile(testFile);

        repository.add(new Bus.Builder().number("А123ВЕ").model("ЛиАЗ").mileage(100000).build());
        repository.save(false);

        repository.clear();
        repository.add(new Bus.Builder().number("В456КМ").model("МАЗ").mileage(50000).build());
        repository.save(false);

        String content = Files.readString(testFile.toPath());
        assertFalse(content.contains("А123ВЕ"));
        assertTrue(content.contains("В456КМ"));
    }

    @Test
    void saveWithAppendShouldAddToExistingFile() throws IOException {
        File testFile = tempDir.resolve("test_buses.json").toFile();
        repository.setBusesFile(testFile);

        repository.add(new Bus.Builder().number("А123ВЕ").model("ЛиАЗ").mileage(100000).build());
        repository.save(false);

        repository.clear();
        repository.add(new Bus.Builder().number("В456КМ").model("МАЗ").mileage(50000).build());
        boolean result = repository.save(true);

        assertTrue(result);
        String content = Files.readString(testFile.toPath());
        assertTrue(content.contains("А123ВЕ"));
        assertTrue(content.contains("В456КМ"));
    }

    @Test
    void saveShouldFailIfFileNotSet() {
        repository.add(new Bus.Builder().number("А123ВЕ").model("Test").mileage(100000).build());

        boolean result = repository.save(false);

        assertFalse(result);
    }

    @Test
    void constructorWithPathShouldAutoload() throws IOException {
        File testFile = tempDir.resolve("test_buses.json").toFile();
        String json = """
                [
                  {
                    "number": "А123ВЕ",
                    "model": "ЛиАЗ",
                    "mileage": 150000
                  }
                ]
                """;
        Files.writeString(testFile.toPath(), json);
        repository.loadFromJson(testFile);
        assertTrue(repository.isLoaded());
        assertEquals(1, repository.getBusesCache().size());
    }
}
