package kdk.jwttutorial.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import kdk.jwttutorial.user.auth.Authority;
import kdk.jwttutorial.user.dto.UserDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@DataJpaTest
class UserRepositoryTest {

	@Autowired
	private UserRepository userRepository;
	private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	@Test
	void 회원가입() {
		// given
		User user = createUser("test1@test.com", "password", "test1");

		// when
		User result = userRepository.save(user);

		// then
		assertThat(user).isSameAs(result);
		assertThat(user.getEmail()).isEqualTo(result.getEmail());
		assertThat(user.getPassword()).isEqualTo(result.getPassword());
		assertThat(user.getNickname()).isEqualTo(result.getNickname());
		assertThat(user.getUserId()).isEqualTo(result.getUserId());
		assertThat(user.getAuthorities()).isEqualTo(result.getAuthorities());

	}

	@Test
	void 사용자_조회() {
		// given
		User user = createUser("test1@test.com", "password", "test1");
		userRepository.save(user);

		// when
		User result = userRepository.findOneWithAuthoritiesByEmail(user.getEmail()).orElse(null);

		// then
		assertThat(user).isSameAs(result);
		assertThat(user.getEmail()).isEqualTo(result.getEmail());
		assertThat(user.getPassword()).isEqualTo(result.getPassword());
		assertThat(user.getNickname()).isEqualTo(result.getNickname());
		assertThat(user.getUserId()).isEqualTo(result.getUserId());
		assertThat(user.getAuthorities()).isEqualTo(result.getAuthorities());
	}

	@Test
	void 사용자가_존재_True() {
		// given
		User user = createUser("test1@test.com", "password", "test1");
		userRepository.save(user);

		// when
		boolean result = userRepository.existsByEmail(user.getEmail());

		// then
		assertTrue(result);
	}

	@Test
	void 사용자가_존재_False() {
		// given
		User user = createUser("test1@test.com", "password", "test1");

		// when
		boolean result = userRepository.existsByEmail(user.getEmail());

		// then
		assertFalse(result);
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