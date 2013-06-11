package de.jtem.jrworkspace.plugin.simplecontroller.widget;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToolBar;

/**
 *  FlowLayout subclass that fully supports wrapping of components.
 */
public class WrapLayout extends FlowLayout
{
	private static final long 
		serialVersionUID = 1L;
	private Dimension 
		preferredLayoutSize;

	/**
	* Constructs a new <code>WrapLayout</code> with a left
	* alignment and a default 5-unit horizontal and vertical gap.
	*/
	public WrapLayout()
	{
		super();
	}

	/**
	* Constructs a new <code>FlowLayout</code> with the specified
	* alignment and a default 5-unit horizontal and vertical gap.
	* The value of the alignment argument must be one of
	* <code>WrapLayout</code>, <code>WrapLayout</code>,
	* or <code>WrapLayout</code>.
	* @param align the alignment value
	*/
	public WrapLayout(int align)
	{
		super(align);
	}

	/**
	* Creates a new flow layout manager with the indicated alignment
	* and the indicated horizontal and vertical gaps.
	* <p>
	* The value of the alignment argument must be one of
	* <code>WrapLayout</code>, <code>WrapLayout</code>,
	* or <code>WrapLayout</code>.
	* @param align the alignment value
	* @param hgap the horizontal gap between components
	* @param vgap the vertical gap between components
	*/
	public WrapLayout(int align, int hgap, int vgap)
	{
		super(align, hgap, vgap);
	}

	/**
	* Returns the preferred dimensions for this layout given the
	* <i>visible</i> components in the specified target container.
	* @param target the component which needs to be laid out
	* @return the preferred dimensions to lay out the
	* subcomponents of the specified container
	*/
	@Override
	public Dimension preferredLayoutSize(Container target)
	{
		return layoutSize(target, true);
	}

	/**
	* Returns the minimum dimensions needed to layout the <i>visible</i>
	* components contained in the specified target container.
	* @param target the component which needs to be laid out
	* @return the minimum dimensions to lay out the
	* subcomponents of the specified container
	*/
	@Override
	public Dimension minimumLayoutSize(Container target)
	{
		return layoutSize(target, false);
	}

	/**
	* Returns the minimum or preferred dimension needed to layout the target
	* container.
	*
	* @param target target to get layout size for
	* @param preferred should preferred size be calculated
	* @return the dimension to layout the target container
	*/
	private Dimension layoutSize(Container target, boolean preferred)
	{
	synchronized (target.getTreeLock())
	{
		//  Each row must fit with the width allocated to the containter.
		//  When the container width = 0, the preferred width of the container
		//  has not yet been calculated so lets ask for the maximum.

		int targetWidth = target.getSize().width;

		if (targetWidth == 0)
			targetWidth = Integer.MAX_VALUE;

		int hgap = getHgap();
		int vgap = getVgap();
		Insets insets = target.getInsets();
		int horizontalInsetsAndGap = insets.left + insets.right + (hgap * 2);
		int maxWidth = targetWidth - horizontalInsetsAndGap;

		//  Fit components into the allowed width

		Dimension dim = new Dimension(0, 0);
		int rowWidth = 0;
		int rowHeight = 0;

		int nmembers = target.getComponentCount();

		for (int i = 0; i < nmembers; i++)
		{
			Component m = target.getComponent(i);

			if (m.isVisible())
			{
				Dimension d = preferred ? m.getPreferredSize() : m.getMinimumSize();

				//  Can't add the component to current row. Start a new row.

				if (rowWidth + d.width > maxWidth)
				{
					addRow(dim, rowWidth, rowHeight);
					rowWidth = 0;
					rowHeight = 0;
				}

				//  Add a horizontal gap for all components after the first

				if (rowWidth != 0)
				{
					rowWidth += hgap;
				}

				rowWidth += d.width;
				rowHeight = Math.max(rowHeight, d.height);
			}
		}

		addRow(dim, rowWidth, rowHeight);

		dim.width += horizontalInsetsAndGap;
		dim.height += insets.top + insets.bottom + vgap * 2;

		//	When using a scroll pane or the DecoratedLookAndFeel we need to
		//  make sure the preferred size is less than the size of the
		//  target containter so shrinking the container size works
		//  correctly. Removing the horizontal gap is an easy way to do this.

		dim.width -= (hgap + 1);

		return dim;
	}
	}

	/**
	 *  Layout the components in the Container using the layout logic of the
	 *  parent FlowLayout class.
	 *
	 *	@param target the Container using this WrapLayout
	 */
	@Override
	public void layoutContainer(Container target)
	{
   		Dimension size = preferredLayoutSize(target);

		//  When a frame is minimized or maximized the preferred size of the
		//  Container is assumed not to change. Therefore we need to force a
		//  validate() to make sure that space, if available, is allocated to
		//  the panel using a WrapLayout.

   		if (size.equals(preferredLayoutSize))
   		{
   			super.layoutContainer(target);
   			int targetWidth = target.getBounds().width;
   			int num = target.getComponentCount();
   			for (int i = 0; i < num; i++) {
   				Component c = target.getComponent(i);
   				if (i == num - 1 || target.getComponent(i + 1).getBounds().x <= c.getBounds().x) {
   					Rectangle bounds = c.getBounds();
   					int width = targetWidth - bounds.x - getHgap();
   					bounds.width = width;
   					c.setBounds(bounds);
   				}
   			}
   		}
   		else
   		{
   			preferredLayoutSize = size;
   			Container top = target;

   			while (top.getParent() != null)
   			{
   				top = top.getParent();
   			}

   			top.validate();
   		}
	}

	/*
	 *  A new row has been completed. Use the dimensions of this row
	 *  to update the preferred size for the container.
	 *
	 *  @param dim update the width and height when appropriate
	 *  @param rowWidth the width of the row to add
	 *  @param rowHeight the height of the row to add
	 */
	private void addRow(Dimension dim, int rowWidth, int rowHeight)
	{
		dim.width = Math.max(dim.width, rowWidth);

		if (dim.height > 0)
		{
			dim.height += getVgap();
		}

		dim.height += rowHeight;
	}
	
	
	public static void main(String[] args) throws Exception {
		Runnable r = new Runnable() {
			@Override
			public void run() {
				try {
					startup();
				} catch (Exception e) {	}
			}
		};
		EventQueue.invokeLater(r);
	}
	
	public static void startup() throws Exception {
		JFrame f = new JFrame("Wrap Layout Test");
		
		JToolBar t1 = new JToolBar();
		JToolBar t2 = new JToolBar();
		t2.setLayout(new GridLayout(1, 3));
		
		t1.setPreferredSize(new Dimension(300, 25));
		
		t1.add(new JButton("B1"));
		t1.add(new JButton("B2"));
		t1.add(new JButton("B3"));
		t1.add(new JComboBox());
		t1.add(new JButton("test"));
		t2.add(new JComboBox());
		t2.add(new JComboBox());
		t2.add(new JComboBox());
		
		t1.setBackground(Color.MAGENTA);
		t2.setBackground(Color.CYAN);
		
		JPanel northPanel = new JPanel();
		northPanel.setBorder(BorderFactory.createEtchedBorder());
		northPanel.setLayout(new WrapLayout(FlowLayout.LEADING, 2, 0));
		northPanel.add(t1);
		northPanel.add(t2);
		
		f.setSize(800, 600);
		f.setLayout(new BorderLayout());
		f.add(northPanel, BorderLayout.NORTH);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
	}
	
}
