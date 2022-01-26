package kdk.jwttutorial.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import kdk.jwttutorial.dto.UserDto;
import kdk.jwttutorial.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@PostMapping("/signup")
	public ResponseEntity<UserDto> signup(
		@Valid @RequestBody UserDto userDto
	) {
		return ResponseEntity.ok(userService.signup(userDto));
	}

	@GetMapping("/user")
	@PreAuthorize("hasAnyRole('USER','ADMIN')")
	public ResponseEntity<UserDto> getMyUserInfo(HttpServletRequest request) {
		return ResponseEntity.ok(userService.getMyUserWithAuthorities());
	}

	@GetMapping("/user/{username}")
	@PreAuthorize("hasAnyRole('ADMIN')")
	public ResponseEntity<UserDto> getUserInfo(@PathVariable String username) {
		return ResponseEntity.ok(userService.getUserWithAuthorities(username));
	}
}