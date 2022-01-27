package kdk.jwttutorial.security;

import java.util.List;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
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
			.map(user -> createUser(username, user))
			.orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 사용자입니다."));
	}

	private org.springframework.security.core.userdetails.User createUser(String username,
		User user) {
		if (!user.isActivated()) {
			throw new RuntimeException("활성화되어 있지 않습니다.");
		}
		List<GrantedAuthority> grantedAuthorities = user.getAuthorities().stream()
			.map(authority -> new SimpleGrantedAuthority(authority.getAuthorityName()))
			.collect(Collectors.toList());
		return new org.springframework.security.core.userdetails.User(user.getEmail(),
			user.getPassword(),
			grantedAuthorities);
	}
}
