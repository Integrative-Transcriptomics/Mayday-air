package mayday.metaimport.core.id;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import mayday.core.meta.MIGroup;
import mayday.core.meta.MIGroupSelection;
import mayday.core.meta.MIManager;
import mayday.core.meta.MIType;
import mayday.core.meta.gui.MIGroupSelectionPanel;
import mayday.metaimport.core.id.gui.AbstractMIGroupModifierGUI;

/**
 * @author Michael Piechotta
 * @version $Revision: 1.2 $, $Date: 2014/11/19 13:46:37 $
 * 
 */
public class CustomMIGroupModifier extends AbstractMIGroupModifier {

	/**
	 * 
	 */
	protected MIGroup miGroup;

	/**
	 * @param idType
	 * @param miManager
	 */
	public CustomMIGroupModifier(MIManager miManager) {
		super(miManager);
	}

	@Override
	public String getMIPath4IdType(String idType) {
		if (miGroup!=null)
			return miGroup.getPath() + "/" + miGroup.getName(); // don't care about
		return "";
		// idType
	}

	@Override
	public MIGroup getMIGroup4IdType(String idType) {
		return miGroup; // don't care about idType
	}

	public void initGUI() {
		gui = new CustomMIGroupModifierGUI(this);
		gui.init();
	}

	/**
	 * 
	 * @param miGroup
	 */
	protected void setMIGroup(MIGroup miGroup) {
		this.miGroup = miGroup;
	}

	@SuppressWarnings("serial")
	private class CustomMIGroupModifierGUI extends AbstractMIGroupModifierGUI {

		/**
		 * 
		 */
		MIGroupSelectionPanel miGroupSelectPanel;

		/**
		 * 
		 */
		private JTextField customMIOPath;

		/**
		 * Constructor.
		 * 
		 * @param miGroupModifier
		 */
		public CustomMIGroupModifierGUI(CustomMIGroupModifier miGroupModifier) {
			super(miGroupModifier);
			miGroupSelectPanel = new MIGroupSelectionPanel(miGroupModifier
					.getMIManager());//, IdSettings.MIO_TYPE);
			customMIOPath = new JTextField();
		}

		@Override
		protected void initContent() {
			customMIOPath.setEditable(false);

			if (miGroup != null)
				customMIOPath.setText(miGroup.getPath() + "/"
						+ miGroup.getName());
			else
				customMIOPath.setText("");

			GridBagConstraints gbc = new GridBagConstraints();
			gbc.anchor = GridBagConstraints.FIRST_LINE_START;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.weightx = 1.0;
			gbc.gridwidth = GridBagConstraints.REMAINDER;

			gbc.weighty = .1;
			add(new JLabel("MIO MIPathUtilities"), gbc);

			gbc.weighty = .1;
			add(customMIOPath, gbc);
			
			gbc.weighty = .6;
			add(miGroupSelectPanel, gbc);
		}

		protected void initButtons() {
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.insets = new Insets(5, 5, 5, 5);
			gbc.gridwidth = GridBagConstraints.RELATIVE;
			gbc.anchor = GridBagConstraints.LAST_LINE_END;
			gbc.weightx = .5;
			gbc.weighty = .2;

			JButton closeButton = new JButton("Cancel");
			closeButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					setVisible(false);
				}
			});
			add(closeButton, gbc);

			JButton okButton = new JButton("Ok");
			okButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					MIGroup miGroupSelected = getSelectedMIGroup();

					// if nothing is selected and not miGroup is set - file an error message
					if (miGroupSelected == null && miGroup == null) {
						JOptionPane.showMessageDialog((JFrame)null,
								"Select only one MIGroup!",
								"MIGroup Selection Error",
								JOptionPane.ERROR_MESSAGE);
						return;
					}

					if(miGroupSelected != null) {
						((CustomMIGroupModifier) miGroupModifier)
								.setMIGroup(miGroupSelected);
						customMIOPath.setText(miGroupSelected.getPath() + "/"
								+ miGroupSelected.getName());
					}
					setVisible(false);
				}
			});
			add(okButton, gbc);
		}

		/**
		 * 
		 */
		public MIGroup getSelectedMIGroup() {
			MIGroupSelection<MIType> selection = miGroupSelectPanel
					.getSelection();
			if (selection != null && selection.size() == 1)
				return selection.get(0);

			return null;
		}

	}

}
