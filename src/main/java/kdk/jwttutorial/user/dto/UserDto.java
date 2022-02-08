package kdk.jwttutorial.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import kdk.jwttutorial.security.auth.dto.AuthorityDto;
import kdk.jwttutorial.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserDto {

	@NotNull
	@Size(min = 3, max = 100)
	@Email
	private String email;

	@JsonProperty(access = Access.WRITE_ONLY)
	@NotNull
	@Size(min = 3, max = 100)
	private String password;

	@NotNull
	@Size(min = 3, max = 50)
	private String nickname;

	@JsonProperty(access = Access.READ_ONLY)
	private Set<AuthorityDto> authorityDtoSet;

	@Builder
	public UserDto(
		@NotNull @Size(min = 3, max = 100) @Email String email,
		@NotNull @Size(min = 3, max = 100) String password,
		@NotNull @Size(min = 3, max = 50) String nickname,
		Set<AuthorityDto> authorityDtoSet) {
		this.email = email;
		this.password = password;
		this.nickname = nickname;
		this.authorityDtoSet = authorityDtoSet;
	}

	public static UserDto from(User user) {
		return UserDto.builder()
			.email(user.getEmail())
			.nickname(user.getNickname())
			.authorityDtoSet(
				user.getAuthorities().stream().map(
					authority -> AuthorityDto.builder()
						.authorityName(authority.getAuthorityName())
						.build()
				)
					.collect(Collectors.toSet())
			).build();
	}
}
