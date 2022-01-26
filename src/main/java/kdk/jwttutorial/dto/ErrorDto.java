package kdk.jwttutorial.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.springframework.validation.FieldError;

@Getter
public class ErrorDto {
	private final int status;
	private final String message;
	private List<FieldErrorDto> fieldErrors = new ArrayList<>();

	public ErrorDto(int status, String message) {
		this.status = status;
		this.message = message;
	}

	public void addFieldError(String path, String message) {
		FieldErrorDto error = new FieldErrorDto(path, message);
		fieldErrors.add(error);
	}
}
