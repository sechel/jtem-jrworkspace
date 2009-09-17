package de.jtem.jrworkspace.plugin.simplecontroller.help;
import java.awt.GridLayout;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.LinkedList;

import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;
import javax.swing.text.html.StyleSheet;

import de.jtem.jrworkspace.plugin.flavor.HelpFlavor;


public class HelpBrowser extends JPanel {

	private static final long 
		serialVersionUID = 1L;
	private JEditorPane
		editorPane = new JEditorPane();
	private JScrollPane
		scroller = new JScrollPane(editorPane);
	private HTMLEditorKit
		editorKit = null;
	private Document
		doc = null;
	private HelpFlavor
		activeHelp = null;
	private LinkedList<Document>
		history = new LinkedList<Document>();
	
	
	public HelpBrowser() {
		makeLayout();
		editorPane.addHyperlinkListener(new JarLinkListener());
		editorPane.setEditable(false);
	} 

	
	private void makeLayout() {
		setLayout(new GridLayout());
		add(scroller);
	}
	
	
	public void setDocument(HelpFlavor help) {
		if (help.getHelpPath() == null || help.getHelpDocument() == null) {
			return;
		}
		activeHelp = help;
		editorKit = new HelpHTMLEditorKit(help);
		editorPane.setEditorKit(editorKit);
		String pagePath = help.getHelpPath() + help.getHelpDocument();
		String cssPath = help.getHelpPath() + help.getHelpStyleSheet();
		loadCss(cssPath);
		loadPage(pagePath);
	}
	
	
	private void loadCss(String cssPath) {
		editorKit.setStyleSheet(null);
		if (activeHelp.getHelpStyleSheet() == null) {
			return;
		}
		try {
			InputStream cssIn = null;
			if (cssPath.startsWith("http://")) {
				URL url = new URL(cssPath);
				cssIn = url.openStream();
			} else {
				cssIn = activeHelp.getHelpHandle().getResourceAsStream(cssPath);
			}
			StyleSheet css = new StyleSheet();
			css.loadRules(new InputStreamReader(cssIn), null);
			editorKit.setStyleSheet(css);
		} catch (Exception e) {
			System.out.println("cannot load style sheet: " + cssPath);
		}
	}
	
	
	private void loadPage(String path) {
		try {
			InputStream in = null;
			if (path.startsWith("http://")) {
				URL url = new URL(path);
				in = url.openStream();
			} else {
				in = activeHelp.getHelpHandle().getResourceAsStream(path);
			}
			doc = editorKit.createDefaultDocument();
			doc.putProperty("IgnoreCharsetDirective",true);
			editorKit.read(in, doc, 0);
			editorPane.setDocument(doc);
		} catch (Exception e) {
			System.out.println("cannot load help page: " + path);
		} 
		history.add(doc);
	}
	
	
	
    private class JarLinkListener implements HyperlinkListener {

		public void hyperlinkUpdate(HyperlinkEvent e) {
			if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
				if (e instanceof HTMLFrameHyperlinkEvent) {
					HTMLFrameHyperlinkEvent evt = (HTMLFrameHyperlinkEvent) e;
					HTMLDocument doc = (HTMLDocument) editorPane.getDocument();
					doc.processHTMLFrameHyperlinkEvent(evt);
				} else {
					if (e.getURL() != null) {
						try {
							editorKit = new HTMLEditorKit();
							editorKit.setStyleSheet(null);
							editorPane.setEditorKit(editorKit);
							doc = editorKit.createDefaultDocument();
							editorPane.setDocument(doc);
							editorPane.setPage(e.getURL());
						} catch (IOException e1) {
							System.out.println(e1.getLocalizedMessage());
						}
					} else if (e.getDescription().toLowerCase().endsWith(".html")) {
						loadPage(activeHelp.getHelpPath() + e.getDescription());		
					} else {
						editorPane.scrollToReference(e.getDescription().substring(1));
					}
				}
			}
		}
	}

	
	
}
