package ch.jtaf.domain;

import ch.jtaf.TestcontainersConfiguration;
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
	void load_user_by_username() {
		UserDetails userDetails = userDetailsService.loadUserByUsername("simon@martinelli.ch");

		assertThat(userDetails.getUsername()).isEqualTo("simon@martinelli.ch");
		assertThat(userDetails.isEnabled()).isTrue();
	}

	@Test
	void unconfirmed_user_is_disabled() throws UserAlreadyExistException {
		userService.createUser("Ursula", "Unbestaetigt", "ursula.unbestaetigt@nodomain.xyz", "pass",
				java.util.Locale.GERMAN);

		UserDetails userDetails = userDetailsService.loadUserByUsername("ursula.unbestaetigt@nodomain.xyz");

		assertThat(userDetails.isEnabled()).isFalse();
	}

	@Test
	void load_unknown_user() {
		assertThatExceptionOfType(UsernameNotFoundException.class)
			.isThrownBy(() -> userDetailsService.loadUserByUsername("jane@doe.com"));
	}

}
