package ch.jtaf.ui.errorhandling;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.HasErrorParameter;
import com.vaadin.flow.router.NotFoundException;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import jakarta.servlet.http.HttpServletResponse;

import java.io.Serial;

import static com.vaadin.flow.component.Tag.DIV;

@AnonymousAllowed
@SuppressWarnings("unused")
@Tag(DIV)
public class RouteNotFoundError extends Component implements HasErrorParameter<NotFoundException> {

	@Serial
	private static final long serialVersionUID = 1L;

	@Override
	public int setErrorParameter(BeforeEnterEvent event, ErrorParameter<NotFoundException> parameter) {
		event.rerouteTo("");

		return HttpServletResponse.SC_TEMPORARY_REDIRECT;
	}

}
