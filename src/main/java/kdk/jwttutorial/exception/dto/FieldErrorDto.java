package kdk.jwttutorial.exception.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
final class FieldErrorDto {

	private final String field;

	private final String message;

	@Builder
	public FieldErrorDto(String field, String message) {
		this.field = field;
		this.message = message;
	}
}
