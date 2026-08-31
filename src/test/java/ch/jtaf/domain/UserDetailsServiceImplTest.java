package ch.jtaf.domain;

import ch.jtaf.TestcontainersConfiguration;
import ch.jtaf.usecase.UseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * Service-level tests for UC-003 (Sign in), Step 4 and BR-005.
 */
@Import(TestcontainersConfiguration.class)
@SpringBootTest
class UserDetailsServiceImplTest {

	@SuppressWarnings("unused")
	@MockitoBean
	private JavaMailSender javaMailSender;

	@Autowired
	private UserDetailsServiceImpl userDetailsService;

	@Autowired
	private UserService userService;

	@Test
	@UseCase(id = "UC-003")
	void load_user_by_username() {
		UserDetails userDetails = userDetailsService.loadUserByUsername("simon@martinelli.ch");

		assertThat(userDetails.getUsername()).isEqualTo("simon@martinelli.ch");
		assertThat(userDetails.isEnabled()).isTrue();
	}

	@Test
	@UseCase(id = "UC-003", scenario = "A2: Account not confirmed", businessRules = "BR-005")
	void unconfirmed_user_is_disabled() throws UserAlreadyExistException {
		userService.createUser("Ursula", "Unbestaetigt", "ursula.unbestaetigt@nodomain.xyz", "pass",
				java.util.Locale.GERMAN);

		UserDetails userDetails = userDetailsService.loadUserByUsername("ursula.unbestaetigt@nodomain.xyz");

		assertThat(userDetails.isEnabled()).isFalse();
	}

	@Test
	@UseCase(id = "UC-003", scenario = "A1: Invalid credentials")
	void load_unknown_user() {
		assertThatExceptionOfType(UsernameNotFoundException.class)
			.isThrownBy(() -> userDetailsService.loadUserByUsername("jane@doe.com"));
	}

}
