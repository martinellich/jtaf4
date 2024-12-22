package ch.jtaf.ui.component;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.Tag;

@Tag("google-analytics")
public class GoogleAnalytics extends Component implements HasSize {

    public GoogleAnalytics(String measurementId) {
        getElement().executeJs(
            """
                (function(w,d,s,l,i){w[l]=w[l]||[];w[l].push({'gtm.start':
                new Date().getTime(),event:'gtm.js'});var f=d.getElementsByTagName(s)[0],
                j=d.createElement(s),dl=l!='dataLayer'?'&l='+l:'';j.async=true;j.src=
                'https://www.googletagmanager.com/gtag/js?id='+i+dl;f.parentNode.insertBefore(j,f);
                })(window,document,'script','dataLayer','$0');
                window.dataLayer = window.dataLayer || [];
                function gtag(){dataLayer.push(arguments);}
                gtag('js', new Date());
                gtag('config', '$0');
                """, measurementId
        );
    }

    public void sendPageView(String pageName) {
        getElement().executeJs(
            """
                gtag('event', 'page_view', {
                    'page_title': $0,
                    'page_location': window.location.href,
                    'page_path': window.location.pathname
                });
                """, pageName
        );
    }
}
