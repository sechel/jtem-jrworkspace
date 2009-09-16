package de.varylab.jrworkspace.plugin.simplecontroller.help;
import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.Element;
import javax.swing.text.StyleConstants;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.ImageView;

import de.varylab.jrworkspace.plugin.flavor.HelpFlavor;


public class HelpHTMLEditorKit extends HTMLEditorKit {

	private static final long 
		serialVersionUID = 1L;
	private JarViewFactory
		viewFactory = new JarViewFactory();
	private HelpFlavor 
		help = null;
	
	public HelpHTMLEditorKit(HelpFlavor help) {
		this.help = help;
	}
	
	
	@Override
	public ViewFactory getViewFactory() {
		return viewFactory;
	}
	
	
	public class JarViewFactory extends HTMLFactory {
		
		@Override
		public View create(Element elem) {
			AttributeSet attrs = elem.getAttributes();
	 	    Object elementName = attrs.getAttribute(AbstractDocument.ElementNameAttribute);
		    Object o = (elementName != null) ? null : attrs.getAttribute(StyleConstants.NameAttribute);
		    if (o instanceof HTML.Tag) {
		    	HTML.Tag kind = (HTML.Tag) o;
				if (kind==HTML.Tag.IMG) {
				    return new JarImageView(elem);
				} 
		    }
			return super.create(elem);
		}
		
	}
	
	
	public class JarImageView extends ImageView {

		private Image
			image = null;
		
		public JarImageView(Element elem) {
			super(elem);
		}
		
		@Override
		public Image getImage() {
			if (image == null) {
				String src = (String)getElement().getAttributes().getAttribute(HTML.Attribute.SRC);
				if (src.toLowerCase().startsWith("http://")) { // absolute
					return super.getImage();
				}
				try {
					String path = help.getHelpPath() + src;
					InputStream in = null;
					if (path.startsWith("http://")) {
						URL url = new URL(path);
						in = url.openStream();
					} else {
						in = help.getHelpHandle().getResourceAsStream(path);
					}
					Image result = null;
					try {
						result = ImageIO.read(in);
					} catch (IOException e) {}
					image = result;
				} catch (Exception e) {
					System.out.println("Could not load image " + src);
				}
			}
			return image;
		}
		
	}
	
	
}
