package ch.jtaf.ui.dialog;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Paragraph;

public class ConfirmDialog extends Dialog {

	public ConfirmDialog(String id, String header, String text, String confirmText,
			ComponentEventListener<ConfirmEvent> confirmListener, String cancelText,
			ComponentEventListener<CancelEvent> cancelListener) {
		setId(id);

		setHeaderTitle(header);

		add(new Paragraph(text));

		var confirm = new Button(confirmText, event -> {
			fireEvent(new ConfirmEvent(this));
			close();
		});
		confirm.setId(id + "-confirm");
		confirm.getThemeNames().add("error");
		confirm.getThemeNames().add("primary");

		var cancel = new Button(cancelText, event -> {
			fireEvent(new CancelEvent(this));
			close();
		});
		cancel.setId(id + "-cancel");

		getFooter().add(confirm, cancel);

		addListener(ConfirmEvent.class, confirmListener);
		addListener(CancelEvent.class, cancelListener);
	}

	public static class ConfirmEvent extends ComponentEvent<ConfirmDialog> {

		public ConfirmEvent(ConfirmDialog source) {
			super(source, false);
		}

	}

	public static class CancelEvent extends ComponentEvent<ConfirmDialog> {

		public CancelEvent(ConfirmDialog source) {
			super(source, false);
		}

	}

}
