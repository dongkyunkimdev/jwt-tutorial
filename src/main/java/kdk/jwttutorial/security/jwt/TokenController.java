package kdk.jwttutorial.security.jwt;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import kdk.jwttutorial.security.jwt.dto.TokenDto;
import kdk.jwttutorial.user.User;
import kdk.jwttutorial.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/token")
@RequiredArgsConstructor
@Log4j2
public class TokenController {

	private final TokenProvider tokenProvider;
	private final UserService userService;

	// 리프레시 토큰을 받음
	// 리프레시 토큰이 없거나, Bearer로 시작하지 않는다면 exception
	// 리프레시 토큰의 subject를 가져옴(email)
	// User 객체를 getByEmail
	// access_token을 다시 만들어서 리프레시 토큰과 함께 response
	@GetMapping("/refresh")
	public ResponseEntity<TokenDto> refreshToken(HttpServletRequest request, HttpServletResponse response) {
		String refreshToken = request.getHeader(JwtFilter.REFRESH_HEADER);
		if (StringUtils.hasText(refreshToken) && refreshToken.startsWith("Bearer ")) {
			refreshToken = refreshToken.substring(7);
		}
		if (!StringUtils.hasText(refreshToken) || !tokenProvider.validateToken(refreshToken)) {
			throw new RuntimeException("Refresh token is missing");
		}

		User user = userService.getUser(tokenProvider.getSubject(refreshToken));
		String accessToken = tokenProvider.createToken(user, EnumToken.ACCESS);
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + accessToken);
		httpHeaders.add(JwtFilter.REFRESH_HEADER, "Bearer " + refreshToken);

		return ResponseEntity.ok()
			.headers(httpHeaders)
			.body(TokenDto.builder()
				.accessToken(accessToken)
				.refreshToken(refreshToken)
				.build()
			);

	}
}
