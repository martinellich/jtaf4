package ch.jtaf.configuration.security;

import com.vaadin.flow.server.VaadinResponse;
import com.vaadin.flow.server.VaadinServletRequest;
import com.vaadin.flow.spring.security.AuthenticationContext;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Takes care of all such static operations that have to do with security and
 * querying rights from different beans of the UI.
 */
@Component
public final class SecurityContext {

    private final AuthenticationContext authenticationContext;

    public SecurityContext(AuthenticationContext authenticationContext) {
        this.authenticationContext = authenticationContext;
    }

    /**
     * Gets the username of the currently signed-in user.
     *
     * @return the username of the current user or <code>null</code> if the user
     * has not signed in
     */
    public String getUsername() {
        var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return switch (principal) {
            case UserDetails userDetails -> userDetails.getUsername();
            case Jwt jwt -> jwt.getSubject();
            case null, default -> ""; // Anonymous or no authentication.
        };
    }

    /**
     * Checks if the user is logged in.
     *
     * @return true if the user is logged in. False otherwise.
     */
    public boolean isUserLoggedIn() {
        return isUserLoggedIn(SecurityContextHolder.getContext().getAuthentication());
    }

    private boolean isUserLoggedIn(Authentication authentication) {
        return authentication != null && !(authentication instanceof AnonymousAuthenticationToken);
    }

    public void logout() {
        var request = VaadinServletRequest.getCurrent().getHttpServletRequest();

        authenticationContext.logout();

        var cookie = new Cookie("remember-me", null);
        cookie.setMaxAge(0);
        cookie.setPath(StringUtils.hasLength(request.getContextPath()) ? request.getContextPath() : "/");

        var response = (HttpServletResponse) VaadinResponse.getCurrent();
        response.addCookie(cookie);
    }
}
