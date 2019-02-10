package com.loanManager.rest.endpoint;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.loanManager.rest.endpoint.error.FinancialInstrumentException;
/**
 *Exception handler class
 * @author Rados³aw Ba³trukiewicz
 * 
 */
public abstract class BaseEndpoint {

	@Autowired
	protected MessageSource messageSource;

	@ExceptionHandler
	protected ResponseEntity<?> handleBindException(BindException exception) {
		return ResponseEntity.badRequest().body(exception.getMessage());
	}

	/**
	 * Exception handler for validation errors caused by method
	 * parameters @RequesParam, @PathVariable, @RequestHeader annotated with
	 * javax.validation constraints.
	 */
	@ExceptionHandler
	protected ResponseEntity<?> handleConstraintViolationException(ConstraintViolationException exception) {

		List<FinancialInstrumentException> errors = new ArrayList<>();

		for (ConstraintViolation<?> violation : exception.getConstraintViolations()) {
			String value = (violation.getInvalidValue() == null ? null : violation.getInvalidValue().toString());
			errors.add(new FinancialInstrumentException(violation.getPropertyPath().toString(), value,
					violation.getMessage()));
		}

		return ResponseEntity.badRequest().body("Header parameter(s) not withing range. " + exception.getMessage());
	}

	/**
	 * Exception handler for @RequestBody loan validation errors.
	 */
	@ExceptionHandler
	protected ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {

		return ResponseEntity.badRequest().body(exception.getMessage());
	}

	/**
	 * Exception handler for missing required parameters errors.
	 */
	@ExceptionHandler
	protected ResponseEntity<?> handleServletRequestBindingException(ServletRequestBindingException exception) {

		return ResponseEntity.badRequest().body(exception.getMessage());
	}

	/**
	 * Exception handler for invalid payload (e.g. json invalid format error).
	 */
	@ExceptionHandler
	protected ResponseEntity<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException exception) {

		return ResponseEntity.badRequest().body(exception.getMessage());
	}

	@ExceptionHandler
	protected ResponseEntity<?> handleNullPointerException(NullPointerException exception) {

		return ResponseEntity.badRequest().body(exception.getMessage());
	}

	protected List<FinancialInstrumentException> convert(List<ObjectError> objectErrors) {

		List<FinancialInstrumentException> errors = new ArrayList<>();

		for (ObjectError objectError : objectErrors) {

			String message = objectError.getDefaultMessage();
			if (message == null) {
				// when using custom spring validator org.springframework.validation.Validator
				// need to resolve messages manually
				message = messageSource.getMessage(objectError, null);
			}

			FinancialInstrumentException error = null;
			if (objectError instanceof FieldError) {
				FieldError fieldError = (FieldError) objectError;
				String value = (fieldError.getRejectedValue() == null ? null
						: fieldError.getRejectedValue().toString());
				error = new FinancialInstrumentException(fieldError.getField(), value, message);
			} else {
				error = new FinancialInstrumentException(objectError.getObjectName(), objectError.getCode(),
						objectError.getDefaultMessage());
			}

			errors.add(error);
		}

		return errors;
	}

}
