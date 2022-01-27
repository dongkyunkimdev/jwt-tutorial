package kdk.jwttutorial.security.auth.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuthorityDto {

	private String authorityName;

	@Builder
	public AuthorityDto(String authorityName) {
		this.authorityName = authorityName;
	}
}
