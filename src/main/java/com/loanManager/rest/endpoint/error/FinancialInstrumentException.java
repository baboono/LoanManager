package com.loanManager.rest.endpoint.error;
/**
 * Custom exception class
 * 
 * @author Rados³aw Ba³trukiewicz
 * 
 */
public class FinancialInstrumentException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5442581635323952821L;

	private String field; 

	private String value; 
	
	private String message;

	public FinancialInstrumentException(String field, String value, String message) {
		this.field = field;
		this.value = value;
		this.message = message;
	}

	public FinancialInstrumentException(String message) {
		this.message = message;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	} 
}
