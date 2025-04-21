package ch.jtaf.ui.element;

import com.microsoft.playwright.ElementHandle;

public class GridElement {

	private final ElementHandle elementHandle;

	public GridElement(ElementHandle elementHandle) {
		this.elementHandle = elementHandle;
	}

	public void scrollToIndex(int index) {
		elementHandle.evaluate("(grid, index) => grid.scrollToIndex(index)", index);
	}

	public ElementHandle waitForCellByTextContent(String textContent) {
		return elementHandle.waitForSelector(String.format("vaadin-grid-cell-content >> '%s'", textContent));
	}

	public ElementHandle getCellByTextContent(String textContent) {
		return elementHandle.querySelector(String.format("vaadin-grid-cell-content >> '%s'", textContent));
	}

}
