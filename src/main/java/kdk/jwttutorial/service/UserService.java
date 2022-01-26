package kdk.jwttutorial.service;

import java.util.Collections;
import java.util.Optional;
import kdk.jwttutorial.dto.UserDto;
import kdk.jwttutorial.entity.Authority;
import kdk.jwttutorial.entity.User;
import kdk.jwttutorial.repository.UserRepository;
import kdk.jwttutorial.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Transactional
	public User signup(UserDto userDto) {
		if (userRepository.findOneWithAuthoritiesByUsername(userDto.getUsername()).orElse(null)
			!= null) {
			throw new RuntimeException("이미 가입되어 있는 유저입니다.");
		}

		Authority authority = Authority.builder()
			.authorityName("ROLE_USER")
			.build();

		User user = User.builder()
			.username(userDto.getUsername())
			.password(passwordEncoder.encode(userDto.getPassword()))
			.nickname(userDto.getNickname())
			.authorities(Collections.singleton(authority))
			.activated(true)
			.build();

		return userRepository.save(user);
	}

	@Transactional(readOnly = true)
	public Optional<User> getUserWithAuthorities(String username) {
		return userRepository.findOneWithAuthoritiesByUsername(username);
	}

	@Transactional(readOnly = true)
	public Optional<User> getMyUserWithAuthorities() {
		return SecurityUtil.getCurrentUsername()
			.flatMap(userRepository::findOneWithAuthoritiesByUsername);
	}
}