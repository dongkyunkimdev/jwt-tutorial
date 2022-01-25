package kdk.jwttutorial.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Authority {

	@Id
	@Column(length = 50)
	private String authorityName;

	@Builder
	public Authority(String authorityName) {
		this.authorityName = authorityName;
	}
}