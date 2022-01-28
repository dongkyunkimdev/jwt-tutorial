package kdk.jwttutorial.error.exception;

import kdk.jwttutorial.error.ErrorCode;

public class EntityNotFoundException extends BusinessException {

	public EntityNotFoundException(String message) {
		super(message, ErrorCode.ENTITY_NOT_FOUND);
	}
}
