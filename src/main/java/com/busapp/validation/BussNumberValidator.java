package com.busapp.validation;

import com.busapp.model.Bus;

public class BussNumberValidator extends BusValidator{
    @Override
    protected ValidationResult validateInternal(Bus bus) {
        if(bus.getNumber() == null || bus.getNumber().trim().isEmpty())
            return errorValidationResult("Номер автобуса не может быть пустым");
        return successValidationResult();
    }
}
