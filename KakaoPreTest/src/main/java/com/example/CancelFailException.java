package com.example;

public class CancelFailException extends RuntimeException {
	private String code;

	public CancelFailException(String message, String code) {
		super(message);
		this.code = code;
	}

	public String getCode() {
		return code;
	}
}
