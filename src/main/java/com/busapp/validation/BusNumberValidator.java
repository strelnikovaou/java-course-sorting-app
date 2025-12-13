package com.busapp.validation;

import com.busapp.model.Bus;
import java.util.regex.Pattern;

public class BusNumberValidator extends BusValidator {
    private static final Pattern PATTERN = Pattern.compile("^[АВЕКМНОРСТУХ]\\d{3}[АВЕКМНОРСТУХ]{2}$");

    @Override
    protected ValidationResult validateInternal(Bus bus) {
        if (bus.getNumber() == null || bus.getNumber().trim().isEmpty())
            return errorValidationResult("Номер автобуса не может быть пустым");
        if (!PATTERN.matcher(bus.getNumber()).matches()) {
            return errorValidationResult("Номер должен соответствовать формату А123ВЕ");
        }
        return successValidationResult();
    }
}
