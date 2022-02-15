package kdk.jwttutorial.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collections;
import kdk.jwttutorial.error.ErrorCode;
import kdk.jwttutorial.user.User;
import kdk.jwttutorial.user.UserRepository;
import kdk.jwttutorial.user.auth.Authority;
import kdk.jwttutorial.user.dto.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class CustomUserDetailsServiceTest {

	@Autowired
	private CustomUserDetailsService customUserDetailsService;
	@Autowired
	private UserRepository userRepository;
	private BCryptPasswordEncoder passwordEncoder;

	@BeforeEach
	void setUp() {
		passwordEncoder = new BCryptPasswordEncoder();
	}

	@Test
	void 이메일로_사용자_조회_성공() {
		// given
		String email = "test1@test.com";
		userRepository.save(createUser(email, "password", "test1"));

		// when
		UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

		// then
		assertThat(userDetails.getUsername()).isEqualTo(email);
	}

	@Test
	void 이메일로_사용자_조회_예외_사용자가_없음() {
		// given
		String email = "test1@test.com";

		// when
		UsernameNotFoundException e = assertThrows(
			UsernameNotFoundException.class,
			() -> customUserDetailsService.loadUserByUsername(email)
		);

		// then
		assertThat(e.getMessage()).isEqualTo(ErrorCode.USER_NOT_FOUND.getMessage());
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