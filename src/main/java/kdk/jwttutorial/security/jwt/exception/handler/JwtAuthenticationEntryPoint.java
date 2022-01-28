package kdk.jwttutorial.security.jwt.exception.handler;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import kdk.jwttutorial.error.ErrorCode;
import kdk.jwttutorial.error.ErrorResponse;
import org.json.simple.JSONObject;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException authException) throws IOException {
		String exception = (String) request.getAttribute("exception");

		if (exception == null) {
			setResponse(response, ErrorCode.UNKNOWN_TOKEN);
		} else if (exception.equals(ErrorCode.INCORRECT_SIGNATURE.getCode())) {
			setResponse(response, ErrorCode.INCORRECT_SIGNATURE);
		} else if (exception.equals(ErrorCode.EXPIRED_TOKEN.getCode())) {
			setResponse(response, ErrorCode.EXPIRED_TOKEN);
		} else if (exception.equals(ErrorCode.UNSUPPORTED_TOKEN.getCode())) {
			setResponse(response, ErrorCode.UNSUPPORTED_TOKEN);
		} else if (exception.equals(ErrorCode.INVALID_TOKEN.getCode())) {
			setResponse(response, ErrorCode.INVALID_TOKEN);
		} else {
			setResponse(response, ErrorCode.UNKNOWN_TOKEN);
		}
	}

	private void setResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {
		response.setContentType("application/json;charset=UTF-8");
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		final ErrorResponse errorResponse = ErrorResponse.of(errorCode);
		JSONObject responseJson = new JSONObject();
		responseJson.put("message", errorResponse.getMessage());
		responseJson.put("status", errorResponse.getStatus());
		responseJson.put("errors", errorResponse.getErrors());
		responseJson.put("code", errorResponse.getCode());

		response.getWriter().print(responseJson);
	}
}
