package com.busapp.model;

import com.busapp.validation.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class BusList extends ArrayList<Bus>{

    public BusList(){
        super();
    }

    public BusList(List<Bus> loaded) {
        super(loaded);
    }

    public void sortByEvenValuesOfMileage(){
        for(int i=0; i<this.size()-1; i++){
            if(this.get(i).getMileage() % 2 != 0)
                continue;
            int min = i;
            for(int j=i+1; j<this.size(); j++){
                if(this.get(j).getMileage() % 2 == 0 &&
                        this.get(j).getMileage() < this.get(min).getMileage())
                    min = j;
            }
            if(min != i)
                swap(i, min);
        }
    }

    private void swap(int i, int j){
        Bus temp = this.get(i);
        this.set(i, this.get(j));
        this.set(j, temp);
    }

    public int count(Bus bus) throws ExecutionException, InterruptedException {
        FutureTask<Integer> fTask1 = new FutureTask<>(()->{
            int sum = 0;
            for(int i=0; i<this.size(); i+=2)
                sum += bus.equals(this.get(i)) ? 1 : 0;
            return sum;
        });
        FutureTask<Integer> fTask2 = new FutureTask<>(()->{
            int sum = 0;
            for(int i=1; i<this.size(); i+=2)
                sum += bus.equals(this.get(i)) ? 1 : 0;
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
