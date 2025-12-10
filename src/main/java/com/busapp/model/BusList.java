package com.busapp.model;

import com.busapp.io.BusRepository;
import com.busapp.validation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.stream.Stream;

public class BusList {
    private List<Bus> busList;
    private BusValidator validatorChain = new NullBusValidator();
    private static final String outputFileName = "busOutputFile.json";
    private static final Logger logger = LoggerFactory.getLogger(BusList.class);

    public BusList(){
        this(List.of());
    }
    public BusList(List<Bus> _busList){
        busList = new ArrayList<>();
        addAll(_busList);
        validatorChain.setNext(new BussMileageValidator())
                      .setNext(new BussModelValidator())
                      .setNext(new BussNumberValidator());
    }

    public BusValidator.ValidationResult add(Bus bus){
        BusValidator.ValidationResult validate = validatorChain.validate(bus);
        if(validate.status() == BusValidator.ValidationStatus.FAIL)
            logger.error("Fail to add Bus: {}", validate.message());
        else
            busList.add(bus);
        return validate;
    }

    public void saveToFile(){
        BusRepository busRepository = new BusRepository(outputFileName);
        busRepository.addAll(busList);
        busRepository.saveJson();
    }

    public List<BusValidator.ValidationResult> addAll(List<Bus> _busList){
        List<BusValidator.ValidationResult> results = new ArrayList<>();
        for(var bus : _busList)
            results.add(add(bus));
        return results;
    }

    public int size(){
        return busList.size();
    }

    public boolean equals(List<Bus> other){
        return busList.equals(other);
    }

    public Stream<Bus> stream(){
        return busList.stream();
    }

    public void sort(){
        busList.sort(null);
    }

    public void sortByEvenValuesOfMileage(){
        for(int i=0; i<busList.size()-1; i++){
            if(busList.get(i).getMileage() % 2 != 0)
                continue;
            int min = i;
            for(int j=i+1; j<busList.size(); j++){
                if(busList.get(j).getMileage() % 2 == 0 &&
                        busList.get(j).getMileage() < busList.get(min).getMileage())
                    min = j;
            }
            if(min != i)
                swap(i, min);
        }
    }

    private void swap(int i, int j){
        Bus temp = busList.get(i);
        busList.set(i, busList.get(j));
        busList.set(j, temp);
    }

    public int count(Bus bus) throws ExecutionException, InterruptedException {
        FutureTask<Integer> fTask1 = new FutureTask<>(()->{
            int sum = 0;
            for(int i=0; i<busList.size(); i+=2)
                sum += bus.equals(busList.get(i)) ? 1 : 0;
            return sum;
        });
        FutureTask<Integer> fTask2 = new FutureTask<>(()->{
            int sum = 0;
            for(int i=1; i<busList.size(); i+=2)
                sum += bus.equals(busList.get(i)) ? 1 : 0;
            return sum;
        });
        new Thread(fTask1).start();
        new Thread(fTask2).start();

        return fTask1.get() + fTask2.get();
    }

    public void countPrint(Bus bus) throws ExecutionException, InterruptedException {
        System.out.printf("Count %s = %d\n", bus, count(bus));
    }
}
