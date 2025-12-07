package com.busapp.validation;

import com.busapp.model.Bus;

public class BussModelValidator extends BusValidator{
    @Override
    protected ValidationResult validateInternal(Bus bus) {
        if(bus.getModel() == null || bus.getModel().trim().isEmpty())
            return errorValidationResult("Модель автобуса не может быть пустой");
        return successValidationResult();
    }
}
