package kdk.jwttutorial.user.exception;

import kdk.jwttutorial.error.ErrorCode;
import kdk.jwttutorial.error.exception.InvalidValueException;

public class EmailAlreadyUseException extends InvalidValueException {

	public EmailAlreadyUseException(String value) {
		super(value, ErrorCode.EMAIL_DUPLICATION);
	}
}