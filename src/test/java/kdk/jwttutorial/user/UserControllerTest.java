package kdk.jwttutorial.user;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;
import kdk.jwttutorial.security.auth.dto.AuthorityDto;
import kdk.jwttutorial.security.jwt.JwtService;
import kdk.jwttutorial.user.dto.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.filter.CharacterEncodingFilter;

@ExtendWith(SpringExtension.class)
@WebMvcTest(UserController.class)
@AutoConfigureDataJpa
@ComponentScan(basePackages = "kdk.jwttutorial.security")
class UserControllerTest {

	@Autowired
	private MockMvc mvc;
	@MockBean
	private UserService userService;
	@MockBean
	private JwtService jwtService;

	@BeforeEach
	void setUp() {
		mvc = MockMvcBuilders.standaloneSetup(new UserController(userService, jwtService))
			.addFilters(new CharacterEncodingFilter("UTF-8", true))
			.build();
	}

	@Test
	void 로그인_성공() {

	}

	@Test
	void 회원가입_성공() throws Exception {
		// given
		BDDMockito.given(userService.signup(any()))
			.willReturn(
				UserDto.builder()
					.email("test1@test.com")
					.password("password")
					.nickname("test1")
					.authorityDtoSet(
						Collections.singleton(
							AuthorityDto.builder()
								.authorityName(EnumAuthority.ROLE_USER.name())
								.build()
						)
					)
					.build()
			);

		// when
		ResultActions actions =
			mvc.perform(
				post("/user/signup")
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
					.characterEncoding("UTF-8")
					.content(
						"{"
							+ " \"email\" : \"test1@test.com\", "
							+ " \"password\" : \"password\", "
							+ " \"nickname\" : \"test1\" "
							+ "}"
					)
			);

		// then
		actions
			.andExpect(status().isCreated())
			.andExpect(jsonPath("email").value("test1@test.com"))
			.andExpect(jsonPath("nickname").value("test1"))
			.andExpect(jsonPath("$.authorityDtoSet[0].authorityName").value(EnumAuthority.ROLE_USER.name()));
	}

	@Test
	void getMyUserInfo() {
	}

	@Test
	void getUserInfo() {
	}
}