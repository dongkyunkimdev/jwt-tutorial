package kdk.jwttutorial.user;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import kdk.jwttutorial.security.jwt.dto.TokenDto;
import kdk.jwttutorial.security.jwt.JwtFilter;
import kdk.jwttutorial.security.jwt.JwtService;
import kdk.jwttutorial.user.dto.LoginDto;
import kdk.jwttutorial.user.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;
	private final JwtService jwtService;

	@PostMapping("/login")
	public ResponseEntity<TokenDto> authorize(@Valid @RequestBody LoginDto loginDto) {
		String jwt = jwtService.getJwt(loginDto);
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + jwt);

		return ResponseEntity.ok()
			.headers(httpHeaders)
			.body(TokenDto.builder()
				.token(jwt)
				.build()
			);
	}

	@PostMapping("/signup")
	public ResponseEntity<UserDto> signup(@Valid @RequestBody UserDto userDto) {
		return ResponseEntity.ok(userService.signup(userDto));
	}

	@GetMapping("/myInfo")
	@PreAuthorize("hasAnyRole('USER','ADMIN')")
	public ResponseEntity<UserDto> getMyUserInfo(HttpServletRequest request) {
		return ResponseEntity.ok(userService.getMyUserWithAuthorities());
	}

	@GetMapping("/info/{username}")
	@PreAuthorize("hasAnyRole('ADMIN')")
	public ResponseEntity<UserDto> getUserInfo(@PathVariable String username) {
		return ResponseEntity.ok(userService.getUserWithAuthorities(username));
	}
}