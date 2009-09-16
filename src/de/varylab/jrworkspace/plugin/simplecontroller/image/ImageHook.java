package de.varylab.jrworkspace.plugin.simplecontroller.image;

import static java.awt.Image.SCALE_SMOOTH;
import static java.awt.image.BufferedImage.TYPE_INT_ARGB;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * An image loader for jar files
 * <p>
 * Copyright 2005 <a href="http://www.sechel.de">Stefan Sechelmann</a>
 * <a href="http://www.math.tu-berlin.de/geometrie">TU-Berlin</a> 
 * @author Stefan Sechelmann
 */
public class ImageHook { 

	
	/**
	 * Java 5 safe method to set the icon image of a dialog
	 * @param dialog
	 * @param image
	 */
	public static void setIconImage(Dialog dialog, Image image) {
		Class<? extends Dialog> dialogClass = dialog.getClass();
		try {
			Method setMethod = dialogClass.getMethod("setIconImage", Image.class);
			setMethod.invoke(dialog, image);
		} catch (Exception e) {}
	}
	
	
	/**
	 * Java 5 safe method to set the icon image of a frame
	 * @param dialog
	 * @param image
	 */
	public static void setIconImage(Frame dialog, Image image) {
		Class<? extends Frame> dialogClass = dialog.getClass();
		try {
			Method setMethod = dialogClass.getMethod("setIconImage", Image.class);
			setMethod.invoke(dialog, image);
		} catch (Exception e) {}
	}
	
	
	public static Image toImage(Icon icon) {
		if (icon == null) {
			return null;
		}
		if (icon instanceof ImageIcon) {
			ImageIcon imageIcon = (ImageIcon)icon;
			return imageIcon.getImage();
		} else {
			BufferedImage image = new BufferedImage(icon.getIconWidth(), icon.getIconWidth(), TYPE_INT_ARGB);
			Graphics2D g = image.createGraphics();
			icon.paintIcon(null, g, 0, 0);
			return image;
		}
	}
	

	public static Image getImage(String filename){ 
		InputStream in = ImageHook.class.getResourceAsStream(filename);
		if (in == null)
			return null;
		Image result = null;
		try {
			result = ImageIO.read(in);
		} catch (IOException e) {}
		return result;
	}

	public static Image getImage(String filename, int width, int height) {
		Image image = getImage(filename);
		if (image == null) {
			return null;
		} else {
			return image.getScaledInstance(width, height, SCALE_SMOOTH);
		}
	}
	
	
	public static Icon getIcon(String filename) {
		Image image = getImage(filename);
		if (image == null) {
			return null;
		} else {
			return new ImageIcon(image);	
		}
	}

	public static Icon getIcon(String filename, int width, int height) {
		Image image = getImage(filename, width, height);
		if (image == null) {
			return null;
		} else {
			return new ImageIcon(image);	
		}
	}
	
	public static Image renderIcon(Icon icon) {
		if (icon == null) {
			return null;
		}
		BufferedImage image = new BufferedImage(icon.getIconWidth(), icon.getIconWidth(), TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();
		icon.paintIcon(null, g, 0, 0);
		return image;
	}
	
	
	public static Icon scaleIcon(Icon icon, int width, int height) {
		if (icon == null) {
			return null;
		}
		Image imageNewSize = null;
		if (icon instanceof ImageIcon) {
			ImageIcon imageIcon = (ImageIcon)icon;
			imageNewSize = imageIcon.getImage().getScaledInstance(width, height, SCALE_SMOOTH);
		} else  {
			Image image = renderIcon(icon);
			imageNewSize = scaleImage(image, width, height);
		}
		return new ImageIcon(imageNewSize);
	}
	
	
	public static Image scaleImage(Image image, int width, int height) {
		return image.getScaledInstance(SCALE_SMOOTH, width, height);
	}
	
}

