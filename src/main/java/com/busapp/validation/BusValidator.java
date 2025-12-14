package com.busapp.validation;

import com.busapp.model.Bus;

public abstract class BusValidator {
    private BusValidator next;

    /**
     * Устанавливает следующий валидатор в цепочке ответственности.
     *
     * @param next следующий валидатор для проверки
     * @return установленный валидатор для цепочки вызовов
     */
    public BusValidator setNext(BusValidator next) {
        this.next = next;
        return next;
    }

    /**
     * Валидирует автобус через цепочку валидаторов.
     *
     * @param bus автобус для валидации
     * @return результат валидации
     */
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

    public enum ValidationStatus {
        SUCCESS,
        FAIL
    }

    /**
     * Создает результат валидации с ошибкой.
     *
     * @param message сообщение об ошибке
     * @return результат валидации со статусом FAIL
     */
    public static ValidationResult errorValidationResult(String message) {
        return new ValidationResult(ValidationStatus.FAIL, message);
    }

    /**
     * Создает успешный результат валидации.
     *
     * @return результат валидации со статусом SUCCESS
     */
    public static ValidationResult successValidationResult() {
        return new ValidationResult(ValidationStatus.SUCCESS, "ok");
    }

    public record ValidationResult(ValidationStatus status, String message) {
    }

    /**
     * Выполняет внутреннюю логику валидации конкретным валидатором.
     *
     * @param bus автобус для валидации
     * @return результат валидации
     */
    protected abstract ValidationResult validateInternal(Bus bus);
}