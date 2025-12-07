package com.busapp.validation;

import com.busapp.model.Bus;

public class NullBusValidator extends BusValidator{
    @Override
    protected ValidationResult validateInternal(Bus bus) {
        if(bus == null)
            return errorValidationResult("Bus is null");
        return successValidationResult();
    }
}
