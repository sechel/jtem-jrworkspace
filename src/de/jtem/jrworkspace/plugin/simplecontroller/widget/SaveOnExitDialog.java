package de.jtem.jrworkspace.plugin.simplecontroller.widget;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

import de.jtem.jrworkspace.plugin.simplecontroller.SimpleController;

public class SaveOnExitDialog {
	//results are saved here
	private OutputStream out;

	//gui values and actions results that are used later
	final private boolean[] yesNoCanceled=new boolean[] {false,false,true}; 
	final JTextField filenameTF=new JTextField();
	final JCheckBox rememberCB=new JCheckBox("Rember my decision.");
	final JCheckBox loadCB=new JCheckBox("Load properties from this file on startup.");
	
	final SimpleController controller;
	final private JDialog dialog;
	
	public SaveOnExitDialog(File file, JFrame mainWindow, SimpleController controller) {
		if (controller == null) 
			throw new NullPointerException();
		this.controller=controller;
		
		dialog=new JDialog(mainWindow, "Save properties?", true);
		final JFileChooser fileChooser=new JFileChooser();

		if (file != null) 
			fileChooser.setSelectedFile(file.getAbsoluteFile());
		fileChooser.setFileHidingEnabled(false);
		fileChooser.setFileFilter(new PropertiesFileFilter());

		//dialog elements
		final JLabel question= new JLabel("Do you want to save the properties?");
		final JLabel filenameLabel=new JLabel("File: ");
		filenameTF.setText(file==null?"":file.getAbsolutePath());
		filenameTF.setMinimumSize(new Dimension(200,10));
		int width=filenameTF.getPreferredSize().width;
		filenameTF.setPreferredSize(new Dimension(width>200?width:200,15));
		final JButton chooseButton=new JButton("Choose...");
		chooseButton.setMargin(new Insets(0, 20, 0, 20));
		rememberCB.setSelected(!controller.isAskBeforeSaveOnExit());
		loadCB.setSelected(controller.isLoadFromUserPropertyFile());
		final JButton noButton = new JButton("No");
		noButton.setMargin(new Insets(0, 20, 0, 20));
		final JButton yesButton = new JButton("Yes");
		yesButton.setMargin(new Insets(0, 20, 0, 20));
		final JButton cancelButton = new JButton("Cancel");
		cancelButton.setMargin(new Insets(0, 20, 0, 20));

		//add functionality
		chooseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fileChooser.setSelectedFile(new File(filenameTF.getText()));
				int choice = fileChooser.showDialog(dialog, "Select");
				if (choice == JFileChooser.APPROVE_OPTION) {
					filenameTF.setText(fileChooser.getSelectedFile().getAbsolutePath());
				}
			}
		});
		yesButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				yesNoCanceled[0]=true; yesNoCanceled[1]=yesNoCanceled[2]=false;
				dialog.setVisible(false);
			}
		});
		noButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				yesNoCanceled[1]=true; yesNoCanceled[0]=yesNoCanceled[2]=false;
				dialog.setVisible(false);
			}
		});
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				yesNoCanceled[2]=true; yesNoCanceled[0]=yesNoCanceled[1]=false;
				dialog.setVisible(false);
			}
		});

		//put everything into the dialog
		dialog.getRootPane().setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		dialog.setLayout(new GridBagLayout());
		GridBagConstraints gbConstraints = new GridBagConstraints();
		gbConstraints.anchor=GridBagConstraints.WEST;
		gbConstraints.gridwidth=GridBagConstraints.REMAINDER;
		gbConstraints.ipady=20;
		gbConstraints.ipadx=5;
		dialog.add(question,gbConstraints);
		gbConstraints.gridwidth=1;
		gbConstraints.ipady=5;	
		dialog.add(filenameLabel,gbConstraints);
		dialog.add(filenameTF,gbConstraints);
		dialog.add(Box.createHorizontalStrut(10),gbConstraints);
		gbConstraints.gridwidth=GridBagConstraints.REMAINDER;
		dialog.add(chooseButton,gbConstraints);
		dialog.add(rememberCB,gbConstraints);
		dialog.add(loadCB,gbConstraints);
		Box buttonBox = Box.createHorizontalBox();
		buttonBox.setBorder(BorderFactory.createEmptyBorder());
		buttonBox.add(yesButton);
		buttonBox.add(Box.createHorizontalStrut(10));
		buttonBox.add(noButton);
		buttonBox.add(Box.createHorizontalStrut(10));
		buttonBox.add(cancelButton);
		gbConstraints.gridwidth=GridBagConstraints.REMAINDER;
		gbConstraints.anchor=GridBagConstraints.EAST;
		dialog.add(buttonBox,gbConstraints);
	}


	/**
	 * @return false, when the user canceled the dialog.
	 */
	public boolean show() {
		yesNoCanceled[0]=yesNoCanceled[1]=false; yesNoCanceled[2]=true; 
		dialog.pack();
		dialog.setResizable(false);
		//see what we have got, ask again till we get a writable file or cancel.
		out=null;
		while (out == null) {
			dialog.setLocationByPlatform(true);
			dialog.setLocationRelativeTo(dialog.getParent());
			dialog.setVisible(true);
			if (yesNoCanceled[2])
				return false; // cancel pressed
			if (yesNoCanceled[1]) { // No pressed
				controller.setAskBeforeSaveOnExit(!rememberCB.isSelected());
				controller.setUserPropertyFile(filenameTF.getText());
				controller.setLoadFromUserPropertyFile(loadCB.isSelected());
				if (rememberCB.isSelected()) {
					controller.setSaveOnExit(false);
				}
				break;
			}
			assert yesNoCanceled[0]=true;
			//yes pressed
			controller.setAskBeforeSaveOnExit(!rememberCB.isSelected());
			controller.setUserPropertyFile(filenameTF.getText());
			controller.setLoadFromUserPropertyFile(loadCB.isSelected());
			if (rememberCB.isSelected()) {
				controller.setSaveOnExit(true);
			}

			File file = new File(filenameTF.getText());
			try {
				if (file != null && file.getParentFile()!=null && !file.getParentFile().exists()) {
					file.getParentFile().mkdirs();
				}
				out=new FileOutputStream(file);
			} catch (Exception e) {
				out=null;
				JOptionPane.showMessageDialog(dialog.getParent(), "Can not write to file: "+file, "", JOptionPane.ERROR_MESSAGE);
			}	
			yesNoCanceled[0]=yesNoCanceled[1]=false; yesNoCanceled[2]=true; 
		}
		return true;
	}


	public OutputStream getOutputStream() {
		return out;
	}

	public static class PropertiesFileFilter extends FileFilter {
		@Override
		public boolean accept(File f) {
			if (f.isDirectory()) {
				return true;
			}
			String name = f.getName().toLowerCase();
			return name.endsWith(".xml") || name.endsWith(".jrw");
		}

		@Override
		public String getDescription() {
			return "Property Files (*.xml, *.jrw)";
		}
		
	}

}
