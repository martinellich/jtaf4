package ch.jtaf.ui.po;

import com.microsoft.playwright.Page;

public class LoginPO {

    private final Page page;

    public LoginPO(Page page) {
        this.page = page;
    }

    public void login(String username, String password) {
        page.locator("vaadin-text-field[name='username'] > input").fill(username);
        page.locator("vaadin-password-field[name='password'] > input").fill(password);
        page.locator("vaadin-button").click();
    }
}
