package org.luke.gui.controls.alert;

import java.util.Arrays;
import java.util.List;

/**
 * The AlertType enum represents different types of alerts that can be
 * displayed. Each alert type is associated with specific button types, defining
 * the available user actions for that alert.
 * <p>
 * Supported AlertTypes:<br>
 * - INFO: Informational alert with a close button.<br>
 * - DELETE: Alert for deletion with cancel and delete buttons.<br>
 * - ERROR: Error alert with a close button.<br>
 * - CONFIRM: Confirmation alert with cancel, no, and yes buttons.
 *
 * @author SDIDSA
 */
public enum AlertType {
	INFO("info", ButtonType.CLOSE), DELETE("delete", ButtonType.CANCEL, ButtonType.DELETE), ERROR("error", ButtonType.CLOSE),
	CONFIRM("confirm", ButtonType.CANCEL, ButtonType.NO, ButtonType.YES);

	private final List<ButtonType> buttons;
	private final String icon;

	/**
	 * Constructs an AlertType with the specified button types.
	 *
	 * @param icon the icon to display in this alert
	 * @param buttonTypes The associated button types.
	 */
    AlertType(String icon, ButtonType... buttonTypes) {
        this.icon = icon;
        buttons = Arrays.asList(buttonTypes);
	}

	public String getIcon() {
		return icon;
	}

	/**
	 * Gets the list of button types associated with the alert type.
	 *
	 * @return The list of button types.
	 */
	public List<ButtonType> getButtons() {
		return buttons;
	}
}
