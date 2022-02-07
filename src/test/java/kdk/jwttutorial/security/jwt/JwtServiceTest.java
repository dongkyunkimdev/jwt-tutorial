package kdk.jwttutorial.security.jwt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import kdk.jwttutorial.security.auth.Authority;
import kdk.jwttutorial.user.User;
import kdk.jwttutorial.user.UserRepository;
import kdk.jwttutorial.user.dto.LoginDto;
import kdk.jwttutorial.user.dto.UserDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

@DataJpaTest
@ComponentScan(basePackages = "kdk.jwttutorial.security, kdk.jwttutorial.user")
class JwtServiceTest {

	@Autowired
	private JwtService jwtService;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private TokenProvider tokenProvider;

	@Test
	void 액세스토큰_생성_성공() {
		// given
		String email = "test1@test.com";
		userRepository.save(createUser(email, "password", "test1"));

		// when
		String jwt = jwtService
			.getJwt(createLoginDto(email, "password"), EnumToken.ACCESS);

		// then
		MockHttpServletRequest request = new MockHttpServletRequest();
		assertTrue(tokenProvider.validateToken(request, jwt));
		assertThat(tokenProvider.getSubject(jwt)).isEqualTo(email);
	}

	@Test
	void 액세스토큰_생성_예외_계정정보_불일치() {
		// given
		String email = "test1@test.com";

		// when
		BadCredentialsException e = Assertions.assertThrows(BadCredentialsException.class,
			() -> jwtService.getJwt(createLoginDto(email, "password"), EnumToken.ACCESS)
		);

		// then
		assertThat(e.getMessage()).isEqualTo("자격 증명에 실패하였습니다.");
	}

	@Test
	void 리프레시토큰_생성_성공() {
		// given
		String email = "test1@test.com";
		userRepository.save(createUser(email, "password", "test1"));

		// when
		String jwt = jwtService
			.getJwt(createLoginDto(email, "password"), EnumToken.REFRESH);

		// then
		MockHttpServletRequest request = new MockHttpServletRequest();
		assertTrue(tokenProvider.validateToken(request, jwt));
		assertThat(tokenProvider.getSubject(jwt)).isEqualTo(email);
	}

	@Test
	void 리프레시토큰_생성_예외_계정정보_불일치() {
		// given
		String email = "test1@test.com";

		// when
		BadCredentialsException e = Assertions.assertThrows(BadCredentialsException.class,
			() -> jwtService.getJwt(createLoginDto(email, "password"), EnumToken.REFRESH)
		);

		// then
		assertThat(e.getMessage()).isEqualTo("자격 증명에 실패하였습니다.");
	}

	private LoginDto createLoginDto(String email, String password) {
		return LoginDto.builder()
			.email(email)
			.password(password)
			.build();
	}

	private User createUser(String email, String password, String nickname) {
		UserDto userDto = UserDto.builder()
			.email(email)
			.password(password)
			.nickname(nickname).build();

		User user = User.builder()
			.email(userDto.getEmail())
			.password(passwordEncoder.encode(userDto.getPassword()))
			.nickname(userDto.getNickname())
			.authorities(Collections.singleton(Authority.createUserRole()))
			.build();
		return user;
	}
}