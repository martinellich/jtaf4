package ch.jtaf.ui.util;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.Notification;

public class NotificationUtil {

	private static final int DURATION = 6000;

	private NotificationUtil() {
	}

	/**
	 * Shows a notification attached directly to the UI.
	 * <p>
	 * Use this instead of {@link Notification#show(String, int, Notification.Position)}
	 * when the notification is shown while a dialog is closing. Since Vaadin 25.3.4 an
	 * auto-added notification is placed inside the topmost dialog with modality, even one
	 * that is already closing, and is removed together with that dialog. Attaching the
	 * notification to the UI first skips the auto-add and keeps it visible.
	 */
	public static void showInUi(String text) {
		var notification = new Notification(text, DURATION, Notification.Position.TOP_END);
		UI.getCurrent().add(notification);
		notification.addOpenedChangeListener(event -> {
			if (!event.isOpened()) {
				notification.removeFromParent();
			}
		});
		notification.open();
	}

}
