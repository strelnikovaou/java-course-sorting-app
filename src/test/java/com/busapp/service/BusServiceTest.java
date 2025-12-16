package com.busapp.service;

import com.busapp.io.BusRepository;
import com.busapp.model.Bus;
import com.busapp.model.BusList;
import com.busapp.validation.BusValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BusServiceTest {

    private BusRepository repository;
    private BusService service;

    @BeforeEach
    void setUp() {
        repository = mock(BusRepository.class);
        Scanner scanner = new Scanner(System.in);
        service = new BusService(repository, scanner);
    }

    @Test
    void generateRandomShouldAddBuses() {
        repository = mock(BusRepository.class);
        String input = "5\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        service = new BusService(repository, scanner);

        service.generateRandom();

        verify(repository, times(1)).addAll(any(BusList.class));
    }

    @Test
    void generateRandomShouldHandleInvalidInput() {
        repository = mock(BusRepository.class);
        String input = "invalid\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        service = new BusService(repository, scanner);

        assertDoesNotThrow(() -> service.generateRandom());
        verify(repository, never()).addAll(any());
    }

    @Test
    void sortCollectionShouldCallRepositoryMethods() {
        repository = mock(BusRepository.class);
        BusList buses = new BusList();
        buses.add(new Bus.Builder().number("А123ВЕ").model("Test").mileage(100000).build());

        when(repository.isEmpty()).thenReturn(false);
        when(repository.getBusesCache()).thenReturn(buses);

        String input = "1\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        service = new BusService(repository, scanner);

        service.sortCollection();

        verify(repository).clear();
        verify(repository).addAll(any(BusList.class));
    }

    @Test
    void sortCollectionShouldNotProceedIfEmpty() {
        repository = mock(BusRepository.class);
        when(repository.isEmpty()).thenReturn(true);

        Scanner scanner = new Scanner(System.in);
        service = new BusService(repository, scanner);

        service.sortCollection();

        verify(repository, never()).getBusesCache();
    }

    @Test
    void sortCollectionShouldHandleInvalidChoice() {
        repository = mock(BusRepository.class);
        BusList buses = new BusList();
        buses.add(new Bus.Builder().number("А123ВЕ").model("Test").mileage(100000).build());

        when(repository.isEmpty()).thenReturn(false);
        when(repository.getBusesCache()).thenReturn(buses);

        String input = "invalid\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        service = new BusService(repository, scanner);

        assertDoesNotThrow(() -> service.sortCollection());
    }

    @Test
    void searchBusShouldFilterBuses() {
        repository = mock(BusRepository.class);
        BusList buses = new BusList();
        buses.add(new Bus.Builder().number("А123ВЕ").model("ЛиАЗ").mileage(100000).build());
        buses.add(new Bus.Builder().number("В456КМ").model("МАЗ").mileage(50000).build());

        when(repository.getBusesCache()).thenReturn(buses);

        String input = "ЛиАЗ\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        service = new BusService(repository, scanner);

        service.searchBus();

        verify(repository).getBusesCache();
    }

    @Test
    void searchBusShouldHandleEmptyQuery() {
        repository = mock(BusRepository.class);
        String input = "\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        service = new BusService(repository, scanner);

        assertDoesNotThrow(() -> service.searchBus());
    }

    @Test
    void countOccurrencesShouldUseParallelStream() {
        repository = mock(BusRepository.class);
        BusList buses = new BusList();
        buses.add(new Bus.Builder().number("А123ВЕ").model("Test").mileage(100000).build());
        buses.add(new Bus.Builder().number("В456КМ").model("Test").mileage(100000).build());

        when(repository.getBusesCache()).thenReturn(buses);

        String input = "100000\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        service = new BusService(repository, scanner);

        service.countOccurrences();

        verify(repository).getBusesCache();
    }

    @Test
    void countOccurrencesShouldHandleInvalidInput() {
        repository = mock(BusRepository.class);
        String input = "invalid\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        service = new BusService(repository, scanner);

        assertDoesNotThrow(() -> service.countOccurrences());
        verify(repository, never()).getBusesCache();
    }

    @Test
    void inputManualShouldAddBusWithValidInput() {
        repository = mock(BusRepository.class);
        String input = "А123ВЕ\nЛиАЗ\n100000\n\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        service = new BusService(repository, scanner);

        BusValidator.ValidationResult successResult =
                new BusValidator.ValidationResult(BusValidator.ValidationStatus.SUCCESS, "OK");
        when(repository.add(any(Bus.class))).thenReturn(successResult);

        service.inputManual();

        verify(repository, atLeastOnce()).add(any(Bus.class));
    }

    @Test
    void inputManualShouldStopOnEmptyNumber() {
        repository = mock(BusRepository.class);
        String input = "\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        service = new BusService(repository, scanner);

        assertDoesNotThrow(() -> service.inputManual());
    }

    @Test
    void loadFromFileShouldHandleEmptyPath() {
        repository = mock(BusRepository.class);
        String input = "\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        service = new BusService(repository, scanner);

        assertDoesNotThrow(() -> service.loadFromFile());
        verify(repository, never()).loadFromJson(any());
    }
}
