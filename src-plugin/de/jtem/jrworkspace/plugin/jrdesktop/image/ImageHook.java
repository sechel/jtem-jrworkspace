/**
This file is part of a jTEM project.
All jTEM projects are licensed under the FreeBSD license 
or 2-clause BSD license (see http://www.opensource.org/licenses/bsd-license.php). 

Copyright (c) 2002-2009, Technische Universität Berlin, jTEM
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, 
are permitted provided that the following conditions are met:

-	Redistributions of source code must retain the above copyright notice, 
	this list of conditions and the following disclaimer.

-	Redistributions in binary form must reproduce the above copyright notice, 
	this list of conditions and the following disclaimer in the documentation 
	and/or other materials provided with the distribution.
 
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS 
BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, 
OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT 
OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, 
STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING 
IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY 
OF SUCH DAMAGE.
**/

package de.jtem.jrworkspace.plugin.jrdesktop.image;

import static java.awt.Image.SCALE_SMOOTH;
import static java.awt.image.BufferedImage.TYPE_INT_ARGB;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

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
	
	public static Icon scaleIcon(Icon icon, int width, int height) {
		Image imageNewSize = null;
		if (icon instanceof ImageIcon) {
			ImageIcon imageIcon = (ImageIcon)icon;
			imageNewSize = imageIcon.getImage().getScaledInstance(width, height, SCALE_SMOOTH);
		} else  {
			BufferedImage image = new BufferedImage(icon.getIconWidth(), icon.getIconWidth(), TYPE_INT_ARGB);
			Graphics2D g = image.createGraphics();
			icon.paintIcon(null, g, 0, 0);
			imageNewSize = scaleImage(image, width, height);
		}
		return new ImageIcon(imageNewSize);
	}
	
	
	public static Image scaleImage(Image image, int width, int height) {
		return image.getScaledInstance(SCALE_SMOOTH, width, height);
	}
	
}

