package com.jimtough.griswold;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.scene.control.TableCell;
import javafx.scene.paint.Color;

import com.jimtough.griswold.beans.AppBetaStatus;
import com.jimtough.griswold.beans.GenericStatusCode;

public class GenericAppStatusTableCell 
		extends TableCell<AppBetaStatus, String> {
	
	private static final Logger logger =
			LoggerFactory.getLogger(GenericAppStatusTableCell.class);

	private static final Color DEFAULT_TEXT_COLOR = Color.BLACK;
	
	private static final String WARNING_BACKGROUND_COLOR = "yellow";
	private static final Color WARNING_TEXT_COLOR = Color.BLACK;
	
	private static final String ERROR_BACKGROUND_COLOR = "red";
	private static final Color ERROR_TEXT_COLOR = Color.BLACK;

	private static final String FATAL_BACKGROUND_COLOR = "black";
	private static final Color FATAL_TEXT_COLOR = Color.RED;
	
	@Override
	protected void updateItem(String item, boolean empty) {
		logger.debug("updateItem()");
		super.updateItem(item, empty);
		setText(item == null ? "" : item);
		if (item != null) {
			if (item.contains(GenericStatusCode.WARNING.displayString)) {
				setTextFill(WARNING_TEXT_COLOR);
				setStyle("-fx-background-color: " + WARNING_BACKGROUND_COLOR);
			} else if (item.contains(GenericStatusCode.ERROR.displayString) ||
					item.contains(GenericStatusCode.UNKNOWN.displayString)) {
				setTextFill(ERROR_TEXT_COLOR);
				setStyle("-fx-background-color: " + ERROR_BACKGROUND_COLOR);
			} else if (item.contains(GenericStatusCode.OFFLINE.displayString)) {
				setTextFill(FATAL_TEXT_COLOR);
				setStyle("-fx-background-color: " + FATAL_BACKGROUND_COLOR);
			} else {
				setTextFill(DEFAULT_TEXT_COLOR);
				setStyle("");
			}
		} else {
			setTextFill(DEFAULT_TEXT_COLOR);
			setStyle("");
		}
	}

}
