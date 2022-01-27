package kdk.jwttutorial.exception.handler;

import static org.springframework.http.HttpStatus.CONFLICT;

import kdk.jwttutorial.exception.DuplicateMemberException;
import kdk.jwttutorial.exception.dto.ErrorDto;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

	@ResponseStatus(CONFLICT)
	@ExceptionHandler(value = {DuplicateMemberException.class})
	@ResponseBody
	protected ErrorDto badRequest(RuntimeException ex, WebRequest request) {
		return ErrorDto.builder()
			.status(CONFLICT.value())
			.message(ex.getMessage())
			.build();
	}
}
