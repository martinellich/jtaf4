package ch.jtaf.ui;

import ch.jtaf.configuration.security.OrganizationProvider;
import ch.jtaf.configuration.security.SecurityContext;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vaadin.flow.theme.lumo.LumoUtility.*;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

public class MainLayout extends AppLayout implements BeforeEnterObserver{

    @Serial
    private static final long serialVersionUID = 1L;

    private static final String LA_LA_FILE = "la la-file";

    private final transient OrganizationProvider organizationProvider;
    private final String applicationVersion;
    private final transient SecurityContext securityContext;

    private final Div version = new Div();
    private Button login;
    private Button logout;

    private H1 viewTitle;

    private RouterLink seriesLink;
    private RouterLink eventsLink;
    private RouterLink clubsLink;
    private RouterLink athletesLink;
    private RouterLink register;

    public MainLayout(OrganizationProvider organizationProvider, @Value("${application.version}") String applicationVersion, SecurityContext securityContext) {
        this.organizationProvider = organizationProvider;
        this.applicationVersion = applicationVersion;
        this.securityContext = securityContext;

        setPrimarySection(Section.DRAWER);
        addToNavbar(false, createHeaderContent());
        addToDrawer(createDrawerContent());
    }

    private Component createHeaderContent() {
        var toggle = new DrawerToggle();
        toggle.addClassName("text-secondary");
        toggle.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        toggle.getElement().setAttribute("aria-label", "Menu toggle");

        viewTitle = new H1();
        viewTitle.setId("view-title");
        viewTitle.addClassNames(LumoUtility.Margin.NONE, LumoUtility.FontSize.LARGE);
        viewTitle.setMinWidth("400px");

        var info = new HorizontalLayout();
        info.setWidthFull();
        info.getStyle().set("padding-right", "20px");
        info.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        info.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);

        var about = new Anchor("https://github.com/martinellich/jtaf4", "About");
        about.setTarget("_blank");

        register = new RouterLink(getTranslation("Register"), RegisterView.class);

        login = new Button("Login", e -> UI.getCurrent().navigate(OrganizationsView.class));
        login.setVisible(false);

        logout = new Button("Logout", e -> securityContext.logout());
        logout.setId("logout");

        info.add(about, version, register, login, logout);

        var header = new Header(toggle, viewTitle, info);
        header.addClassNames(Background.BASE, Border.BOTTOM, BorderColor.CONTRAST_10, BoxSizing.BORDER,
            Display.FLEX, Height.XLARGE, AlignItems.CENTER, Width.FULL);
        return header;
    }

    private Component createDrawerContent() {
        var logo = new Image("icons/logo.png", "JTAF");

        var section = new com.vaadin.flow.component.html.Section(logo, createNavigation(), createFooter());
        section.addClassNames(Display.FLEX, FlexDirection.COLUMN, AlignItems.STRETCH, MaxHeight.FULL, MinHeight.FULL);
        return section;
    }

    private Nav createNavigation() {
        var nav = new Nav();
        nav.addClassNames(Border.BOTTOM, BorderColor.CONTRAST_10, Flex.GROW, Overflow.AUTO);
        nav.getElement().setAttribute("aria-labelledby", "views");

        var views = new H3("Views");
        views.addClassNames(Display.FLEX, Height.MEDIUM, AlignItems.CENTER, Margin.Horizontal.MEDIUM, Margin.Vertical.NONE,
            FontSize.SMALL, TextColor.TERTIARY);
        views.setId("views");

        for (var link : createLinks()) {
            nav.add(link);
        }
        return nav;
    }

    private List<RouterLink> createLinks() {
        var links = new ArrayList<RouterLink>();

        links.add(createLink(new MenuItemInfo(getTranslation("Dashboard"), "la la-globe", DashboardView.class)));
        links.add(createLink(new MenuItemInfo(getTranslation("My.Organizations"), LA_LA_FILE, OrganizationsView.class)));

        seriesLink = createLink(new MenuItemInfo("", LA_LA_FILE, SeriesListView.class));
        seriesLink.setId("series-list-link");
        links.add(seriesLink);

        eventsLink = createLink(new MenuItemInfo(getTranslation("Events"), LA_LA_FILE, EventsView.class));
        links.add(eventsLink);

        clubsLink = createLink(new MenuItemInfo(getTranslation("Clubs"), LA_LA_FILE, ClubsView.class));
        links.add(clubsLink);

        athletesLink = createLink(new MenuItemInfo(getTranslation("Athletes"), LA_LA_FILE, AthletesView.class));
        links.add(athletesLink);

        setVisibilityOfLinks(false);

        return links;
    }

    private static RouterLink createLink(MenuItemInfo menuItemInfo) {
        var link = new RouterLink();
        link.addClassNames(Display.FLEX, Margin.Horizontal.SMALL, Padding.SMALL, Position.RELATIVE, TextColor.SECONDARY);
        link.setRoute(menuItemInfo.view());

        var icon = new Span();
        icon.addClassNames(Margin.End.SMALL, FontSize.LARGE);
        if (!menuItemInfo.iconClass().isEmpty()) {
            icon.addClassNames(menuItemInfo.iconClass());
        }

        var text = new Span(menuItemInfo.text());
        text.addClassNames(FontWeight.MEDIUM, FontSize.SMALL);

        link.add(icon, text);
        return link;
    }

    private Footer createFooter() {
        var layout = new Footer();
        layout.addClassNames(Display.FLEX, Margin.Vertical.SMALL, Padding.Horizontal.MEDIUM, Padding.Vertical.XSMALL);

        var htmlByLink = new Html("<p style='color: var(--lumo-primary-color)'>Free and<br>Open Source<br>by Martinelli LLC</p>");

        var byLink = new Anchor();
        byLink.setWidth("300px");
        byLink.getElement().getStyle().set("font-size", "small");
        byLink.setHref("https://72.services");
        byLink.setTarget("_blank");
        byLink.add(htmlByLink);
        layout.add(byLink);

        return layout;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
    }

    private String getCurrentPageTitle() {
        var title = (HasDynamicTitle) getContent();
        return title == null ? "" : title.getPageTitle();
    }

    @PostConstruct
    public void postConstruct() {
        version.setText(applicationVersion);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (securityContext.isUserLoggedIn()) {
            register.setVisible(false);
            login.setVisible(false);

            logout.setText("Logout (%s)".formatted(securityContext.getUsername()));
            logout.setVisible(true);

            var organization = organizationProvider.getOrganization();
            if (organization != null) {
                seriesLink.setText(organization.getOrganizationKey());
                setVisibilityOfLinks(true);
            }
        } else {
            register.setVisible(true);
            login.setVisible(true);
            logout.setVisible(false);

            seriesLink.setText("");
            setVisibilityOfLinks(false);
        }
    }

    private void setVisibilityOfLinks(boolean visible) {
        seriesLink.setVisible(visible);
        eventsLink.setVisible(visible);
        clubsLink.setVisible(visible);
        athletesLink.setVisible(visible);
    }

    public record MenuItemInfo(String text, String iconClass, Class<? extends Component> view) {
    }
}
