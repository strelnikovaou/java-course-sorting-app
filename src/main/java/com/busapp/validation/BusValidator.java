package com.busapp.validation;

import com.busapp.model.Bus;

public abstract class BusValidator {
    private BusValidator next;

    public BusValidator setNext(BusValidator next){
        this.next = next;
        return next;
    }

    public final ValidationResult validate(Bus bus) {
        ValidationResult result = validateInternal(bus);
        if (result.status() != ValidationStatus.SUCCESS) {
            return result;
        }
        if (next != null) {
            return next.validate(bus);
        }
        return successValidationResult();
    }

    public enum ValidationStatus{
        SUCCESS,
        FAIL
    }

    public static ValidationResult errorValidationResult(String message){
          return new ValidationResult(ValidationStatus.FAIL, message);
    }
    public static ValidationResult successValidationResult(){
        return new ValidationResult(ValidationStatus.SUCCESS, "ok");
    }

    public record ValidationResult (ValidationStatus status, String message) {}
    protected abstract ValidationResult validateInternal(Bus bus);
}