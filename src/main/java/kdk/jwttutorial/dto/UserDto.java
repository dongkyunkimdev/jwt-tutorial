package kdk.jwttutorial.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import kdk.jwttutorial.entity.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
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

	private Set<AuthorityDto> authorityDtoSet;

	public static UserDto from(User user) {
		if (user == null) {
			return null;
		}

		return UserDto.builder()
			.email(user.getEmail())
			.nickname(user.getNickname())
			.authorityDtoSet(user.getAuthorities().stream()
				.map(authority -> AuthorityDto.builder().authorityName(authority.getAuthorityName())
					.build())
				.collect(Collectors.toSet())).build();
	}
}
