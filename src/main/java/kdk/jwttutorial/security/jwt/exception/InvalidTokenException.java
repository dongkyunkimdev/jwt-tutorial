package kdk.jwttutorial.security.jwt.exception;

import kdk.jwttutorial.error.ErrorCode;
import kdk.jwttutorial.error.exception.InvalidValueException;

public class InvalidTokenException extends InvalidValueException {

	public InvalidTokenException(String value) {
		super(value, ErrorCode.INVALID_TOKEN);
	}
}
