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
    public int size(){
        return busList.size();
    }
    public List<BusValidator.ValidationResult> addAll(List<Bus> _busList){
        List<BusValidator.ValidationResult> results = new ArrayList<>();
        for(var bus : _busList)
            results.add(add(bus));
        return results;
    }
    public Stream<Bus> stream(){
        return busList.stream();
    }
    public void sort(){
        busList.sort(null);
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
