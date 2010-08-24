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

package de.jtem.jrworkspace.plugin.sidecontainer.widget;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

public class TestBed {

	
	public static void main(String[] args) throws Exception {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		JFrame f = new JFrame("ShrinkPanel Testbed");
		f.setLayout(new BorderLayout());
		f.setSize(800, 600);
		
		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new BorderLayout());
		
		
		ShrinkPanel sp1 = new ShrinkPanel("Test Panel 1", true);
		ShrinkPanel sp2 = new ShrinkPanel("Test Panel 2");
		ShrinkPanel sp3 = new ShrinkPanel("Test Panel 3");
		ShrinkPanel sp4 = new ShrinkPanel("Test Panel 4");
		
		sp1.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.fill = GridBagConstraints.BOTH;
		c.gridwidth = GridBagConstraints.REMAINDER;
		sp1.add(new JLabel("Test Label 1"), c);
		sp1.add(sp2, c);
		sp2.add(new JLabel("Test Label 2"));
		sp3.add(new JLabel("Test Label 3"));
		sp4.add(new JLabel("Test Label 4"));
		
		ShrinkSlotVertical spc1 = new ShrinkSlotVertical(200);
		ShrinkSlotVertical spc2 = new ShrinkSlotVertical(200);
		ShrinkSlotHorizontal sps1 = new ShrinkSlotHorizontal(10);
		ShrinkSlotHorizontal sps2 = new ShrinkSlotHorizontal(10);
//		JPanel spc1 = new JPanel();
//		JPanel spc2 = new JPanel();
//		JPanel sps1 = new JPanel();
//		JPanel sps2 = new JPanel();
		spc1.setBorder(BorderFactory.createEtchedBorder());
		spc2.setBorder(BorderFactory.createEtchedBorder());
		sps1.setBorder(BorderFactory.createEtchedBorder());
		sps2.setBorder(BorderFactory.createEtchedBorder());
		
		spc1.addShrinkPanel(sp1);
		spc1.addShrinkPanel(sp3);
		spc2.addShrinkPanel(sp4);
		
		f.add(spc1, BorderLayout.WEST);
		f.add(spc2, BorderLayout.EAST);
		centerPanel.add(sps1, BorderLayout.NORTH);
		centerPanel.add(sps2, BorderLayout.SOUTH);
		f.add(centerPanel, BorderLayout.CENTER);
		
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
	}
	
	
}
