package ch.jtaf.ui.usecase.account;

import ch.jtaf.TestcontainersConfiguration;
import ch.jtaf.domain.UserService;
import ch.jtaf.usecase.UseCase;
import org.jooq.DSLContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Locale;

import static ch.jtaf.db.tables.SecurityUser.SECURITY_USER;
import static ch.jtaf.db.tables.UserGroup.USER_GROUP;
import static org.jooq.impl.DSL.select;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

/**
 * UC-003: Sign in — server-side authentication through the real Spring Security filter
 * chain: credential verification against {@code SECURITY_USER} with BCrypt, the stateless
 * JWT cookie, and the redirect behaviour. The browserless UI half lives in
 * {@link UC003SignInTest}.
 * <p>
 * See {@code docs/use_cases/uc-003-sign-in.md}.
 */
@Import(TestcontainersConfiguration.class)
@SpringBootTest
class UC003SignInAuthenticationTest {

	private static final String EMAIL = "uc003.signin@nodomain.xyz";

	private static final String PASSWORD = "s3cret-uc003";

	@SuppressWarnings("unused")
	@MockitoBean
	private JavaMailSender javaMailSender;

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Autowired
	private UserService userService;

	@Autowired
	private DSLContext dslContext;

	private MockMvc mockMvc;

	@BeforeEach
	void setUpMockMvc() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();
	}

	@AfterEach
	void removeCreatedUser() {
		dslContext.deleteFrom(USER_GROUP)
			.where(USER_GROUP.USER_ID
				.in(select(SECURITY_USER.ID).from(SECURITY_USER).where(SECURITY_USER.EMAIL.eq(EMAIL))))
			.execute();
		dslContext.deleteFrom(SECURITY_USER).where(SECURITY_USER.EMAIL.eq(EMAIL)).execute();
	}

	@Test
	@UseCase(id = "UC-003")
	void valid_credentials_issue_jwt_cookie_and_forward_to_dashboard() throws Exception {
		var user = userService.createUser("Ulla", "UseCase", EMAIL, PASSWORD, Locale.ENGLISH);
		userService.confirm(user.getConfirmationId());

		mockMvc.perform(formLogin("/login").user(EMAIL).password(PASSWORD))
			.andExpect(authenticated().withUsername(EMAIL))
			.andExpect(redirectedUrl("/"))
			.andExpect(cookie().exists("jwt.headerAndPayload"))
			.andExpect(cookie().exists("jwt.signature"));
	}

	@Test
	@UseCase(id = "UC-003", scenario = "A1: Invalid credentials")
	void wrong_password_reloads_the_login_view_with_error() throws Exception {
		var user = userService.createUser("Ulla", "UseCase", EMAIL, PASSWORD, Locale.ENGLISH);
		userService.confirm(user.getConfirmationId());

		mockMvc.perform(formLogin("/login").user(EMAIL).password("wrong"))
			.andExpect(unauthenticated())
			.andExpect(redirectedUrl("/login?error"))
			.andExpect(cookie().doesNotExist("jwt.headerAndPayload"));
	}

	@Test
	@UseCase(id = "UC-003", scenario = "A1: Invalid credentials")
	void unknown_user_reloads_the_login_view_with_error() throws Exception {
		mockMvc.perform(formLogin("/login").user("not.existing@user.com").password("pass"))
			.andExpect(unauthenticated())
			.andExpect(redirectedUrl("/login?error"));
	}

	@Test
	@UseCase(id = "UC-003", scenario = "A2: Account not confirmed", businessRules = "BR-005")
	void unconfirmed_user_is_rejected() throws Exception {
		userService.createUser("Ulla", "UseCase", EMAIL, PASSWORD, Locale.ENGLISH);

		mockMvc.perform(formLogin("/login").user(EMAIL).password(PASSWORD))
			.andExpect(unauthenticated())
			.andExpect(redirectedUrl("/login?error"));
	}

}
