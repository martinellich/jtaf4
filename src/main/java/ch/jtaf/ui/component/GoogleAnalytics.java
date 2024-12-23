package ch.jtaf.ui.component;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.Tag;
import elemental.json.JsonObject;

@Tag("google-analytics")
public class GoogleAnalytics extends Component implements HasSize {

    public GoogleAnalytics(String measurementId) {
        getElement().executeJs(
            """
                (function(browserWindow, htmlDocument, scriptTagName, analyticsLayerName, analyticsId) {
                    // Initialize analytics data layer array if it doesn't exist
                    browserWindow[analyticsLayerName] = browserWindow[analyticsLayerName] || [];
                
                    // Add initial GTM event with timestamp
                    browserWindow[analyticsLayerName].push({
                        'gtm.start': new Date().getTime(),
                        event: 'gtm.js'
                    });
                
                    // Get reference to first script tag in document
                    var firstScriptTag = htmlDocument.getElementsByTagName(scriptTagName)[0];
                
                    // Create new script element for Analytics
                    var analyticsScript = htmlDocument.createElement(scriptTagName);
                
                    // Set custom layer name if not using default 'dataLayer'
                    var customLayerParam = analyticsLayerName != 'dataLayer' ? '&l=' + analyticsLayerName : '';
                
                    // Configure script loading
                    analyticsScript.async = true;
                    analyticsScript.src = 'https://www.googletagmanager.com/gtag/js?id=' + analyticsId + customLayerParam;
                
                    // Insert Analytics script into document
                    firstScriptTag.parentNode.insertBefore(analyticsScript, firstScriptTag);
                
                })(window, document, 'script', 'dataLayer', '$0');
                
                // Ensure dataLayer exists
                window.dataLayer = window.dataLayer || [];
                
                // Function to send data to Analytics
                function sendToAnalytics() {
                    window.dataLayer.push(arguments);
                }
                
                // Initialize Analytics with timestamp
                sendToAnalytics('js', new Date());
                
                // Configure Analytics with measurement ID
                sendToAnalytics('config', '$0');
                """, measurementId
        );
    }

    public void sendPageView(String pageName) {
        getElement().executeJs("""
            gtag('event', 'page_view', {
             'page_title': $0,                      // The page name you pass
             'page_location': window.location.href, // Full URL
             'page_path': window.location.pathname  // URL path only
            });
            """, pageName
        );
    }

    public void sendEvent(String eventName, JsonObject eventParams) {
        getElement().executeJs("""
            gtag('event', $0, $1); // $0 is event name, $1 is parameters object
            """, eventName, eventParams
        );
    }
}
