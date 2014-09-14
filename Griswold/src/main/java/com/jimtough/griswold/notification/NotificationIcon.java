package com.jimtough.griswold.notification;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import com.jimtough.griswold.SVGStringConstants;

/**
 * Icon that should be display with a notification message
 * @author JTOUGH
 */
public enum NotificationIcon {
	COOL_STUFF(SVGStringConstants.SVG_SUNGLASSES, Color.BLACK),
	SKULL(SVGStringConstants.SVG_SKULL, Color.WHITE),
	CHECKMARK(SVGStringConstants.SVG_CHECKMARK, Color.GREEN),
	CLOCK(SVGStringConstants.SVG_CLOCK, Color.BLACK),
	QUESTION(SVGStringConstants.SVG_QUESTION_CIRCLE, Color.TURQUOISE),
	
	INFO(SVGStringConstants.SVG_INFO, Color.TURQUOISE),
	WARNING(SVGStringConstants.SVG_WARNING, Color.YELLOW),
	ERROR(SVGStringConstants.SVG_ERROR, Color.RED);
	
	public final String svgPathString;
	public final Paint fillPaint;

	private NotificationIcon(
			final String svgPathString,
			final Paint fillPaint) {
		this.svgPathString = svgPathString;
		this.fillPaint = fillPaint;
	}
	
}
