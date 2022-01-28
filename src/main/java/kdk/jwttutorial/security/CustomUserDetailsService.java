package kdk.jwttutorial.security;

import java.util.List;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import kdk.jwttutorial.error.ErrorCode;
import kdk.jwttutorial.user.User;
import kdk.jwttutorial.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component("userDetailsService")
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;

	@Override
	@Transactional
	public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
		return userRepository.findOneWithAuthoritiesByEmail(username)
			.map(this::createUser)
			.orElseThrow(() -> new UsernameNotFoundException(ErrorCode.USER_NOT_FOUND.getMessage()));
	}

	private org.springframework.security.core.userdetails.User createUser(User user) {
		List<GrantedAuthority> grantedAuthorities = user.getAuthorities().stream()
			.map(authority -> new SimpleGrantedAuthority(authority.getAuthorityName()))
			.collect(Collectors.toList());
		return new org.springframework.security.core.userdetails.User(user.getEmail(),
			user.getPassword(),
			grantedAuthorities);
	}
}
