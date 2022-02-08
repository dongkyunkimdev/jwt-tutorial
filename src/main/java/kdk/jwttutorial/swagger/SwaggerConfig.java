package kdk.jwttutorial.swagger;

import java.util.Arrays;
import java.util.List;
import kdk.jwttutorial.security.jwt.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
@EnableWebMvc
public class SwaggerConfig {

	private static final String API_NAME = "Jwt API";
	private static final String API_DESCRIPTION = "Jwt API 명세서";
	private static final String API_VERSION = "1.0";
	private static final String AUTHORIZATION_TYPE = "JWT";

	@Bean
	public Docket api() {
		return new Docket(DocumentationType.OAS_30)
			.useDefaultResponseMessages(false)
			.securityContexts(Arrays.asList(securityContext()))
			.securitySchemes(Arrays.asList(apiKey()))
			.select()
			.apis(RequestHandlerSelectors.basePackage("kdk.jwttutorial"))
			.paths(PathSelectors.any())
			.build()
			.apiInfo(apiInfo());
	}

	private SecurityContext securityContext() {
		return SecurityContext.builder()
			.securityReferences(defaultAuth())
			.build();
	}

	private List<SecurityReference> defaultAuth() {
		AuthorizationScope authorizationScope = new AuthorizationScope("global",
			"accessEverything");
		AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
		authorizationScopes[0] = authorizationScope;
		return Arrays
			.asList(new SecurityReference(JwtFilter.AUTHORIZATION_HEADER, authorizationScopes));
	}

	private ApiKey apiKey() {
		return new ApiKey(AUTHORIZATION_TYPE, JwtFilter.AUTHORIZATION_HEADER, "header");
	}

	private ApiInfo apiInfo() {
		return new ApiInfoBuilder()
			.title(API_NAME)
			.description(API_DESCRIPTION)
			.version(API_VERSION)
			.build();
	}
}
