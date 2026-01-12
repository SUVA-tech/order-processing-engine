package com.company.order.exception;

/**
 * Exception thrown when an order fails compliance validation.
 */
public class ComplianceViolationException extends RuntimeException {

    public ComplianceViolationException() {
        super();
    }

    public ComplianceViolationException(String message) {
        super(message);
    }

    public ComplianceViolationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ComplianceViolationException(Throwable cause) {
        super(cause);
    }
}
