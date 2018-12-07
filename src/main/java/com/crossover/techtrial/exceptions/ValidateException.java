package com.crossover.techtrial.exceptions;

public class ValidateException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	
	private ValidateException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public static ValidationExceptionBuilder of() {
		return new ValidationExceptionBuilder();
	}
	
	public static final class ValidationExceptionBuilder {
	
		private String message;
		private Throwable cause;
		
		public ValidationExceptionBuilder message(String message) {
			this.message = message;
			return this;
		}
		
		public ValidationExceptionBuilder cause(Throwable cause) {
			this.cause = cause;
			return this;
		}
		
		public ValidateException build() {
			return new ValidateException(message, cause);						
		}
	}

}
