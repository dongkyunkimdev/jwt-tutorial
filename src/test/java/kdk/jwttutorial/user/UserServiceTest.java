package kdk.jwttutorial.user;

import static java.util.Optional.ofNullable;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;

import java.util.Collections;
import java.util.Optional;
import kdk.jwttutorial.error.ErrorCode;
import kdk.jwttutorial.security.SecurityUtil;
import kdk.jwttutorial.user.auth.Authority;
import kdk.jwttutorial.user.dto.UserDto;
import kdk.jwttutorial.user.exception.EmailAlreadyUseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

class UserServiceTest {

	@InjectMocks
	private UserService userService;
	@Mock
	private UserRepository userRepository;
	private BCryptPasswordEncoder passwordEncoder;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		passwordEncoder = new BCryptPasswordEncoder();
		userService = new UserService(userRepository, passwordEncoder);
	}

	@Test
	void 회원가입_성공() {
		// given
		String email = "test1@test.com";
		String nickname = "test1";
		given(userRepository.existsByEmail(any())).willReturn(false);
		given(userRepository.save(any())).willReturn(
			createUser(email, "password", nickname)
		);
		UserDto userDto = createUserDto(email, "password", nickname);

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
		given(userRepository.existsByEmail(any())).willReturn(true);
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
		String nickname = "test";
		given(userRepository.findOneWithAuthoritiesByEmail(any())).willReturn(
			ofNullable(createUser(email, "password", nickname))
		);

		// when
		UserDto result = userService.getUserWithAuthorities(email);

		// then
		assertThat(email).isEqualTo(result.getEmail());
		assertThat(nickname).isEqualTo(result.getNickname());
		assertThat(Authority.createUserRole().getAuthorityName())
			.isEqualTo(result.getAuthorityDtoSet().iterator().next().getAuthorityName());
	}

	@Test
	void 사용자_조회_예외_사용자가_없음() {
		// given
		String email = "test1@test.com";
		given(userRepository.findOneWithAuthoritiesByEmail(any()))
			.willReturn(Optional.ofNullable(null));

		// when
		UsernameNotFoundException e = assertThrows(UsernameNotFoundException.class,
			() -> userService.getUserWithAuthorities(email));

		// then
		assertThat(e.getMessage()).isEqualTo(ErrorCode.USER_NOT_FOUND.getMessage());
	}

	@Test
	void 내정보_조회_성공() {
		// given
		MockedStatic<SecurityUtil> mockSecurityUtil = mockStatic(SecurityUtil.class);
		String email = "test1@test.com";
		String nickname = "test1";
		given(userRepository.findOneWithAuthoritiesByEmail(any())).willReturn(
			ofNullable(createUser(email, "password", nickname))
		);
		given(SecurityUtil.getCurrentUsername()).willReturn(Optional.ofNullable(email));

		// when
		UserDto result = userService.getMyUserWithAuthorities();

		// then
		assertThat(email).isEqualTo(result.getEmail());
		assertThat(nickname).isEqualTo(result.getNickname());
		assertThat(Authority.createUserRole().getAuthorityName())
			.isEqualTo(result.getAuthorityDtoSet().iterator().next().getAuthorityName());

		// end
		mockSecurityUtil.close();
	}

	@Test
	void 내정보_조회_예외_사용자가_없음() {
		// given
		MockedStatic<SecurityUtil> mockSecurityUtil = mockStatic(SecurityUtil.class);
		String email = "test1@test.com";
		given(SecurityUtil.getCurrentUsername()).willReturn(Optional.ofNullable(email));
		given(userRepository.findOneWithAuthoritiesByEmail(any()))
			.willReturn(Optional.ofNullable(null));

		// when
		UsernameNotFoundException e = assertThrows(
			UsernameNotFoundException.class, () -> userService.getMyUserWithAuthorities()
		);

		// then
		assertThat(e.getMessage()).isEqualTo(ErrorCode.USER_NOT_FOUND.getMessage());

		// end
		mockSecurityUtil.close();
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