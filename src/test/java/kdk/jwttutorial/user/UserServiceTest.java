package kdk.jwttutorial.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collections;
import kdk.jwttutorial.error.ErrorCode;
import kdk.jwttutorial.user.auth.Authority;
import kdk.jwttutorial.user.dto.UserDto;
import kdk.jwttutorial.user.exception.EmailAlreadyUseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;

@DataJpaTest
class UserServiceTest {

	private UserService userService;
	@Autowired
	private UserRepository userRepository;
	private BCryptPasswordEncoder passwordEncoder;

	@BeforeEach
	void setUp() {
		passwordEncoder = new BCryptPasswordEncoder();
		userService = new UserService(userRepository, passwordEncoder);
	}

	@Test
	void 회원가입_성공() {
		// given
		UserDto userDto = createUserDto("test1@test.com", "password", "test1");

		// when
		UserDto result = userService.signup(userDto);

		// then
		assertThat(userDto.getEmail()).isEqualTo(result.getEmail());
		assertThat(userDto.getNickname()).isEqualTo(result.getNickname());
		assertThat(EnumAuthority.ROLE_USER.name())
			.isEqualTo(result.getAuthorityDtoSet().iterator().next().getAuthorityName());
	}

	@Test
	void 회원가입_예외_이메일_중복() {
		// given
		userRepository.save(createUser("test1@test.com", "password", "test1"));
		UserDto userDto = createUserDto("test1@test.com", "password", "test1");

		// when
		EmailAlreadyUseException e = assertThrows(
			EmailAlreadyUseException.class, () -> userService.signup(userDto));

		// then
		assertThat(e.getMessage()).isEqualTo(ErrorCode.EMAIL_DUPLICATION.getMessage());
	}

	@Test
	void 사용자_조회_성공() {
		// given
		String email = "test1@test.com";
		User user = userRepository.save(createUser(email, "password", "test1"));

		// when
		UserDto result = userService.getUserWithAuthorities(email);

		// then
		assertThat(user.getEmail()).isEqualTo(result.getEmail());
		assertThat(user.getNickname()).isEqualTo(result.getNickname());
		assertThat(user.getAuthorities().iterator().next().getAuthorityName())
			.isEqualTo(result.getAuthorityDtoSet().iterator().next().getAuthorityName());
	}

	@Test
	void 사용자_조회_예외_사용자가_없음() {
		// given
		String email = "test1@test.com";

		// when
		UsernameNotFoundException e = assertThrows(UsernameNotFoundException.class,
			() -> userService.getUserWithAuthorities(email));

		// then
		assertThat(e.getMessage()).isEqualTo(ErrorCode.USER_NOT_FOUND.getMessage());
	}

	@Test
	@WithMockUser(username = "test1@test.com", authorities = {"ROLE_USER"})
	void 내정보_조회_성공() {
		// given
		userRepository.save(createUser("test1@test.com", "password", "test1"));
		UserDetails springSecurityUser = (UserDetails) SecurityContextHolder.getContext()
			.getAuthentication()
			.getPrincipal();
		String email = springSecurityUser.getUsername();
		String authority = springSecurityUser.getAuthorities().iterator().next().getAuthority();

		// when
		UserDto result = userService.getMyUserWithAuthorities();

		// then
		assertThat(email).isEqualTo(result.getEmail());
		assertThat(authority)
			.isEqualTo(result.getAuthorityDtoSet().iterator().next().getAuthorityName());
	}

	@Test
	@WithMockUser(username = "test1@test.com", authorities = {"ROLE_USER"})
	void 내정보_조회_예외_사용자가_없음() {
		// given
		UserDetails springSecurityUser = (UserDetails) SecurityContextHolder.getContext()
			.getAuthentication()
			.getPrincipal();

		// when
		UsernameNotFoundException e = assertThrows(
			UsernameNotFoundException.class, () -> userService.getMyUserWithAuthorities());

		// then
		assertThat(e.getMessage()).isEqualTo(ErrorCode.USER_NOT_FOUND.getMessage());
	}

	private UserDto createUserDto(String email, String password, String nickname) {
		UserDto userDto = UserDto.builder()
			.email(email)
			.password(password)
			.nickname(nickname).build();
		return userDto;
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