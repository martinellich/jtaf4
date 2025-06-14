package ch.jtaf.configuration.security;

import ch.jtaf.ui.LoginView;
import com.vaadin.flow.spring.security.VaadinWebSecurity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.JwsAlgorithms;

import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@EnableWebSecurity
@Configuration
public class SecurityConfiguration extends VaadinWebSecurity {

	public static final String LOGOUT_URL = "/";

	private final String authSecret;

	public SecurityConfiguration(@Value("${jwt.auth.secret}") String authSecret) {
		this.authSecret = authSecret;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(c -> {
			c.requestMatchers("/icons/*.png", "/line-awesome/*").permitAll();
			c.requestMatchers(EndpointRequest.to(HealthEndpoint.class)).permitAll();
		});

		super.configure(http);

		setLoginView(http, LoginView.class, LOGOUT_URL);

		setStatelessAuthentication(http, new SecretKeySpec(Base64.getDecoder().decode(authSecret), JwsAlgorithms.HS256),
				"ch.jtaf", 3600);
	}

}
