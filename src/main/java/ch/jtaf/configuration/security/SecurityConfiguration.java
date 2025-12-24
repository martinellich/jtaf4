package ch.jtaf.configuration.security;

import ch.jtaf.ui.LoginView;
import com.vaadin.flow.spring.security.VaadinSecurityConfigurer;
import com.vaadin.flow.spring.security.stateless.VaadinStatelessSecurityConfigurer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.health.actuate.endpoint.HealthEndpoint;
import org.springframework.boot.security.autoconfigure.actuate.web.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.JwsAlgorithms;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

	public static final String LOGOUT_URL = "/";

	private final String authSecret;

	public SecurityConfiguration(@Value("${jwt.auth.secret}") String authSecret) {
		this.authSecret = authSecret;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(c -> {
			c.requestMatchers("/fonts/*.*", "/icons/*.png", "/line-awesome/*").permitAll();
			c.requestMatchers(EndpointRequest.to(HealthEndpoint.class)).permitAll();
		});

		http.with(VaadinSecurityConfigurer.vaadin(), configurer -> configurer.loginView(LoginView.class, LOGOUT_URL));

		http.with(new VaadinStatelessSecurityConfigurer<>(),
				stateless -> stateless.issuer("ch.jtaf")
					.withSecretKey()
					.secretKey(new SecretKeySpec(Base64.getDecoder().decode(authSecret), JwsAlgorithms.HS256)));

		return http.build();
	}

}
