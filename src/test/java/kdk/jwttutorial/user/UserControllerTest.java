package kdk.jwttutorial.user;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;
import kdk.jwttutorial.security.auth.dto.AuthorityDto;
import kdk.jwttutorial.security.jwt.JwtService;
import kdk.jwttutorial.user.dto.UserDto;
import org.junit.jupiter.api.Assertions;
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
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.MethodArgumentNotValidException;
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
						convertUserDtoJson("test1@test.com", "password", "test1")
					)
			);

		// then
		actions
			.andExpect(status().isCreated())
			.andExpect(jsonPath("email").value("test1@test.com"))
			.andExpect(jsonPath("nickname").value("test1"))
			.andExpect(jsonPath("$.authorityDtoSet[0].authorityName")
				.value(EnumAuthority.ROLE_USER.name()));
	}

	@Test
	void 회원가입_예외_이메일이_없음() throws Exception {
		// when
		ResultActions actions =
			mvc.perform(
				post("/user/signup")
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
					.characterEncoding("UTF-8")
					.content(
						"{"
							+ " \"password\" : \"password\", "
							+ " \"nickname\" : \"test1\" "
							+ "}"
					)
			);

		// then
		expectValidException(actions);
	}

	@Test
	void 회원가입_예외_이메일_형식이_아님() throws Exception {
		// when
		ResultActions actions =
			mvc.perform(
				post("/user/signup")
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
					.characterEncoding("UTF-8")
					.content(
						convertUserDtoJson("test1", "password", "test1")
					)
			);

		// then
		expectValidException(actions);
	}

	@Test
	void 회원가입_예외_이메일_길이_부족() throws Exception {
		// when
		ResultActions actions =
			mvc.perform(
				post("/user/signup")
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
					.characterEncoding("UTF-8")
					.content(
						convertUserDtoJson("t@", "password", "test1")
					)
			);

		// then
		expectValidException(actions);
	}

	@Test
	void 회원가입_예외_이메일_길이_초과() throws Exception {
		// when
		ResultActions actions =
			mvc.perform(
				post("/user/signup")
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
					.characterEncoding("UTF-8")
					.content(
						convertUserDtoJson(
							"ttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttt@ttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttt.com",
							"password", "test1")
					)
			);

		// then
		expectValidException(actions);
	}

	@Test
	void 회원가입_예외_패스워드가_없음() throws Exception {
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
							+ " \"nickname\" : \"test1\" "
							+ "}"
					)
			);

		// then
		expectValidException(actions);
	}

	@Test
	void 회원가입_예외_패스워드_길이_부족() throws Exception {
		// when
		ResultActions actions =
			mvc.perform(
				post("/user/signup")
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
					.characterEncoding("UTF-8")
					.content(
						convertUserDtoJson("test1@test.com", "p", "test1")
					)
			);

		// then
		expectValidException(actions);
	}

	@Test
	void 회원가입_예외_패스워드_길이_초과() throws Exception {
		// when
		ResultActions actions =
			mvc.perform(
				post("/user/signup")
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
					.characterEncoding("UTF-8")
					.content(
						convertUserDtoJson("test1@test.com",
							"passwordpasswordpasswordpasswordpasswordpasswordpasswordpasswordpasswordpasswordpasswordpasswordpasswordpasswordpasswordpasswordpasswordpasswordpasswordpasswordpasswordpassword",
							"test1")
					)
			);

		// then
		expectValidException(actions);
	}

	@Test
	void 회원가입_예외_닉네임이_없음() throws Exception {
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
							+ " \"password\" : \"password\" "
							+ "}"
					)
			);

		// then
		expectValidException(actions);
	}

	@Test
	void 회원가입_예외_닉네임_길이_부족() throws Exception {
		// when
		ResultActions actions =
			mvc.perform(
				post("/user/signup")
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
					.characterEncoding("UTF-8")
					.content(
						convertUserDtoJson("test1@test.com", "password", "t")
					)
			);

		// then
		expectValidException(actions);
	}

	@Test
	void 회원가입_예외_닉네임_길이_초과() throws Exception {
		// when
		ResultActions actions =
			mvc.perform(
				post("/user/signup")
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
					.characterEncoding("UTF-8")
					.content(
						convertUserDtoJson("test1@test.com", "password",
							"test1test1test1test1test1test1test1test1test1test1test1test1test1test1test1test1test1test1test1test1test1test1test1test1test1test1test1test1test1test1")
					)
			);

		// then
		expectValidException(actions);
	}

	@Test
	void 회원가입_예외_JSON_포맷_에러() throws Exception {
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
					)
			);

		// then
		expectNotReadableException(actions);
	}

	private String convertUserDtoJson(String email, String password, String nickname) {
		return String.valueOf(new StringBuffer().append("{")
			.append(" \"email\" : \"")
			.append(email)
			.append("\",")
			.append(" \"password\" : \"")
			.append(password)
			.append("\",")
			.append(" \"nickname\" : \"")
			.append(nickname)
			.append("\"")
			.append("}"));
	}

	private void expectValidException(ResultActions actions) throws Exception {
		actions
			.andExpect(
				(result) -> Assertions
					.assertTrue(result.getResolvedException().getClass().isAssignableFrom(
						MethodArgumentNotValidException.class))
			);
	}

	private void expectNotReadableException(ResultActions actions) throws Exception {
		actions
			.andExpect(
				(result) -> Assertions
					.assertTrue(result.getResolvedException().getClass().isAssignableFrom(
						HttpMessageNotReadableException.class))
			);
	}


	@Test
	void getMyUserInfo() {
	}

	@Test
	void getUserInfo() {
	}
}