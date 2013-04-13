package com.dubture.composer.ui.wizard;

/**
 * Throw this exception with the corresponding {@link Severity} into your
 * {@link AbstractValidator} implementation
 * 
 * @author Robert Gruendler <r.gruendler@gmail.com>
 * 
 */
public class ValidationException extends Exception {

	private static final long serialVersionUID = 1L;
	private Severity severity;

	public enum Severity {
		WARNING, ERROR
	}

	public ValidationException(String message, Severity severity) {
		super(message);
		this.severity = severity;
	}

	public Severity getSeverity() {
		return severity;
	}
}
