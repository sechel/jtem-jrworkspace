package de.jtem.jrworkspace.plugin.simplecontroller.widget;

import javax.swing.JLabel;

import de.jtem.jrworkspace.plugin.Controller;
import de.jtem.jrworkspace.plugin.aggregators.ToolBarAggregator;
import de.jtem.jrworkspace.plugin.flavor.PerspectiveFlavor;
import de.jtem.jrworkspace.plugin.simplecontroller.SimpleController;

public class ToolBarAggregatorTest  {

	public static class ToolBar1 extends ToolBarAggregator {
	
		@Override
		public void install(Controller c) throws Exception {
			addTool(getClass(), 0, new JLabel("Test Tool 1"));
			addTool(getClass(), 0, new JLabel("Test Tool 2"));
			addTool(getClass(), 0, new JLabel("Test Tool 3"));
			addTool(getClass(), 0, new JLabel("Test Tool 4"));
		}
	
		@Override
		public Class<? extends PerspectiveFlavor> getPerspective() {
			return SPIPluginTest.class;
		}
	}
	

	public static class ToolBar2 extends ToolBarAggregator {
	
		@Override
		public void install(Controller c) throws Exception {
			addTool(getClass(), 0, new JLabel("Test Tool 5"));
			addTool(getClass(), 0, new JLabel("Test Tool 6"));
			addTool(getClass(), 0, new JLabel("Test Tool 7"));
			addTool(getClass(), 0, new JLabel("Test Tool 8"));
		}
	
		@Override
		public Class<? extends PerspectiveFlavor> getPerspective() {
			return SPIPluginTest.class;
		}
	}
	
	
	public static void main(String[] args) {
		SimpleController c = new SimpleController();
		c.registerPlugin(ToolBar1.class);
		c.registerPlugin(ToolBar2.class);
		c.startup();
	}

}
