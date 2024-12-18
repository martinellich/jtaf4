package ch.jtaf.domain;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@SpringBootTest
class UserDetailsServiceImplTest {

    @MockitoBean
    private JavaMailSender javaMailSender;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Test
    void load_user_by_username() {
        UserDetails userDetails = userDetailsService.loadUserByUsername("simon@martinelli.ch");

        assertThat(userDetails.getUsername()).isEqualTo("simon@martinelli.ch");
    }

    @Test
    void load_unknown_user() {
        assertThatExceptionOfType(UsernameNotFoundException.class)
            .isThrownBy(() -> userDetailsService.loadUserByUsername("jane@doe.com"));
    }

}
