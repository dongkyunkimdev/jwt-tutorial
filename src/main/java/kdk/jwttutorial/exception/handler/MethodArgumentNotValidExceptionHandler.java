package kdk.jwttutorial.exception.handler;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import java.util.List;
import kdk.jwttutorial.exception.dto.ErrorDto;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class MethodArgumentNotValidExceptionHandler {

	@ResponseStatus(BAD_REQUEST)
	@ResponseBody
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ErrorDto methodArgumentNotValidException(MethodArgumentNotValidException ex) {
		BindingResult result = ex.getBindingResult();
		List<FieldError> fieldErrors = result.getFieldErrors();
		return processFieldErrors(fieldErrors);
	}

	private ErrorDto processFieldErrors(List<FieldError> fieldErrors) {
		ErrorDto errorDTO = ErrorDto.builder()
			.status(BAD_REQUEST.value())
			.message("@Valid Error")
			.build();
		for (FieldError fieldError : fieldErrors) {
			errorDTO.addFieldError(fieldError.getField(), fieldError.getDefaultMessage());
		}

		return errorDTO;
	}
}