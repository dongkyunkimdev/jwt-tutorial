package kdk.jwttutorial.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import kdk.jwttutorial.error.ErrorCode;
import kdk.jwttutorial.security.auth.dto.AuthorityDto;
import kdk.jwttutorial.user.dto.UserDto;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class TokenProvider implements InitializingBean {

	static final String ISSUER = "kdk";
	private static final String AUTHORITIES_KEY = "auth";

	private final String secret;
	private final long accessTokenValidityInMilliseconds;
	private final long refreshTokenValidityInMilliseconds;

	private Key key;

	public TokenProvider(
		@Value("${jwt.secret}") String secret,
		@Value("${jwt.access-token-validity-in-seconds}") long accessTokenValidityInMilliseconds,
		@Value("${jwt.refresh-token-validity-in-seconds}") long refreshTokenValidityInMilliseconds) {
		this.secret = secret;
		this.accessTokenValidityInMilliseconds = accessTokenValidityInMilliseconds * 1000;
		this.refreshTokenValidityInMilliseconds = refreshTokenValidityInMilliseconds * 1000;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		byte[] keyBytes = Decoders.BASE64.decode(secret);
		this.key = Keys.hmacShaKeyFor(keyBytes);
	}

	public String createToken(Authentication authentication, EnumToken token) {
		String authorities = authentication.getAuthorities().stream()
			.map(GrantedAuthority::getAuthority)
			.collect(Collectors.joining(","));

		long now = (new Date()).getTime();
		Date validity;
		validity =
			token == EnumToken.ACCESS ?
				new Date(now + this.accessTokenValidityInMilliseconds)
				: new Date(now + this.refreshTokenValidityInMilliseconds);

		JwtBuilder builder = Jwts.builder();
		if (token == EnumToken.ACCESS) {
			builder.claim(AUTHORITIES_KEY, authorities);
		}

		return builder
			.setHeaderParam("typ", "JWT")
			.setIssuer(ISSUER)
			.setSubject(authentication.getName())
			.signWith(key, SignatureAlgorithm.HS512)
			.setExpiration(validity)
			.setIssuedAt(new Date())
			.compact();
	}

	public String createToken(UserDto userDto, EnumToken token) {
		String authorities = userDto.getAuthorityDtoSet().stream()
			.map(AuthorityDto::getAuthorityName).collect(Collectors.joining(","));

		long now = (new Date()).getTime();
		Date validity;
		validity =
			token == EnumToken.ACCESS ?
				new Date(now + this.accessTokenValidityInMilliseconds)
				: new Date(now + this.refreshTokenValidityInMilliseconds);

		JwtBuilder builder = Jwts.builder();
		if (token == EnumToken.ACCESS) {
			builder.claim(AUTHORITIES_KEY, authorities);
		}

		return builder
			.setHeaderParam("typ", "JWT")
			.setIssuer(ISSUER)
			.setSubject(userDto.getEmail())
			.signWith(key, SignatureAlgorithm.HS512)
			.setExpiration(validity)
			.setIssuedAt(new Date())
			.compact();
	}

	public Authentication getAuthentication(String token) {
		Claims claims = Jwts
			.parserBuilder()
			.setSigningKey(key)
			.build()
			.parseClaimsJws(token)
			.getBody();

		Collection<? extends GrantedAuthority> authorities =
			Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
				.map(SimpleGrantedAuthority::new)
				.collect(Collectors.toList());

		User principal = new User(claims.getSubject(), "", authorities);

		return new UsernamePasswordAuthenticationToken(principal, token, authorities);
	}

	public boolean validateToken(HttpServletRequest request, String token) {
		try {
			Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
			return true;
		} catch (SecurityException | MalformedJwtException e) {
			log.info(ErrorCode.INCORRECT_SIGNATURE.getMessage());
			request.setAttribute("exception", ErrorCode.INCORRECT_SIGNATURE.getCode());
		} catch (ExpiredJwtException e) {
			log.info(ErrorCode.EXPIRED_TOKEN.getMessage());
			request.setAttribute("exception", ErrorCode.EXPIRED_TOKEN.getCode());
		} catch (UnsupportedJwtException e) {
			log.info(ErrorCode.UNSUPPORTED_TOKEN.getMessage());
			request.setAttribute("exception", ErrorCode.UNSUPPORTED_TOKEN.getCode());
		} catch (IllegalArgumentException e) {
			log.info(ErrorCode.INVALID_TOKEN.getMessage());
			request.setAttribute("exception", ErrorCode.INVALID_TOKEN.getCode());
		}
		return false;
	}

	public String getSubject(String token) {
		return Jwts.parserBuilder()
			.setSigningKey(key)
			.build()
			.parseClaimsJws(token)
			.getBody()
			.getSubject();
	}

}
