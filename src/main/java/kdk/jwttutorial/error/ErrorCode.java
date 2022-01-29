package kdk.jwttutorial.error;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Getter
public enum ErrorCode {

	// Common
	INVALID_INPUT_VALUE(400, "C001", "유효하지 않은 파라미터 값입니다"),
	INVALID_TYPE_VALUE(400, "C002", "유효하지 않은 파라미터 타입입니다"),
	MESSAGE_NOT_READABLE(400, "C003", "유효하지 않은 포맷입니다"),
	ENTITY_NOT_FOUND(400, "C004", "결과가 존재하지 않습니다"),
	METHOD_NOT_ALLOWED(405, "C005", "허용되지 않은 메서드입니다"),
	HANDLE_ACCESS_DENIED(403, "C006", "액세스가 거부되었습니다"),
	INTERNAL_SERVER_ERROR(500, "C007", "Server Error"),

	// User
	EMAIL_DUPLICATION(400, "U001", "이미 사용중인 이메일입니다"),
	USER_NOT_FOUND(400, "U002", "존재하지 않는 사용자입니다"),

	// Authentication
	INVALID_TOKEN(401, "A001", "유효하지 않은 JWT 토큰입니다"),
	EXPIRED_TOKEN(401, "A002", "만료된 JWT 토큰입니다"),
	UNSUPPORTED_TOKEN(401, "A003", "지원되지 않는 JWT 토큰입니다"),
	INCORRECT_SIGNATURE(401, "A004", "유효하지 않은 JWT 서명입니다"),
	UNKNOWN_TOKEN(401, "A005", "알 수 없는 오류입니다"),

	// Login
	INVALID_ACCOUNT(400, "L001", "계정 정보가 일치하지 않습니다.");

	private int status;
	private final String code;
	private final String message;

	ErrorCode(final int status, final String code, final String message) {
		this.status = status;
		this.message = message;
		this.code = code;
	}

}
