package kdk.jwttutorial.security.jwt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;
import kdk.jwttutorial.error.ErrorCode;
import kdk.jwttutorial.security.auth.dto.AuthorityDto;
import kdk.jwttutorial.security.jwt.exception.InvalidTokenException;
import kdk.jwttutorial.user.EnumAuthority;
import kdk.jwttutorial.user.UserService;
import kdk.jwttutorial.user.dto.UserDto;
import org.assertj.core.api.AbstractThrowableAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.filter.CharacterEncodingFilter;

@WebMvcTest(TokenController.class)
@AutoConfigureDataJpa
@ComponentScan(basePackages = "kdk.jwttutorial.security")
@Transactional
@AutoConfigureTestDatabase
class TokenControllerTest {

	@Autowired
	private MockMvc mvc;
	@MockBean
	private TokenProvider tokenProvider;
	@MockBean
	private UserService userService;

	@BeforeEach
	void setUp() {
		mvc = MockMvcBuilders.standaloneSetup(new TokenController(tokenProvider, userService))
			.addFilters(new CharacterEncodingFilter("UTF-8", true))
			.build();
	}

	@Test
	void 토큰_재발급_성공() throws Exception {
		// given
		String email = "test1@test.com";
		String accessToken = "accessToken";
		String refreshToken = "refreshToken";
		given(tokenProvider.validateToken(any(), any())).willReturn(true);
		given(tokenProvider.getSubject(any())).willReturn(email);
		given(userService.getUserWithAuthorities(email)).willReturn(
			createUserDto(email, "password", "test1")
		);
		given(tokenProvider.createToken((UserDto) any(), eq(EnumToken.ACCESS)))
			.willReturn(accessToken);

		// when
		String requestUrl = "/token/refresh";
		ResultActions actions = mvc.perform(
			get(requestUrl)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.characterEncoding("UTF-8")
				.header(JwtFilter.REFRESH_HEADER, "Bearer " + refreshToken)
		);

		// then
		actions
			.andExpect(status().isOk())
			.andExpect(header().string(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + accessToken))
			.andExpect(header().string(JwtFilter.REFRESH_HEADER, "Bearer " + refreshToken))
			.andExpect(jsonPath("accessToken").value(accessToken))
			.andExpect(jsonPath("refreshToken").value(refreshToken));
	}

	@Test
	void 토큰_재발급_예외_유효하지않음() throws Exception {
		// given
		String refreshToken = "refreshToken";
		given(tokenProvider.validateToken(any(), any())).willReturn(false);

		// when
		String requestUrl = "/token/refresh";
		AbstractThrowableAssert<?, ? extends Throwable> o = assertThatThrownBy(
			() ->
				mvc.perform(
					get(requestUrl)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON)
						.characterEncoding("UTF-8")
						.header(JwtFilter.REFRESH_HEADER, "Bearer " + refreshToken)
				)
		).hasCause(new InvalidTokenException(ErrorCode.INVALID_TOKEN.getMessage()));

		// then
		o.getCause().isInstanceOf(InvalidTokenException.class);
	}

	private UserDto createUserDto(String email, String password, String nickname) {
		return UserDto.builder()
			.email(email)
			.password(password)
			.nickname(nickname)
			.authorityDtoSet(
				Collections.singleton(
					AuthorityDto.builder()
						.authorityName(EnumAuthority.ROLE_USER.name())
						.build()
				)
			)
			.build();
	}
}