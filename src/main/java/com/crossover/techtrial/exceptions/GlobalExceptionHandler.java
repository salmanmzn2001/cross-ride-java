package com.crossover.techtrial.exceptions;

import java.time.format.DateTimeParseException;
import java.util.AbstractMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ControllerAdvice
@Component
public class GlobalExceptionHandler {

	private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);
	private static final String MESSAGE = "message";  

	/**
	 * Global Exception handler for all exceptions.
	 */
	@ExceptionHandler
	public ResponseEntity<AbstractMap.SimpleEntry<String, String>> handle(Exception exception) {
		// general exception
		LOG.error("Exception: Unable to process this request. ", exception);
		 AbstractMap.SimpleEntry<String, String> response = new AbstractMap.SimpleEntry<>(MESSAGE, "Unable to process this request.");	    
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	}

	/**
	 *  Exception handler for Validate exception (Explicitly thrown exceptions).
	 */
	@ExceptionHandler(ValidateException.class)
	public ResponseEntity<AbstractMap.SimpleEntry<String, String>> handle(ValidateException ve) {
		LOG.error("ValidateException: ", ve.getMessage());
		AbstractMap.SimpleEntry<String, String> response = new AbstractMap.SimpleEntry<>(MESSAGE, ve.getMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	}

	/**
	 *  Exception handler for Request method argument Validation.
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<AbstractMap.SimpleEntry<String, String>> validationError(MethodArgumentNotValidException ex) {
		BindingResult result = ex.getBindingResult();
		final List<FieldError> fieldErrors = result.getFieldErrors();
		String message = "Bad request - Input validation error.";

		if (null != fieldErrors && !fieldErrors.isEmpty()) {
			FieldError fieldError = fieldErrors.get(0);

			message = "Invalid request. "  + "Field : "
					+ fieldError.getField() + " - " + fieldError.getDefaultMessage();

		}
		LOG.error("MethodArgumentNotValidException : ", message);
		AbstractMap.SimpleEntry<String, String> response = new AbstractMap.SimpleEntry<>(MESSAGE, message);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

	}
	
	/**
	 *  Exception handler for  HttpMessageNotReadableException (Field Format Errors).
	 */
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<AbstractMap.SimpleEntry<String, String>> handle(HttpMessageNotReadableException ex) {
		String message=null;
		if(ex.getCause().getCause() instanceof DateTimeParseException) {		
			message="Invalid date Format error. Accepted date format is : yyyy-MM-dd'T'HH:mm:ss .";
			LOG.error("DateTimeParseException : ", message);
		}else {		
			message="Bad request - Field format error. Validate inputs and try again.";
			LOG.error(message, ex);
		}
		
		AbstractMap.SimpleEntry<String, String> response = new AbstractMap.SimpleEntry<>(MESSAGE, message);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	}
	
	/**
	 *  Exception handler for  MethodArgumentTypeMismatchException (Field type mismatch Errors).
	 */
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<AbstractMap.SimpleEntry<String, String>> handle(MethodArgumentTypeMismatchException ex) {
		String message="Bad request - Field: "+ex.getName()+" - Fieldtype Mismatch error. Validate inputs and try again.";
		LOG.error(message, ex);		
		AbstractMap.SimpleEntry<String, String> response = new AbstractMap.SimpleEntry<>(MESSAGE, message);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	}
	
	/**
	 *  Exception handler for  HttpRequestMethodNotSupportedException.
	 */
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ResponseEntity<AbstractMap.SimpleEntry<String, String>> handle(HttpRequestMethodNotSupportedException ex) {
		String message= ex.getMethod() + " method not supported. Validate method and try again.";
		LOG.error(message, ex);		
		AbstractMap.SimpleEntry<String, String> response = new AbstractMap.SimpleEntry<>(MESSAGE, message);
		return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(response);
	}
	
	@ExceptionHandler(IllegalStateException.class)
	public ResponseEntity<AbstractMap.SimpleEntry<String, String>> handle(IllegalStateException ex) {
		String message= ex.getMessage();
		LOG.error(message, ex);		
		AbstractMap.SimpleEntry<String, String> response = new AbstractMap.SimpleEntry<>(MESSAGE, message);
		return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(response);
	}
	
	
	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ResponseEntity<AbstractMap.SimpleEntry<String, String>> handle(MissingServletRequestParameterException ex) {
		String message= ex.getMessage();
		LOG.error(message, ex);		
		AbstractMap.SimpleEntry<String, String> response = new AbstractMap.SimpleEntry<>(MESSAGE, message);
		return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(response);
	}
}
