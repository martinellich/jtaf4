package ch.jtaf.ui.util;

import ch.jtaf.db.tables.records.SeriesRecord;
import com.vaadin.flow.component.html.Image;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class LogoUtil {

	private LogoUtil() {
	}

	public static Image resizeLogo(SeriesRecord series) {
		var logo = new Image();

		if (series.getLogo() != null) {
			try {
				var image = ImageIO.read(new ByteArrayInputStream(series.getLogo()));
				double width = image.getWidth(null);
				double height = image.getHeight(null);
				double ratio = width / height;

				logo.setSrc(event -> {
					event.setFileName("logo");
					event.getOutputStream().write(series.getLogo());
				});
				logo.setHeight("60px");
				logo.setWidth(60 * ratio + "px");
			}
			catch (IOException ignore) {
				// Ignore
			}
		}
		return logo;
	}

}
