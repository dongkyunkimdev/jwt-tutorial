package kdk.jwttutorial.security.jwt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import kdk.jwttutorial.error.ErrorCode;
import kdk.jwttutorial.security.auth.Authority;
import kdk.jwttutorial.security.auth.dto.AuthorityDto;
import kdk.jwttutorial.user.EnumAuthority;
import kdk.jwttutorial.user.User;
import kdk.jwttutorial.user.UserRepository;
import kdk.jwttutorial.user.dto.LoginDto;
import kdk.jwttutorial.user.dto.UserDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

@DataJpaTest
@ComponentScan(basePackages = "kdk.jwttutorial.security, kdk.jwttutorial.user")
class TokenProviderTest {

	@Autowired
	private TokenProvider tokenProvider;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private AuthenticationManagerBuilder authenticationManagerBuilder;
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Test
	void Authentication객체로_액세스토큰_생성_성공() {
		// given
		userRepository.save(createUser("test1@test.com", "password", "test1"));
		LoginDto loginDto = createLoginDto("test1@test.com", "password");
		UsernamePasswordAuthenticationToken authenticationToken =
			new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword());
		Authentication authentication = authenticationManagerBuilder.getObject()
			.authenticate(authenticationToken);

		// when
		String token = tokenProvider.createToken(authentication, EnumToken.ACCESS);

		// then
		MockHttpServletRequest request = new MockHttpServletRequest();
		assertTrue(tokenProvider.validateToken(request, token));
		assertThat(tokenProvider.getSubject(token)).isEqualTo(loginDto.getEmail());
	}

	@Test
	void Authentication객체로_리프레시토큰_생성_성공() {
		// given
		userRepository.save(createUser("test1@test.com", "password", "test1"));
		LoginDto loginDto = createLoginDto("test1@test.com", "password");
		UsernamePasswordAuthenticationToken authenticationToken =
			new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword());
		Authentication authentication = authenticationManagerBuilder.getObject()
			.authenticate(authenticationToken);

		// when
		String token = tokenProvider.createToken(authentication, EnumToken.REFRESH);

		// then
		MockHttpServletRequest request = new MockHttpServletRequest();
		assertTrue(tokenProvider.validateToken(request, token));
		assertThat(tokenProvider.getSubject(token)).isEqualTo(loginDto.getEmail());
	}

	@Test
	void UserDto객체로_액세스토큰_생성_성공() {
		// given
		UserDto userDto = createUserDto("test1@test.com", "password", "test1");

		// when
		String token = tokenProvider.createToken(userDto, EnumToken.ACCESS);

		// then
		MockHttpServletRequest request = new MockHttpServletRequest();
		assertTrue(tokenProvider.validateToken(request, token));
		assertThat(tokenProvider.getSubject(token)).isEqualTo(userDto.getEmail());
	}

	@Test
	void UserDto객체로_리프레시토큰_생성_성공() {
		// given
		UserDto userDto = createUserDto("test1@test.com", "password", "test1");

		// when
		String token = tokenProvider.createToken(userDto, EnumToken.REFRESH);

		// then
		MockHttpServletRequest request = new MockHttpServletRequest();
		assertTrue(tokenProvider.validateToken(request, token));
		assertThat(tokenProvider.getSubject(token)).isEqualTo(userDto.getEmail());
	}

	@Test
	void 토큰으로_Authentication_반환_성공() {
		// given
		userRepository.save(createUser("test1@test.com", "password", "test1"));
		UserDto userDto = createUserDto("test1@test.com", "password", "test1");
		String token = tokenProvider.createToken(userDto, EnumToken.ACCESS);

		// when
		Authentication authentication = tokenProvider.getAuthentication(token);

		// then
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
			userDto.getEmail(), userDto.getPassword());
		Authentication authentication1 = authenticationManagerBuilder.getObject()
			.authenticate(authenticationToken);
		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		UserDetails userDetails1 = (UserDetails) authentication1.getPrincipal();
		assertThat(userDetails1.getUsername()).isEqualTo(userDetails.getUsername());
		assertThat(userDetails1.getAuthorities()).isEqualTo(userDetails.getAuthorities());
		assertThat(userDetails1).isEqualTo(userDetails);
	}

	@Test
	void 토큰_검증_성공() {
		// given
		UserDto userDto = createUserDto("test1@test.com", "password", "test1");
		String token = tokenProvider.createToken(userDto, EnumToken.ACCESS);

		// when
		MockHttpServletRequest request = new MockHttpServletRequest();
		boolean result = tokenProvider.validateToken(request, token);

		// then
		assertTrue(result);
	}

	@Test
	void 토큰_검증_예외_유효하지않은_서명() {
		// given
		String incorrectToken = "eyJ0eXAOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJhdXRoIjoiUk9MRV9VU0VSIiwiaXNzIjoia2RrIiwic3ViIjoia2RrQGtkay5jb20iLCJleHAiOjE2NDQyMTUwNDcsImlhdCI6MTY0NDIxNDQ0N30.UfH9kKM6eSLAtQ_zIILzOUVlVEAayPCm3_exQ32n43qe5IiV4TLYFsIlgQBGP8gzLd2UmQlcTlzpdiDCY0XDsA";
		MockHttpServletRequest request = new MockHttpServletRequest();

		// when
		tokenProvider.validateToken(request, incorrectToken);

		// then
		assertThat(request.getAttribute("exception"))
			.isEqualTo(ErrorCode.INCORRECT_SIGNATURE.getCode());
	}

	@Test
	void 토큰_검증_예외_만료됨() {
		// given
		String expiredToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJhdXRoIjoiUk9MRV9VU0VSIiwiaXNzIjoia2RrIiwic3ViIjoia2RrQGtkay5jb20iLCJleHAiOjE2NDQyMTUwNDcsImlhdCI6MTY0NDIxNDQ0N30.UfH9kKM6eSLAtQ_zIILzOUVlVEAayPCm3_exQ32n43qe5IiV4TLYFsIlgQBGP8gzLd2UmQlcTlzpdiDCY0XDsA";
		MockHttpServletRequest request = new MockHttpServletRequest();

		// when
		tokenProvider.validateToken(request, expiredToken);

		// then
		assertThat(request.getAttribute("exception")).isEqualTo(ErrorCode.EXPIRED_TOKEN.getCode());
	}

	@Test
	void 토큰으로_이메일_반환_성공() {
		// given
		UserDto userDto = createUserDto("test1@test.com", "password", "test1");
		String token = tokenProvider.createToken(userDto, EnumToken.ACCESS);

		// when
		String result = tokenProvider.getSubject(token);

		// then
		assertThat(userDto.getEmail()).isEqualTo(result);
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