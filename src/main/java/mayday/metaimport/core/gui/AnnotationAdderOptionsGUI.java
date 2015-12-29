package mayday.metaimport.core.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import mayday.core.gui.MaydayDialog;

import mayday.metaimport.core.AbstractAnnotationAdder;

@SuppressWarnings("serial")
public class AnnotationAdderOptionsGUI extends MaydayDialog {

	@SuppressWarnings("unchecked")
	// don't care about type
	protected AbstractAnnotationAdder adder;

	@SuppressWarnings("unchecked")
	public AnnotationAdderOptionsGUI(AbstractAnnotationAdder adder) {
		super();
		this.adder = adder;
	}

	public void init() {
		setLayout(new GridBagLayout());
		setTitle("Options for " + adder.getName());

		initAdderPanel();
		initButtons();
		
		// set the location - taken from some other part of Mayday
		initLocation();

		// set the size of the dialog
		setResizable(false);
		pack();
	}

	private void initAdderPanel() {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		gbc.weighty = .8;
		
		add(adder.getGUI(), gbc);
	}

	private void initButtons() {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.anchor = GridBagConstraints.LAST_LINE_END;
		gbc.weightx = 1.0;
		gbc.weighty = .2;
		
		JButton closeButton = new JButton("Close");
		closeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});
		add(closeButton, gbc);
	}

	/**
	 * Initializes the location to the middle of the screen.
	 * 
	 * taken from PluginManagerDialog
	 */
	private void initLocation() {
		// get the screen resolution
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();

		// compute the upper left corner of the dialog to center it on
		// the screen
		int x = (int) ((screen.width - getWidth()) * 0.5);
		int y = (int) ((screen.height - getHeight()) * 0.5);
		setLocation(x, y);
	}
	
}
