package kdk.jwttutorial.security.jwt;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Log4j2
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

	public static final String AUTHORIZATION_HEADER = "Authorization";
	public static final String REFRESH_HEADER = "Refresh";

	private final TokenProvider tokenProvider;

	// JWT 인증 정보를 SecurityContext에 저장
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain chain) throws ServletException, IOException {
		if (ignoreFilterUrl(request)) {
			chain.doFilter(request, response);
			return;
		}

		String jwt = resolveToken(request);
		String requestURI = request.getRequestURI();

		if (StringUtils.hasText(jwt) && tokenProvider.validateToken(request, jwt)) {
			Authentication authentication = tokenProvider.getAuthentication(jwt);
			SecurityContextHolder.getContext().setAuthentication(authentication);
			log.debug("Security Context에 '{}' 인증 정보를 저장했습니다, uri: {}", authentication.getName(),
				requestURI);
		} else {
			log.debug("유효한 JWT 토큰이 없습니다, uri: {}", requestURI);
		}

		chain.doFilter(request, response);
	}

	private boolean ignoreFilterUrl(HttpServletRequest request) {
		if (request.getServletPath().equals("/user/login") || request.getServletPath()
			.equals("/user/signup") || request.getServletPath().equals("/token/refresh")
			|| request.getServletPath().contains("/swagger-resource") || request.getServletPath()
			.equals("/v3/api-docs")) {

			return true;
		}
		return false;
	}

	private String resolveToken(HttpServletRequest request) {
		String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7);
		}
		return null;
	}
}
