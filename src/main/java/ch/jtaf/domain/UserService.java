package ch.jtaf.domain;

import ch.jtaf.db.tables.records.SecurityUserRecord;
import com.vaadin.flow.i18n.I18NProvider;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.UUID;

import static ch.jtaf.db.tables.SecurityGroup.SECURITY_GROUP;
import static ch.jtaf.db.tables.SecurityUser.SECURITY_USER;
import static ch.jtaf.db.tables.UserGroup.USER_GROUP;

@Service
public class UserService {

    private final DSLContext dslContext;

    private final PasswordEncoder passwordEncoder;

    private final JavaMailSender mailSender;

    private final I18NProvider i18n;

    private final String publicAddress;

    public UserService(DSLContext dslContext, PasswordEncoder passwordEncoder, JavaMailSender mailSender,
            I18NProvider i18n, @Value("${jtaf.public.address}") String publicAddress) {
        this.dslContext = dslContext;
        this.passwordEncoder = passwordEncoder;
        this.mailSender = mailSender;
        this.i18n = i18n;
        this.publicAddress = publicAddress;
    }

    @Transactional(rollbackFor = UserAlreadyExistException.class)
    public SecurityUserRecord createUser(String firstName, String lastName, String email, String password,
            Locale locale) throws UserAlreadyExistException {

        var count = dslContext.selectCount()
            .from(SECURITY_USER)
            .where(SECURITY_USER.EMAIL.eq(email))
            .fetchOneInto(Integer.class);
        if (count != null && count > 0) {
            throw new UserAlreadyExistException();
        }
        var user = dslContext.newRecord(SECURITY_USER);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setSecret(passwordEncoder.encode(password));
        user.setConfirmationId(UUID.randomUUID().toString());
        user.setConfirmed(false);
        user.store();

        var group = dslContext.selectFrom(SECURITY_GROUP).where(SECURITY_GROUP.NAME.eq("USER")).fetchOne();

        if (group != null) {
            var userGroup = dslContext.newRecord(USER_GROUP);
            userGroup.setUserId(user.getId());
            userGroup.setGroupId(group.getId());
            userGroup.store();

            sendConfirmationEmail(user, locale);

            return user;
        }
        else {
            throw new IllegalStateException("USER role does not exist!");
        }
    }

    public void sendConfirmationEmail(SecurityUserRecord user, Locale locale) {
        var message = new SimpleMailMessage();
        message.setFrom("no-reply@jtaf.ch");
        message.setTo(user.getEmail());
        message.setSubject(i18n.getTranslation("Confirm.Email.Subject", locale));
        message.setText(i18n.getTranslation("Confirm.Email.Body", locale, publicAddress, user.getConfirmationId()));
        mailSender.send(message);
    }

    @Transactional
    public boolean confirm(String confirmationId) {
        var securityUser = dslContext.selectFrom(SECURITY_USER)
            .where(SECURITY_USER.CONFIRMATION_ID.eq(confirmationId))
            .fetchOne();

        if (securityUser != null) {
            securityUser.setConfirmed(true);
            securityUser.store();

            return true;
        }
        else {
            return false;
        }
    }

}
