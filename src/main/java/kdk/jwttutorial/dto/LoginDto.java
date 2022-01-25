package kdk.jwttutorial.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class LoginDto {

	@NotNull
	@Size(min = 3, max = 50)
	private String username;

	@NotNull
	@Size(min = 3, max = 100)
	private String password;
}
