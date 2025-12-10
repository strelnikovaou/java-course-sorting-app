package com.busapp.model.builder;

import com.busapp.model.Bus;
import com.busapp.model.BusList;
import com.busapp.validation.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BusCreatorTest {

    @Test
    void createBusFromIncorrectData1() {
        BusValidator validatorChain = new NullBusValidator();
        validatorChain.setNext(new BussMileageValidator())
                      .setNext(new BussModelValidator())
                      .setNext(new BussNumberValidator());

        BusCreator busCreator =  new BusCreator(new StringBusBuilder());
        Bus bus = busCreator.bus(", Fiat, 6000");

        assertTrue(()->
            validatorChain.validate(bus).status() == BusValidator.ValidationStatus.FAIL);
    }

    @Test
    void createBusFromIncorrectData2(){
        BusValidator validatorChain = new NullBusValidator();
        validatorChain.setNext(new BussMileageValidator())
                .setNext(new BussModelValidator())
                .setNext(new BussNumberValidator());

        BusCreator busCreator =  new BusCreator(new StringBusBuilder());
        Bus bus = busCreator.bus("А123, Fiat, -6000");

        assertTrue(()->
                validatorChain.validate(bus).status() == BusValidator.ValidationStatus.FAIL);
    }

    @Test
    void createBusFromCorrectData(){
        BusValidator validatorChain = new NullBusValidator();
        validatorChain.setNext(new BussMileageValidator())
                .setNext(new BussModelValidator())
                .setNext(new BussNumberValidator());

        BusCreator busCreator =  new BusCreator(new StringBusBuilder());
        Bus bus = busCreator.bus("А123, Fiat 6000");

        assertTrue(()->
                validatorChain.validate(bus).status() == BusValidator.ValidationStatus.SUCCESS);
    }

    @Test
    void createBusFromRandomData(){
        BusValidator validatorChain = new NullBusValidator();
        validatorChain.setNext(new BussMileageValidator())
                .setNext(new BussModelValidator())
                .setNext(new BussNumberValidator());

        BusCreator busCreator =  new BusCreator(new RandomBusBuilder());
        Bus bus = busCreator.bus(null);

        assertTrue(()->
                validatorChain.validate(bus).status() == BusValidator.ValidationStatus.SUCCESS);
    }
}