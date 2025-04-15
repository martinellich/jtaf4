package ch.jtaf.configuration.security;

import ch.jtaf.db.tables.records.OrganizationRecord;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinResponse;
import com.vaadin.flow.spring.annotation.VaadinSessionScope;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jooq.DSLContext;
import org.springframework.stereotype.Component;

import java.util.Arrays;

import static ch.jtaf.db.tables.Organization.ORGANIZATION;
import static ch.jtaf.db.tables.OrganizationUser.ORGANIZATION_USER;
import static ch.jtaf.db.tables.SecurityUser.SECURITY_USER;

@VaadinSessionScope
@Component
public class OrganizationProvider {

	public static final String JTAF_ORGANIZATION_ID = "jtaf-organization-id";

	private final DSLContext dslContext;

	private final SecurityContext securityContext;

	private OrganizationRecord organization;

	public OrganizationProvider(DSLContext dslContext, SecurityContext securityContext) {
		this.dslContext = dslContext;
		this.securityContext = securityContext;
	}

	public OrganizationRecord getOrganization() {
		if (organization == null) {
			loadOrganizationFromCookie();
		}
		return organization;
	}

	public void setOrganization(OrganizationRecord organization) {
		this.organization = organization;
		saveOrganizationToCookie();
	}

	private void loadOrganizationFromCookie() {
		var request = (HttpServletRequest) VaadinRequest.getCurrent();

		if (request != null) {
			var cookies = request.getCookies();
			if (cookies != null) {
				// @formatter:off
                Arrays.stream(cookies)
                    .filter(cookie -> cookie.getName().equals(JTAF_ORGANIZATION_ID))
                    .findFirst()
                    .map(Cookie::getValue)
                    .ifPresent(s -> organization = dslContext
                        .select(ORGANIZATION.fields())
                        .from(ORGANIZATION)
                        .join(ORGANIZATION_USER).on(ORGANIZATION_USER.ORGANIZATION_ID.eq(ORGANIZATION.ID))
                        .join(SECURITY_USER).on(SECURITY_USER.ID.eq(ORGANIZATION_USER.USER_ID))
                        .where(ORGANIZATION.ID.eq(Long.parseLong(s)))
                        .and(SECURITY_USER.EMAIL.eq(securityContext.getUsername()))
                        .fetchOneInto(OrganizationRecord.class));
                // @formatter:on
			}
		}
	}

	private void saveOrganizationToCookie() {
		var response = (HttpServletResponse) VaadinResponse.getCurrent();

		var cookie = new Cookie(JTAF_ORGANIZATION_ID, organization.getId().toString());
		cookie.setHttpOnly(true);
		cookie.setMaxAge(60 * 60 * 24);
		response.addCookie(cookie);
	}

}
