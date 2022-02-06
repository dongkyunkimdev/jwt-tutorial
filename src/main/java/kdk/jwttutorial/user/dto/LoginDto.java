package kdk.jwttutorial.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LoginDto {

	@NotNull
	@Size(min = 3, max = 100)
	@Email
	private String email;

	@JsonProperty(access = Access.WRITE_ONLY)
	@NotNull
	@Size(min = 3, max = 100)
	private String password;

	@Builder
	public LoginDto(
		@NotNull @Size(min = 3, max = 50) @Email String email,
		@NotNull @Size(min = 3, max = 100) String password) {
		this.email = email;
		this.password = password;
	}
}
