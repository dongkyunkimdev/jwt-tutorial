package kdk.jwttutorial.config;

import kdk.jwttutorial.jwt.JwtAccessDeniedHandler;
import kdk.jwttutorial.jwt.JwtAuthenticationEntryPoint;
import kdk.jwttutorial.jwt.JwtSecurityConfig;
import kdk.jwttutorial.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	private final TokenProvider tokenProvider;
	private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
	private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Override
	public void configure(WebSecurity web) {
		web
			.ignoring()
			.antMatchers(
				"/h2-console/**"
				, "/favicon.ico"
			);
	}

	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception {
		httpSecurity
			// token을 사용하는 방식이기 때문에 csrf를 disable
			.csrf().disable()

			.exceptionHandling()
			.authenticationEntryPoint(jwtAuthenticationEntryPoint)
			.accessDeniedHandler(jwtAccessDeniedHandler)

			// enable h2-console
			.and()
			.headers()
			.frameOptions()
			.sameOrigin()

			// 세션을 사용하지 않기 때문에 STATELESS로 설정
			.and()
			.sessionManagement()
			.sessionCreationPolicy(SessionCreationPolicy.STATELESS)

			.and()
			.authorizeRequests()
			.antMatchers("/api/hello").permitAll()
			.antMatchers("/api/authenticate").permitAll()
			.antMatchers("/api/signup").permitAll()

			.anyRequest().authenticated()

			.and()
			.apply(new JwtSecurityConfig(tokenProvider));
	}
}