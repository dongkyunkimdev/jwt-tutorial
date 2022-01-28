package kdk.jwttutorial.user;

import java.util.Collections;
import kdk.jwttutorial.error.ErrorCode;
import kdk.jwttutorial.security.SecurityUtil;
import kdk.jwttutorial.security.auth.Authority;
import kdk.jwttutorial.user.dto.UserDto;
import kdk.jwttutorial.user.exception.EmailAlreadyUseException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Transactional
	public UserDto signup(UserDto userDto) {
		if (userRepository.findOneWithAuthoritiesByEmail(userDto.getEmail()).orElse(null)
			!= null) {
			throw new EmailAlreadyUseException(ErrorCode.EMAIL_DUPLICATION.getMessage());
		}

		Authority authority = Authority.builder()
			.authorityName("ROLE_USER")
			.build();

		User user = User.builder()
			.username(userDto.getEmail())
			.password(passwordEncoder.encode(userDto.getPassword()))
			.nickname(userDto.getNickname())
			.authorities(Collections.singleton(authority))
			.build();

		return UserDto.from(userRepository.save(user));
	}

	@Transactional(readOnly = true)
	public UserDto getUserWithAuthorities(String username) {
		return UserDto.from(userRepository.findOneWithAuthoritiesByEmail(username).orElseThrow(
			() -> new UsernameNotFoundException(ErrorCode.USER_NOT_FOUND.getMessage()))
		);
	}

	@Transactional(readOnly = true)
	public UserDto getMyUserWithAuthorities() {
		return UserDto.from(SecurityUtil.getCurrentUsername()
			.flatMap(userRepository::findOneWithAuthoritiesByEmail).orElseThrow(
				() -> new UsernameNotFoundException(ErrorCode.USER_NOT_FOUND.getMessage()))
		);
	}

	@Transactional(readOnly = true)
	public User getUser(String email) {
		return userRepository.findOneWithAuthoritiesByEmail(email).orElseThrow(
			() -> new UsernameNotFoundException(ErrorCode.USER_NOT_FOUND.getMessage())
		);
	}

}