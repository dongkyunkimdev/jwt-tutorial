package kdk.jwttutorial.exception.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ErrorDto {

	private final int status;
	private final String message;
	private List<FieldErrorDto> fieldErrors = new ArrayList<>();

	@Builder
	public ErrorDto(int status, String message) {
		this.status = status;
		this.message = message;
	}

	public void addFieldError(String path, String message) {
		fieldErrors.add(FieldErrorDto.builder()
			.field(path)
			.message(message)
			.build()
		);
	}
}
